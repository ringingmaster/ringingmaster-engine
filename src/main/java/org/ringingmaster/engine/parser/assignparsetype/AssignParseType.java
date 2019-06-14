package org.ringingmaster.engine.parser.assignparsetype;

import com.google.common.base.Strings;
import com.google.common.collect.HashBasedTable;
import org.ringingmaster.engine.arraytable.BackingTableLocationAndValue;
import org.ringingmaster.engine.arraytable.ImmutableArrayTable;
import org.ringingmaster.engine.composition.Composition;
import org.ringingmaster.engine.notation.Call;
import org.ringingmaster.engine.notation.CallingPosition;
import org.ringingmaster.engine.notation.Notation;
import org.ringingmaster.engine.parser.cell.ParsedCell;
import org.ringingmaster.engine.parser.definition.BuildDefinitionsAdjacencyList;
import org.ringingmaster.engine.parser.definition.FollowTransitiveDefinitions;
import org.ringingmaster.engine.parser.parse.Parse;
import org.ringingmaster.engine.parser.parse.ParseBuilder;
import org.ringingmaster.engine.composition.cell.Cell;
import org.ringingmaster.engine.composition.compositiontype.CompositionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.concurrent.Immutable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.ringingmaster.engine.compiler.variance.VarianceFactory.ODD_EVEN_REGEX;
import static org.ringingmaster.engine.compiler.variance.VarianceFactory.OMIT_INCLUDE_REGEX;
import static org.ringingmaster.engine.compiler.variance.VarianceFactory.SPECIFIED_PARTS_REGEX;
import static org.ringingmaster.engine.composition.tableaccess.DefinitionTableAccess.SHORTHAND_COLUMN;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.CALL;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.CALLING_POSITION;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.CALL_MULTIPLIER;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.DEFAULT_CALL_MULTIPLIER;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.DEFINITION;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.DEFINITION_MULTIPLIER;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.MULTIPLIER_GROUP_CLOSE;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.MULTIPLIER_GROUP_OPEN;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.MULTIPLIER_GROUP_OPEN_MULTIPLIER;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.PLAIN_LEAD;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.PLAIN_LEAD_MULTIPLIER;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.SPLICE;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.SPLICE_MULTIPLIER;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.VARIANCE_CLOSE;

import static org.ringingmaster.engine.composition.compositiontype.CompositionType.LEAD_BASED;
import static org.ringingmaster.engine.composition.tableaccess.DefinitionTableAccess.DEFINITION_COLUMN;

/**
 * Parses all cells and assigns a parse type where possible.
 * Does not include multipliers.
 *
 * @author Steve Lake
 */
@Immutable
public class AssignParseType implements Function<Composition, Parse> {

    private final Logger log = LoggerFactory.getLogger(AssignParseType.class);

    // This defines the precedence order for the different types we are parsing.
    // The higher numbers get processed first

    // We want the definition to win.
    // This is necessary because in parseDefinitionDefinitionArea we parse the
    // definitions first on their own and we want the same behaviour.
    private static final int PRECEDENCE_DEFINITION = 10;
    private static final int PRECEDENCE_BRACE = 9;
    private static final int PRECEDENCE_GENERAL = 5;
    private static final int PRECEDENCE_LOWEST = 0;

    private final CellLexer lexer = new CellLexer();

    public Parse apply(Composition composition) {

        log.debug("[{}] > assign parse type", composition.getLoggingTag());

        final HashBasedTable<Integer, Integer, ParsedCell> parsedCompositionCells = HashBasedTable.create();
        parseNullArea(composition, parsedCompositionCells);
        parseCallingPositionArea(composition, parsedCompositionCells);
        final Set<String> mainBodyDefinitions = parseMainBodyArea(composition, parsedCompositionCells);
        final Set<String> spliceAreaDefinitions = parseSpliceArea(composition, parsedCompositionCells);

        final HashBasedTable<Integer, Integer, ParsedCell> parsedDefinitionCells = HashBasedTable.create();
        parseDefinitionShorthandArea(composition, parsedDefinitionCells);
        parseDefinitionDefinitionArea(composition, parsedDefinitionCells, mainBodyDefinitions, spliceAreaDefinitions);

        //TODO should we allow variance in splice?

        Parse parse = new ParseBuilder()
                .prototypeOf(composition)
                .setCompositionTableCells(parsedCompositionCells)
                .setDefinitionTableCells(parsedDefinitionCells)
                .build();

        log.debug("[{}] < assign parse type", parse.getComposition().getLoggingTag());

        return parse;
    }

