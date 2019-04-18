package org.ringingmaster.engine.parser.assignparsetype;

import com.google.common.base.Strings;
import com.google.common.collect.HashBasedTable;
import org.ringingmaster.engine.arraytable.BackingTableLocationAndValue;
import org.ringingmaster.engine.arraytable.ImmutableArrayTable;
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.notation.NotationCall;
import org.ringingmaster.engine.notation.NotationMethodCallingPosition;
import org.ringingmaster.engine.parser.cell.ParsedCell;
import org.ringingmaster.engine.parser.functions.BuildDefinitionsAdjacencyList;
import org.ringingmaster.engine.parser.functions.FollowTransitiveDefinitions;
import org.ringingmaster.engine.parser.parse.Parse;
import org.ringingmaster.engine.parser.parse.ParseBuilder;
import org.ringingmaster.engine.touch.Touch;
import org.ringingmaster.engine.touch.cell.Cell;
import org.ringingmaster.engine.touch.checkingtype.CheckingType;
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
import static org.ringingmaster.engine.touch.checkingtype.CheckingType.LEAD_BASED;
import static org.ringingmaster.engine.touch.tableaccess.DefinitionTableAccess.DEFINITION_COLUMN;

/**
 * Parses all cells and assigns a parse type where possible.
 * Does not include multipliers.
 *
 * @author stevelake
 */
@Immutable
public class AssignParseType implements Function<Touch, Parse> {

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

    public Parse apply(Touch touch) {

        log.debug("[{}] > assign parse type", touch.getTitle());

        final HashBasedTable<Integer, Integer, ParsedCell> parsedTouchCells = HashBasedTable.create();
        parseCallPositionArea(touch, parsedTouchCells);
        final Set<String> mainBodyDefinitions =
                parseMainBodyArea(touch, parsedTouchCells);
        final Set<String> spliceAreaDefinitions =
                parseSpliceArea(touch, parsedTouchCells);

        final HashBasedTable<Integer, Integer, ParsedCell> parsedDefinitionCells = HashBasedTable.create();
        parseDefinitionShorthandArea(touch, parsedDefinitionCells);
        parseDefinitionDefinitionArea(touch, parsedDefinitionCells, mainBodyDefinitions, spliceAreaDefinitions);


        //TODO should we allow variance in definitions?
        //TODO should we allow variance in splice?


        Parse parse = new ParseBuilder()
                .prototypeOf(touch)
                .setTouchTableCells(parsedTouchCells)
                .setDefinitionTableCells(parsedDefinitionCells)
                .build();

        log.debug("[{}] < assign parse type", parse.getTouch().getTitle());

        return parse;
    }

    private void parseDefinitionShorthandArea(Touch touch, HashBasedTable<Integer, Integer, ParsedCell> parsedDefinitionCells) {
        log.debug("[{}] Parse definition shorthand area", touch.getTitle());

        // This is a special parse - we just take trimmed versions of every definition and add it as a parse
        Set<LexerDefinition> lexerDefinitions = new HashSet<>();
        addDefinitionLexerDefinitions(touch, lexerDefinitions);
        addWhitespaceLexerDefinitions(lexerDefinitions);

        parse(parsedDefinitionCells, lexerDefinitions, touch.definitionShorthandCells(), (parsedCell) -> {}, touch.getTitle());
    }

    private void parseCallPositionArea(Touch touch, HashBasedTable<Integer, Integer, ParsedCell> parsedCells) {
        if (touch.getCheckingType() != CheckingType.COURSE_BASED) {
            return;
        }

        log.debug("[{}] Parse call position area", touch.getTitle());

        Set<LexerDefinition> lexerDefinitions = new HashSet<>();
        addCallingPositionLexerDefinitions(touch, lexerDefinitions);
        addWhitespaceLexerDefinitions(lexerDefinitions);

        parse(parsedCells, lexerDefinitions, touch.callPositionCells(), (parsedCell) -> {}, touch.getTitle());
    }

