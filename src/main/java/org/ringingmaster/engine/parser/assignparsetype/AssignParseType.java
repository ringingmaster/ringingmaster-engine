package org.ringingmaster.engine.parser.assignparsetype;

import com.google.common.base.Strings;
import com.google.common.collect.HashBasedTable;
import org.ringingmaster.engine.arraytable.BackingTableLocationAndValue;
import org.ringingmaster.engine.arraytable.ImmutableArrayTable;
import org.ringingmaster.engine.composition.Composition;
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.notation.NotationCall;
import org.ringingmaster.engine.notation.NotationMethodCallingPosition;
import org.ringingmaster.engine.parser.cell.ParsedCell;
import org.ringingmaster.engine.parser.functions.BuildDefinitionsAdjacencyList;
import org.ringingmaster.engine.parser.functions.FollowTransitiveDefinitions;
import org.ringingmaster.engine.parser.parse.Parse;
import org.ringingmaster.engine.parser.parse.ParseBuilder;
import org.ringingmaster.engine.composition.cell.Cell;
import org.ringingmaster.engine.composition.checkingtype.CheckingType;
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
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.WHITESPACE;
import static org.ringingmaster.engine.composition.checkingtype.CheckingType.LEAD_BASED;
import static org.ringingmaster.engine.composition.tableaccess.DefinitionTableAccess.DEFINITION_COLUMN;