    private void parseNullArea(Composition composition, HashBasedTable<Integer, Integer, ParsedCell> parsedCells) {
        if (composition.getCompositionType() != CompositionType.COURSE_BASED) {
            return;
        }

        log.debug("[{}] Parse null area (unused top right cell when both spliced and course based)", composition.getLoggingTag());

        Set<LexerDefinition> lexerDefinitions = Collections.emptySet();

        parse(parsedCells, lexerDefinitions, composition.nullAreaCells(), (parsedCell) -> {}, composition.getLoggingTag());
    }

    private void parseCallingPositionArea(Composition composition, HashBasedTable<Integer, Integer, ParsedCell> parsedCells) {
        if (composition.getCompositionType() != CompositionType.COURSE_BASED) {
            return;
        }

        log.debug("[{}] Parse call position area", composition.getLoggingTag());

        Set<LexerDefinition> lexerDefinitions = buildCallingPositionParseTokenMap(composition);

        parse(parsedCells, lexerDefinitions, composition.callingPositionCells(), (parsedCell) -> {}, composition.getLoggingTag());
    }

    private Set<LexerDefinition> buildCallingPositionParseTokenMap(Composition composition) {
        Set<LexerDefinition> lexerDefinitions = new HashSet<>();
        addCallingPositionLexerDefinitions(composition, lexerDefinitions);
        return lexerDefinitions;
    }

    private Set<String> parseMainBodyArea(Composition composition, HashBasedTable<Integer, Integer, ParsedCell> parsedCells) {
        log.debug("[{}] Parse main body area", composition.getLoggingTag());

        Set<LexerDefinition> lexerDefinitions = buildMainBodyParseTokenMap(composition);

        Set<String> definitionsInUse = new HashSet<>();
        parse(parsedCells, lexerDefinitions, composition.mainBodyCells(), (parsedCell) ->
                parsedCell.allSections().stream()
                        .filter(section -> section.getParseType().equals(DEFINITION))
                        .map(parsedCell::getCharacters)
                        .forEach(definitionsInUse::add),
                composition.getLoggingTag()
        );

        return definitionsInUse;
    }

    private Set<LexerDefinition> buildMainBodyParseTokenMap(Composition composition) {
        Set<LexerDefinition> lexerDefinitions = new HashSet<>();
        addCallLexerDefinitions(composition, lexerDefinitions);
        addPlainLeadLexerDefinitions(composition, lexerDefinitions);
        addVarianceLexerDefinitions(lexerDefinitions);
        addMultiplierGroupLexerDefinitions(lexerDefinitions);
        addDefaultCallLexerDefinitions(lexerDefinitions);

        addDefinitionLexerDefinitions(composition, lexerDefinitions);
        return lexerDefinitions;
    }

    private Set<String> parseSpliceArea(Composition composition, HashBasedTable<Integer, Integer, ParsedCell> parsedCells) {
        if (!composition.isSpliced()) {
            return Collections.emptySet();
        }

        log.debug("[{}] Parse splice area", composition.getLoggingTag());
        Set<LexerDefinition> lexerDefinitions = buildSpliceAreaParseTokenMap(composition);


        Set<String> definitionsInUse = new HashSet<>();
        parse(parsedCells, lexerDefinitions, composition.splicedCells(), (parsedCell) ->
                parsedCell.allSections().stream()
                    .filter(section -> section.getParseType().equals(DEFINITION))
                    .map(parsedCell::getCharacters)
                    .forEach(definitionsInUse::add),
                            composition.getLoggingTag()
        );

        return definitionsInUse;
    }

    private Set<LexerDefinition> buildSpliceAreaParseTokenMap(Composition composition) {
        Set<LexerDefinition> lexerDefinitions = new HashSet<>();
        addSpliceLexerDefinitions(composition, lexerDefinitions);
        addMultiplierGroupLexerDefinitions(lexerDefinitions);
        addDefinitionLexerDefinitions(composition, lexerDefinitions);
        addDefinitionLexerDefinitions(composition, lexerDefinitions);
        return lexerDefinitions;
    }

    private void parseDefinitionShorthandArea(Composition composition, HashBasedTable<Integer, Integer, ParsedCell> parsedDefinitionCells) {
        log.debug("[{}] Parse definition shorthand area", composition.getLoggingTag());

        // This is a special parse - we just take trimmed versions of every definition and add it as a parse
        Set<LexerDefinition> lexerDefinitions = new HashSet<>();
        addDefinitionLexerDefinitions(composition, lexerDefinitions);

        parse(parsedDefinitionCells, lexerDefinitions, composition.definitionShorthandCells(), (parsedCell) -> {}, composition.getLoggingTag());
    }