    private Set<String> parseMainBodyArea(Touch touch, HashBasedTable<Integer, Integer, ParsedCell> parsedCells) {
        log.debug("[{}] Parse main body area", touch.getTitle());

        Set<LexerDefinition> lexerDefinitions = buildMainBodyParseTokenMap(touch);

        Set<String> definitionsInUse = new HashSet<>();
        parse(parsedCells, lexerDefinitions, touch.mainBodyCells(), (parsedCell) ->
                parsedCell.allSections().stream()
                        .filter(section -> section.getParseType().equals(DEFINITION))
                        .map(parsedCell::getCharacters)
                        .forEach(definitionsInUse::add),
                touch.getTitle()
        );

        return definitionsInUse;
    }

    private Set<LexerDefinition> buildMainBodyParseTokenMap(Touch touch) {
        Set<LexerDefinition> lexerDefinitions = new HashSet<>();
        addCallLexerDefinitions(touch, lexerDefinitions);
        addPlainLeadLexerDefinitions(touch, lexerDefinitions);
        addVarianceLexerDefinitions(lexerDefinitions);
        addMultiplierGroupLexerDefinitions(lexerDefinitions);
        addDefaultCallLexerDefinitions(lexerDefinitions);
        addWhitespaceLexerDefinitions(lexerDefinitions);

        addDefinitionLexerDefinitions(touch, lexerDefinitions);
        return lexerDefinitions;
    }

    private Set<String> parseSpliceArea(Touch touch, HashBasedTable<Integer, Integer, ParsedCell> parsedCells) {
        if (!touch.isSpliced()) {
            return Collections.emptySet();
        }

        log.debug("[{}] Parse splice area", touch.getTitle());
        Set<LexerDefinition> lexerDefinitions = buildSpliceAreaParseTokenMap(touch);


        Set<String> definitionsInUse = new HashSet<>();
        parse(parsedCells, lexerDefinitions, touch.splicedCells(), (parsedCell) ->
                parsedCell.allSections().stream()
                    .filter(section -> section.getParseType().equals(DEFINITION))
                    .map(parsedCell::getCharacters)
                    .forEach(definitionsInUse::add),
                            touch.getTitle()
        );

        return definitionsInUse;
    }

    private Set<LexerDefinition> buildSpliceAreaParseTokenMap(Touch touch) {
        Set<LexerDefinition> lexerDefinitions = new HashSet<>();
        addSpliceLexerDefinitions(touch, lexerDefinitions);
        addMultiplierGroupLexerDefinitions(lexerDefinitions);
        addDefinitionLexerDefinitions(touch, lexerDefinitions);
        addWhitespaceLexerDefinitions(lexerDefinitions);
        addDefinitionLexerDefinitions(touch, lexerDefinitions);
        return lexerDefinitions;
    }

    private void parseDefinitionDefinitionArea(Touch touch, HashBasedTable<Integer, Integer, ParsedCell> parsedDefinitionCells, Set<String> mainBodyDefinitions, Set<String> spliceAreaDefinitions) {

        if (touch.getAllDefinitionShorthands().size() == 0) {
            return;
        }

        // Pass 1 - parse definitions
        Set<LexerDefinition> definitionMappings = buildDefinitionDefinitionTokenMap(touch);
        for (String shorthand : touch.getAllDefinitionShorthands()) {
            touch.findDefinitionByShorthand(shorthand).ifPresent(
                    (definitionTable) -> {
                        log.debug("[{}] Parsing definition with shorthand [{}] for definition regex's", touch.getTitle(), shorthand);
                        final ImmutableArrayTable<Cell> definitionCellAsTable = definitionTable.subTable(0, 1, DEFINITION_COLUMN, DEFINITION_COLUMN + 1);
                        parse(parsedDefinitionCells, definitionMappings, definitionCellAsTable, (parsedCell) -> {}, touch.getTitle());
                    }
            );
        }

        // Pass 2 - find transative definitions
        final Parse tempParseToFindAdjacensyList = new ParseBuilder()
                .prototypeOf(touch)
                .setTouchTableCells(HashBasedTable.create())
                .setDefinitionTableCells(parsedDefinitionCells)
                .build();
        final Map<String, Set<String>> adjencency = new BuildDefinitionsAdjacencyList().apply(tempParseToFindAdjacensyList);

        Set<String> mainBodyDefinitionsWithTransative = new FollowTransitiveDefinitions().apply(mainBodyDefinitions, adjencency);
        Set<String> spliceAreaDefinitionsWithTransative = new FollowTransitiveDefinitions().apply(spliceAreaDefinitions, adjencency);

        // Pass 3 - parse other values
        Set<LexerDefinition> mainBodyParseTokenMappings = buildMainBodyParseTokenMap(touch);
        Set<LexerDefinition> spliceAreaParseTokenMappings = buildSpliceAreaParseTokenMap(touch);

        for (String shorthand : touch.getAllDefinitionShorthands()) {
            touch.findDefinitionByShorthand(shorthand).ifPresent(
                    (definitionTable) -> {
                        log.debug("[{}] Parsing definition with shorthand [{}] for non-definition regex's", touch.getTitle(), shorthand);

                        final ImmutableArrayTable<Cell> definitionCellAsTable = definitionTable.subTable(0, 1, DEFINITION_COLUMN, DEFINITION_COLUMN + 1);
                        // We only use splices mappings when token is not in main body but is in spliced.
                        Set<LexerDefinition> chosenMappings = (!mainBodyDefinitionsWithTransative.contains(shorthand))&&
                                spliceAreaDefinitionsWithTransative.contains(shorthand) ? spliceAreaParseTokenMappings : mainBodyParseTokenMappings;
                        parse(parsedDefinitionCells, chosenMappings, definitionCellAsTable, (parsedCell) -> {}, touch.getTitle());
                    }
            );
        }

    }