/**
 * Parses all cells and assigns a parse type where possible.
 * Does not include multipliers.
 *
 * @author stevelake
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

        log.debug("[{}] > assign parse type", composition.getTitle());

        final HashBasedTable<Integer, Integer, ParsedCell> parsedCompositionCells = HashBasedTable.create();
        parseCallPositionArea(composition, parsedCompositionCells);
        final Set<String> mainBodyDefinitions =
                parseMainBodyArea(composition, parsedCompositionCells);
        final Set<String> spliceAreaDefinitions =
                parseSpliceArea(composition, parsedCompositionCells);

        final HashBasedTable<Integer, Integer, ParsedCell> parsedDefinitionCells = HashBasedTable.create();
        parseDefinitionShorthandArea(composition, parsedDefinitionCells);
        parseDefinitionDefinitionArea(composition, parsedDefinitionCells, mainBodyDefinitions, spliceAreaDefinitions);


        //TODO should we allow variance in definitions?
        //TODO should we allow variance in splice?


        Parse parse = new ParseBuilder()
                .prototypeOf(composition)
                .setCompositionTableCells(parsedCompositionCells)
                .setDefinitionTableCells(parsedDefinitionCells)
                .build();

        log.debug("[{}] < assign parse type", parse.getComposition().getTitle());

        return parse;
    }

    private void parseDefinitionShorthandArea(Composition composition, HashBasedTable<Integer, Integer, ParsedCell> parsedDefinitionCells) {
        log.debug("[{}] Parse definition shorthand area", composition.getTitle());

        // This is a special parse - we just take trimmed versions of every definition and add it as a parse
        Set<LexerDefinition> lexerDefinitions = new HashSet<>();
        addDefinitionLexerDefinitions(composition, lexerDefinitions);
        addWhitespaceLexerDefinitions(lexerDefinitions);

        parse(parsedDefinitionCells, lexerDefinitions, composition.definitionShorthandCells(), (parsedCell) -> {}, composition.getTitle());
    }

    private void parseCallPositionArea(Composition composition, HashBasedTable<Integer, Integer, ParsedCell> parsedCells) {
        if (composition.getCheckingType() != CheckingType.COURSE_BASED) {
            return;
        }

        log.debug("[{}] Parse call position area", composition.getTitle());

        Set<LexerDefinition> lexerDefinitions = new HashSet<>();
        addCallingPositionLexerDefinitions(composition, lexerDefinitions);
        addWhitespaceLexerDefinitions(lexerDefinitions);

        parse(parsedCells, lexerDefinitions, composition.callPositionCells(), (parsedCell) -> {}, composition.getTitle());
    }

    private Set<String> parseMainBodyArea(Composition composition, HashBasedTable<Integer, Integer, ParsedCell> parsedCells) {
        log.debug("[{}] Parse main body area", composition.getTitle());

        Set<LexerDefinition> lexerDefinitions = buildMainBodyParseTokenMap(composition);

        Set<String> definitionsInUse = new HashSet<>();
        parse(parsedCells, lexerDefinitions, composition.mainBodyCells(), (parsedCell) ->
                parsedCell.allSections().stream()
                        .filter(section -> section.getParseType().equals(DEFINITION))
                        .map(parsedCell::getCharacters)
                        .forEach(definitionsInUse::add),
                composition.getTitle()
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
        addWhitespaceLexerDefinitions(lexerDefinitions);

        addDefinitionLexerDefinitions(composition, lexerDefinitions);
        return lexerDefinitions;
    }

    private Set<String> parseSpliceArea(Composition composition, HashBasedTable<Integer, Integer, ParsedCell> parsedCells) {
        if (!composition.isSpliced()) {
            return Collections.emptySet();
        }

        log.debug("[{}] Parse splice area", composition.getTitle());
        Set<LexerDefinition> lexerDefinitions = buildSpliceAreaParseTokenMap(composition);


        Set<String> definitionsInUse = new HashSet<>();
        parse(parsedCells, lexerDefinitions, composition.splicedCells(), (parsedCell) ->
                parsedCell.allSections().stream()
                    .filter(section -> section.getParseType().equals(DEFINITION))
                    .map(parsedCell::getCharacters)
                    .forEach(definitionsInUse::add),
                            composition.getTitle()
        );

        return definitionsInUse;
    }

    private Set<LexerDefinition> buildSpliceAreaParseTokenMap(Composition composition) {
        Set<LexerDefinition> lexerDefinitions = new HashSet<>();
        addSpliceLexerDefinitions(composition, lexerDefinitions);
        addMultiplierGroupLexerDefinitions(lexerDefinitions);
        addDefinitionLexerDefinitions(composition, lexerDefinitions);
        addWhitespaceLexerDefinitions(lexerDefinitions);
        addDefinitionLexerDefinitions(composition, lexerDefinitions);
        return lexerDefinitions;
    }

    private void parseDefinitionDefinitionArea(Composition composition, HashBasedTable<Integer, Integer, ParsedCell> parsedDefinitionCells, Set<String> mainBodyDefinitions, Set<String> spliceAreaDefinitions) {

        if (composition.getAllDefinitionShorthands().size() == 0) {
            return;
        }

        // Pass 1 - parse definitions
        Set<LexerDefinition> definitionMappings = buildDefinitionDefinitionTokenMap(composition);
        for (String shorthand : composition.getAllDefinitionShorthands()) {
            composition.findDefinitionByShorthand(shorthand).ifPresent(
                    (definitionTable) -> {
                        log.debug("[{}] Parsing definition with shorthand [{}] for definition regex's", composition.getTitle(), shorthand);
                        final ImmutableArrayTable<Cell> definitionCellAsTable = definitionTable.subTable(0, 1, DEFINITION_COLUMN, DEFINITION_COLUMN + 1);
                        parse(parsedDefinitionCells, definitionMappings, definitionCellAsTable, (parsedCell) -> {}, composition.getTitle());
                    }
            );
        }

        // Pass 2 - find transative definitions
        final Parse tempParseToFindAdjacensyList = new ParseBuilder()
                .prototypeOf(composition)
                .setCompositionTableCells(HashBasedTable.create())
                .setDefinitionTableCells(parsedDefinitionCells)
                .build();
        final Map<String, Set<String>> adjencency = new BuildDefinitionsAdjacencyList().apply(tempParseToFindAdjacensyList);

        Set<String> mainBodyDefinitionsWithTransative = new FollowTransitiveDefinitions().apply(mainBodyDefinitions, adjencency);
        Set<String> spliceAreaDefinitionsWithTransative = new FollowTransitiveDefinitions().apply(spliceAreaDefinitions, adjencency);

        // Pass 3 - parse other values
        Set<LexerDefinition> mainBodyParseTokenMappings = buildMainBodyParseTokenMap(composition);
        Set<LexerDefinition> spliceAreaParseTokenMappings = buildSpliceAreaParseTokenMap(composition);

        for (String shorthand : composition.getAllDefinitionShorthands()) {
            composition.findDefinitionByShorthand(shorthand).ifPresent(
                    (definitionTable) -> {
                        log.debug("[{}] Parsing definition with shorthand [{}] for non-definition regex's", composition.getTitle(), shorthand);

                        final ImmutableArrayTable<Cell> definitionCellAsTable = definitionTable.subTable(0, 1, DEFINITION_COLUMN, DEFINITION_COLUMN + 1);
                        // We only use splices mappings when token is not in main body but is in spliced.
                        Set<LexerDefinition> chosenMappings = (!mainBodyDefinitionsWithTransative.contains(shorthand))&&
                                spliceAreaDefinitionsWithTransative.contains(shorthand) ? spliceAreaParseTokenMappings : mainBodyParseTokenMappings;
                        parse(parsedDefinitionCells, chosenMappings, definitionCellAsTable, (parsedCell) -> {}, composition.getTitle());
                    }
            );
        }

    }

    private Set<LexerDefinition> buildDefinitionDefinitionTokenMap(Composition composition) {
        Set<LexerDefinition> lexerDefinitions = new HashSet<>();
        addDefinitionLexerDefinitions(composition, lexerDefinitions);
        return lexerDefinitions;
    }


    private void parse(HashBasedTable<Integer, Integer, ParsedCell> parsedCells, Set<LexerDefinition> lexerDefinitions, ImmutableArrayTable<Cell> cells, Consumer<ParsedCell> observer, String logPreamble) {
        for (BackingTableLocationAndValue<Cell> cellAndLocation : cells) {
            ParsedCell parsedCell = lexer.lexCell(cellAndLocation.getValue(), lexerDefinitions, logPreamble);
            observer.accept(parsedCell);
            parsedCells.put(cellAndLocation.getRow(), cellAndLocation.getCol(), parsedCell);
        }
    }


    private void addCallingPositionLexerDefinitions(Composition composition, Set<LexerDefinition> lexerDefinitions) {
        for (NotationBody notation : composition.getAvailableNotations()) {
            for (NotationMethodCallingPosition callingPosition : notation.getMethodBasedCallingPositions()) {
                lexerDefinitions.add(new LexerDefinition(PRECEDENCE_GENERAL, callingPosition.getName().length(),
                        callingPosition.getName(), CALLING_POSITION));
            }
        }
    }

    private void addPlainLeadLexerDefinitions(Composition composition, Set<LexerDefinition> lexerDefinitions) {
        if (composition.getCheckingType() == LEAD_BASED) {
            lexerDefinitions.add(new LexerDefinition(PRECEDENCE_GENERAL, composition.getPlainLeadToken().length(),
                    "(\\d*)(" + composition.getPlainLeadToken() + ")", PLAIN_LEAD_MULTIPLIER, PLAIN_LEAD));
        }
    }

    private void addCallLexerDefinitions(Composition composition, Set<LexerDefinition> lexerDefinitions) {
        for (NotationBody notation : composition.getAvailableNotations()) {
            for (NotationCall notationCall : notation.getCalls()) {
                lexerDefinitions.add(new LexerDefinition(PRECEDENCE_GENERAL, notationCall.getNameShorthand().length(),
                        "(\\d*)(" + notationCall.getNameShorthand() + ")", CALL_MULTIPLIER, CALL));
                lexerDefinitions.add(new LexerDefinition(PRECEDENCE_GENERAL, notationCall.getName().length(),
                        "(\\d*)(" + notationCall.getName() + ")", CALL_MULTIPLIER, CALL));
            }
        }
    }

    private void addSpliceLexerDefinitions(Composition composition, Set<LexerDefinition> lexerDefinitions) {
        for (NotationBody notation : composition.getAvailableNotations()) {
            if (!Strings.isNullOrEmpty(notation.getSpliceIdentifier())){
                lexerDefinitions.add(new LexerDefinition(PRECEDENCE_GENERAL, notation.getSpliceIdentifier().length(),
                        "(\\d*)(" + notation.getSpliceIdentifier() + ")", SPLICE_MULTIPLIER, SPLICE));
            }
            lexerDefinitions.add(new LexerDefinition(PRECEDENCE_GENERAL, notation.getName().length(),
                    "(\\d*)(" + notation.getName() + ")", SPLICE_MULTIPLIER, SPLICE));
            lexerDefinitions.add(new LexerDefinition(PRECEDENCE_GENERAL, notation.getNameIncludingNumberOfBells().length(),
                    "(\\d*)(" + notation.getNameIncludingNumberOfBells() + ")", SPLICE_MULTIPLIER, SPLICE));
        }
    }

    private void addDefinitionLexerDefinitions(Composition composition, Set<LexerDefinition> lexerDefinitions) {
        for (String shorthand : composition.getAllDefinitionShorthands()) {
            lexerDefinitions.add(new LexerDefinition(PRECEDENCE_DEFINITION, shorthand.length(),
                    "(\\d*)(" + shorthand + ")", DEFINITION_MULTIPLIER, DEFINITION));
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

    private void addWhitespaceLexerDefinitions(Set<LexerDefinition> lexerDefinitions) {
        lexerDefinitions.add(new LexerDefinition(PRECEDENCE_LOWEST, 0,
                "\\s", WHITESPACE));
    }
}