    private void parseDefinitionDefinitionArea(Composition composition, HashBasedTable<Integer, Integer, ParsedCell> parsedDefinitionCells, Set<String> mainBodyDefinitions, Set<String> spliceAreaDefinitions) {

        if (composition.getAllDefinitionShorthands().size() == 0) {
            return;
        }

        // Pass 1 - parse definition area for definition ParseType's
        Set<LexerDefinition> definitionMappings = buildDefinitionDefinitionTokenMap(composition);
        for (ImmutableArrayTable<Cell> definitionTable : composition.getDefinitionAsTables()) {
            if (definitionTable.getColumnSize() == 2) { //Can  be one when only shorthand entered
                final ImmutableArrayTable<Cell> definitionCellAsTable = definitionTable.subTable(0, 1, DEFINITION_COLUMN, DEFINITION_COLUMN + 1);
                String shorthand = definitionCellAsTable.get(0, SHORTHAND_COLUMN).getCharacters();
                log.debug("[{}] Parsing definition with shorthand [{}] for definition regex's", composition.getLoggingTag(), shorthand);
                parse(parsedDefinitionCells, definitionMappings, definitionCellAsTable, parsedCell -> {}, composition.getLoggingTag());
            }
        }

        // Pass 2 - build adjacency list of transitive definitions for main and spliced
        final Parse tempParseToFindAdjacencyList = new ParseBuilder()
                .prototypeOf(composition)
                .setCompositionTableCells(HashBasedTable.create())
                .setDefinitionTableCells(parsedDefinitionCells)
                .build();
        final Map<String, Set<String>> adjacency = new BuildDefinitionsAdjacencyList().apply(tempParseToFindAdjacencyList);

        Set<String> mainBodyDefinitionsWithTransitive = new FollowTransitiveDefinitions().apply(mainBodyDefinitions, adjacency);
        Set<String> spliceAreaDefinitionsWithTransitive = new FollowTransitiveDefinitions().apply(spliceAreaDefinitions, adjacency);

        // Pass 3 - parse definition area for the other (non definition) ParseType's
        Set<LexerDefinition> mainBodyParseTokenMappings = buildMainBodyParseTokenMap(composition);
        Set<LexerDefinition> spliceAreaParseTokenMappings = buildSpliceAreaParseTokenMap(composition);

        for (ImmutableArrayTable<Cell> definitionTable : composition.getDefinitionAsTables()) {
            if (definitionTable.getColumnSize() == 2) { //Can  be one when only shorthand entered
                final ImmutableArrayTable<Cell> definitionCellAsTable = definitionTable.subTable(0, 1, DEFINITION_COLUMN, DEFINITION_COLUMN + 1);
                String shorthand = definitionTable.get(0, SHORTHAND_COLUMN).getCharacters();
                log.debug("[{}] Parsing definition with shorthand [{}] for non-definition regex's", composition.getLoggingTag(), shorthand);

                // We only use splices mappings when token is not in main body but is in spliced.
                Set<LexerDefinition> chosenMappings = (!mainBodyDefinitionsWithTransitive.contains(shorthand)) &&
                        spliceAreaDefinitionsWithTransitive.contains(shorthand) ? spliceAreaParseTokenMappings : mainBodyParseTokenMappings;
                parse(parsedDefinitionCells, chosenMappings, definitionCellAsTable, (parsedCell) -> {
                }, composition.getLoggingTag());
            }
        }

    }

    private Set<LexerDefinition> buildDefinitionDefinitionTokenMap(Composition composition) {
        Set<LexerDefinition> lexerDefinitions = new HashSet<>();
        addDefinitionLexerDefinitions(composition, lexerDefinitions);
        return lexerDefinitions;
    }


    private void parse(HashBasedTable<Integer, Integer, ParsedCell> targetParsedCells, Set<LexerDefinition> lexerDefinitions,
                       ImmutableArrayTable<Cell> cells, Consumer<ParsedCell> observer, String logPreamble) {
        for (BackingTableLocationAndValue<Cell> cellAndLocation : cells) {
            ParsedCell parsedCell = lexer.lexCell(cellAndLocation.getValue(), lexerDefinitions, logPreamble);
            observer.accept(parsedCell);
            targetParsedCells.put(cellAndLocation.getRow(), cellAndLocation.getCol(), parsedCell);
        }
    }


    private void addCallingPositionLexerDefinitions(Composition composition, Set<LexerDefinition> lexerDefinitions) {
        for (Notation notation : composition.getAvailableNotations()) {
            for (CallingPosition callingPosition : notation.getCallingPositions()) {
                lexerDefinitions.add(new LexerDefinition(PRECEDENCE_GENERAL, callingPosition.getName().length(),
                        "\\Q" + callingPosition.getName() + "\\E", CALLING_POSITION));
            }
        }
    }