    private Set<LexerDefinition> buildDefinitionDefinitionTokenMap(Touch touch) {
        Set<LexerDefinition> lexerDefinitions = new HashSet<>();
        addDefinitionLexerDefinitions(touch, lexerDefinitions);
        return lexerDefinitions;
    }


    private void parse(HashBasedTable<Integer, Integer, ParsedCell> parsedCells, Set<LexerDefinition> lexerDefinitions, ImmutableArrayTable<Cell> cells, Consumer<ParsedCell> observer, String logPreamble) {
        for (BackingTableLocationAndValue<Cell> cellAndLocation : cells) {
            ParsedCell parsedCell = lexer.lexCell(cellAndLocation.getValue(), lexerDefinitions, logPreamble);
            observer.accept(parsedCell);
            parsedCells.put(cellAndLocation.getRow(), cellAndLocation.getCol(), parsedCell);
        }
    }


    private void addCallingPositionLexerDefinitions(Touch touch, Set<LexerDefinition> lexerDefinitions) {
        for (NotationBody notation : touch.getAvailableNotations()) {
            for (NotationMethodCallingPosition callingPosition : notation.getMethodBasedCallingPositions()) {
                lexerDefinitions.add(new LexerDefinition(PRECEDENCE_GENERAL, callingPosition.getName().length(),
                        callingPosition.getName(), CALLING_POSITION));
            }
        }
    }

    private void addPlainLeadLexerDefinitions(Touch touch, Set<LexerDefinition> lexerDefinitions) {
        if (touch.getCheckingType() == LEAD_BASED) {
            lexerDefinitions.add(new LexerDefinition(PRECEDENCE_GENERAL, touch.getPlainLeadToken().length(),
                    "(\\d*)(" + touch.getPlainLeadToken() + ")", PLAIN_LEAD_MULTIPLIER, PLAIN_LEAD));
        }
    }

    private void addCallLexerDefinitions(Touch touch, Set<LexerDefinition> lexerDefinitions) {
        for (NotationBody notation : touch.getAvailableNotations()) {
            for (NotationCall notationCall : notation.getCalls()) {
                lexerDefinitions.add(new LexerDefinition(PRECEDENCE_GENERAL, notationCall.getNameShorthand().length(),
                        "(\\d*)(" + notationCall.getNameShorthand() + ")", CALL_MULTIPLIER, CALL));
                lexerDefinitions.add(new LexerDefinition(PRECEDENCE_GENERAL, notationCall.getName().length(),
                        "(\\d*)(" + notationCall.getName() + ")", CALL_MULTIPLIER, CALL));
            }
        }
    }

    private void addSpliceLexerDefinitions(Touch touch, Set<LexerDefinition> lexerDefinitions) {
        for (NotationBody notation : touch.getAvailableNotations()) {
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

    private void addDefinitionLexerDefinitions(Touch touch, Set<LexerDefinition> lexerDefinitions) {
        for (String shorthand : touch.getAllDefinitionShorthands()) {
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