    private void addPlainLeadLexerDefinitions(Composition composition, Set<LexerDefinition> lexerDefinitions) {
        if (composition.getCompositionType() == LEAD_BASED) {
            lexerDefinitions.add(new LexerDefinition(PRECEDENCE_GENERAL, composition.getPlainLeadToken().length(),
                    "(\\d*)(\\Q" + composition.getPlainLeadToken() + "\\E)", PLAIN_LEAD_MULTIPLIER, PLAIN_LEAD));
        }
    }

    private void addCallLexerDefinitions(Composition composition, Set<LexerDefinition> lexerDefinitions) {
        for (Notation notation : composition.getAvailableNotations()) {
            for (Call call : notation.getCalls()) {
                lexerDefinitions.add(new LexerDefinition(PRECEDENCE_GENERAL, call.getNameShorthand().length(),
                        "(\\d*)(\\Q" + call.getNameShorthand() + "\\E)", CALL_MULTIPLIER, CALL));
                lexerDefinitions.add(new LexerDefinition(PRECEDENCE_GENERAL, call.getName().length(),
                        "(\\d*)(\\Q" + call.getName() + "\\E)", CALL_MULTIPLIER, CALL));
            }
        }
    }

    private void addSpliceLexerDefinitions(Composition composition, Set<LexerDefinition> lexerDefinitions) {
        for (Notation notation : composition.getAvailableNotations()) {
            if (!Strings.isNullOrEmpty(notation.getSpliceIdentifier())){
                lexerDefinitions.add(new LexerDefinition(PRECEDENCE_GENERAL, notation.getSpliceIdentifier().length(),
                        "(\\d*)(\\Q" + notation.getSpliceIdentifier() + "\\E)", SPLICE_MULTIPLIER, SPLICE));
            }
            lexerDefinitions.add(new LexerDefinition(PRECEDENCE_GENERAL, notation.getName().length(),
                    "(\\d*)(\\Q" + notation.getName() + "\\E)", SPLICE_MULTIPLIER, SPLICE));
            lexerDefinitions.add(new LexerDefinition(PRECEDENCE_GENERAL, notation.getNameIncludingNumberOfBells().length(),
                    "(\\d*)(\\Q" + notation.getNameIncludingNumberOfBells() + "\\E)", SPLICE_MULTIPLIER, SPLICE));
        }
    }

    private void addDefinitionLexerDefinitions(Composition composition, Set<LexerDefinition> lexerDefinitions) {
        for (String shorthand : composition.getAllDefinitionShorthands()) {
            lexerDefinitions.add(new LexerDefinition(PRECEDENCE_DEFINITION, shorthand.length(),
                    "(\\d*)(\\Q" + shorthand + "\\E)", DEFINITION_MULTIPLIER, DEFINITION));
        }
    }

    //NOTE: This is closely related to the regex in BuildVarianceLookupByName::parseVariance
    private void addVarianceLexerDefinitions(Set<LexerDefinition> lexerDefinitions) { //TODO ensure these chars cant appear anywhere else. i.e.in calls method names Etc
        lexerDefinitions.add(new LexerDefinition(PRECEDENCE_BRACE, 0,
                "(?i)(\\[)((?:" + OMIT_INCLUDE_REGEX + ")(?:" + ODD_EVEN_REGEX + "|"+ SPECIFIED_PARTS_REGEX + "+))", ParseType.VARIANCE_OPEN, ParseType.VARIANCE_DETAIL));
        lexerDefinitions.add(new LexerDefinition(PRECEDENCE_BRACE, 0,
                "\\]", VARIANCE_CLOSE));
    }

    private void addMultiplierGroupLexerDefinitions(Set<LexerDefinition> lexerDefinitions) { //TODO ensure these chars cant appear anywhere else. i.e.in calls method names Etc
        lexerDefinitions.add(new LexerDefinition(PRECEDENCE_BRACE, 0,
                "(\\d*)(\\()", MULTIPLIER_GROUP_OPEN_MULTIPLIER, MULTIPLIER_GROUP_OPEN));
        lexerDefinitions.add(new LexerDefinition(PRECEDENCE_BRACE, 0,
                "\\)", MULTIPLIER_GROUP_CLOSE));
    }


    private void addDefaultCallLexerDefinitions(Set<LexerDefinition> lexerDefinitions) {
        lexerDefinitions.add(new LexerDefinition(PRECEDENCE_LOWEST, 0,
                "\\d+", DEFAULT_CALL_MULTIPLIER));
    }
}
