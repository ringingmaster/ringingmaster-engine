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

import static org.ringingmaster.engine.touch.tableaccess.DefinitionTableAccess.DEFINITION_COLUMN;

/**
 * Parses all cells and assigns a parse type where possible
 *
 * @author stevelake
 */
@Immutable
public class AssignParseType implements Function<Touch, Parse> {

    private final Logger log = LoggerFactory.getLogger(AssignParseType.class);

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


        Parse parse = new ParseBuilder()
                .prototypeOf(touch)
                .setTouchTableCells(parsedTouchCells)
                .setDefinitionTableCells(parsedDefinitionCells)
                .build();

        log.debug("[{}] < assign parse type", parse.getUnderlyingTouch().getTitle());

        return parse;
    }

    private void parseDefinitionShorthandArea(Touch touch, HashBasedTable<Integer, Integer, ParsedCell> parsedDefinitionCells) {
        // This is a special parse - we just take trimmed versions of every definition and add it as a parse
        Set<ParseDefinition> parseTokenMappings = new HashSet<>();
        addDefinitionTokens(touch, parseTokenMappings);
        addWhitespaceTokens(parseTokenMappings);

        parse(parsedDefinitionCells, parseTokenMappings, touch.definitionShorthandCells(), (parsedCell) -> {});
    }

    private void parseCallPositionArea(Touch touch, HashBasedTable<Integer, Integer, ParsedCell> parsedCells) {
        if (touch.getCheckingType() != CheckingType.COURSE_BASED) {
            return;
        }
        Set<ParseDefinition> parseTokenMappings = new HashSet<>();
        addCallingPositionTokens(touch, parseTokenMappings);
        addWhitespaceTokens(parseTokenMappings);

        parse(parsedCells, parseTokenMappings, touch.callPositionCells(), (parsedCell) -> {});
    }

    private Set<String> parseMainBodyArea(Touch touch, HashBasedTable<Integer, Integer, ParsedCell> parsedCells) {
        Set<ParseDefinition> parseTokenMappings = buildMainBodyParseTokenMap(touch);

        Set<String> definitionsInUse = new HashSet<>();
        parse(parsedCells, parseTokenMappings, touch.mainBodyCells(), (parsedCell) ->
                parsedCell.allSections().stream()
                        .filter(section -> section.getParseType().equals(ParseType.DEFINITION))
                        .map(parsedCell::getCharacters)
                        .forEach(definitionsInUse::add)
        );

        return definitionsInUse;
    }

    private Set<ParseDefinition> buildMainBodyParseTokenMap(Touch touch) {
        Set<ParseDefinition> parseTokenMappings = new HashSet<>();
        addCallTokens(touch, parseTokenMappings);
        addPlainLeadToken(touch, parseTokenMappings);
        addVarianceTokens(parseTokenMappings);
        addMultiplierGroupTokens(parseTokenMappings);
        addWhitespaceTokens(parseTokenMappings);
        // We add definitions last so if there is a namespace clash, the definition wins.
        // This is necessary because in parseDefinitionDefinitionArea we parse the definitions first on
        // their own and we want the same behaviour.
        addDefinitionTokens(touch, parseTokenMappings);
        return parseTokenMappings;
    }

    private Set<String> parseSpliceArea(Touch touch, HashBasedTable<Integer, Integer, ParsedCell> parsedCells) {
        if (!touch.isSpliced()) {
            return Collections.emptySet();
        }
        Set<ParseDefinition> parseTokenMappings = buildSpliceAreaParseTokenMap(touch);

        Set<String> definitionsInUse = new HashSet<>();
        parse(parsedCells, parseTokenMappings, touch.splicedCells(), (parsedCell) ->
                parsedCell.allSections().stream()
                    .filter(section -> section.getParseType().equals(ParseType.DEFINITION))
                    .map(parsedCell::getCharacters)
                    .forEach(definitionsInUse::add)
        );

        return definitionsInUse;
    }

    private Set<ParseDefinition> buildSpliceAreaParseTokenMap(Touch touch) {
        Set<ParseDefinition> parseTokenMappings = new HashSet<>();
        addSpliceTokens(touch, parseTokenMappings);
        addMultiplierGroupTokens(parseTokenMappings);
        addDefinitionTokens(touch, parseTokenMappings);
        addWhitespaceTokens(parseTokenMappings);
        // We add definitions last so if there is a namespace clash, the definition wins.
        // This is necessary because in parseDefinitionDefinitionArea we parse the definitions first on
        // their own and we want the same behaviour.
        addDefinitionTokens(touch, parseTokenMappings);
        return parseTokenMappings;
    }

    private void parseDefinitionDefinitionArea(Touch touch, HashBasedTable<Integer, Integer, ParsedCell> parsedDefinitionCells, Set<String> mainBodyDefinitions, Set<String> spliceAreaDefinitions) {

        if (touch.getAllDefinitionShorthands().size() == 0) {
            return;
        }

        // Pass 1 - parse definitions
        Set<ParseDefinition> definitionMappings = buildDefinitionDefinitionTokenMap(touch);
        for (String shorthand : touch.getAllDefinitionShorthands()) {
            touch.findDefinitionByShorthand(shorthand).ifPresent(
                    (definitionTable) -> {
                        final ImmutableArrayTable<Cell> definitionCellAsTable = definitionTable.subTable(0, 1, DEFINITION_COLUMN, DEFINITION_COLUMN + 1);
                        parse(parsedDefinitionCells, definitionMappings, definitionCellAsTable, (parsedCell) -> {});
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
        Set<ParseDefinition> mainBodyParseTokenMappings = buildMainBodyParseTokenMap(touch);
        Set<ParseDefinition> spliceAreaParseTokenMappings = buildSpliceAreaParseTokenMap(touch);

        for (String shorthand : touch.getAllDefinitionShorthands()) {
            touch.findDefinitionByShorthand(shorthand).ifPresent(
                    (definitionTable) -> {
                        final ImmutableArrayTable<Cell> definitionCellAsTable = definitionTable.subTable(0, 1, DEFINITION_COLUMN, DEFINITION_COLUMN + 1);
                        // We only use splices mappings when token is not in main body but is in spliced.
                        Set<ParseDefinition> chosenMappings = (!mainBodyDefinitionsWithTransative.contains(shorthand))&&
                                spliceAreaDefinitionsWithTransative.contains(shorthand) ? spliceAreaParseTokenMappings : mainBodyParseTokenMappings;
                        parse(parsedDefinitionCells, chosenMappings, definitionCellAsTable, (parsedCell) -> {});
                    }
            );
        }

    }

    private Set<ParseDefinition> buildDefinitionDefinitionTokenMap(Touch touch) {
        Set<ParseDefinition> parseTokenMappings = new HashSet<>();
        addDefinitionTokens(touch, parseTokenMappings);
        return parseTokenMappings;
    }


    private void parse(HashBasedTable<Integer, Integer, ParsedCell> parsedCells, Set<ParseDefinition> parseTokenMappings, ImmutableArrayTable<Cell> cells, Consumer<ParsedCell> observer) {
        for (BackingTableLocationAndValue<Cell> cellAndLocation : cells) {
            ParsedCell parsedCell = lexer.lexCell(cellAndLocation.getValue(), parseTokenMappings);
            observer.accept(parsedCell);
            parsedCells.put(cellAndLocation.getRow(), cellAndLocation.getCol(), parsedCell);
        }
    }


    private void addCallingPositionTokens(Touch touch, Set<ParseDefinition> parseDefinitions) {
        for (NotationBody notation : touch.getAvailableNotations()) {
            for (NotationMethodCallingPosition callingPosition : notation.getMethodBasedCallingPositions()) {
                parseDefinitions.add(new ParseDefinition(callingPosition.getName(), ParseType.CALLING_POSITION));
            }
        }
    }

    private void addPlainLeadToken(Touch touch, Set<ParseDefinition> parseDefinitions) {
        if (touch.getCheckingType() == CheckingType.LEAD_BASED) {
            parseDefinitions.add(new ParseDefinition(touch.getPlainLeadToken(), ParseType.PLAIN_LEAD));
        }
    }

    private void addCallTokens(Touch touch, Set<ParseDefinition> parseDefinitions) {
        for (NotationBody notation : touch.getAvailableNotations()) {
            for (NotationCall notationCall : notation.getCalls()) {
                parseDefinitions.add(new ParseDefinition(notationCall.getNameShorthand(), ParseType.CALL));
                parseDefinitions.add(new ParseDefinition(notationCall.getName(), ParseType.CALL));
            }
        }
    }

    private void addSpliceTokens(Touch touch, Set<ParseDefinition> parseDefinitions) {
        for (NotationBody notation : touch.getAvailableNotations()) {
            if (!Strings.isNullOrEmpty(notation.getSpliceIdentifier())){
                parseDefinitions.add(new ParseDefinition(notation.getSpliceIdentifier(), ParseType.SPLICE));
            }
            parseDefinitions.add(new ParseDefinition(notation.getName(), ParseType.SPLICE));
            parseDefinitions.add(new ParseDefinition(notation.getNameIncludingNumberOfBells(), ParseType.SPLICE));
        }
    }

    private void addDefinitionTokens(Touch touch, Set<ParseDefinition> parseDefinitions) {
        for (String shorthand : touch.getAllDefinitionShorthands()) {
            parseDefinitions.add(new ParseDefinition(shorthand, ParseType.DEFINITION));
        }
    }

    private void addWhitespaceTokens(Set<ParseDefinition> parseDefinitions) {
        parseDefinitions.add(new ParseDefinition("\\s", ParseType.WHITESPACE));
    }

    private void addVarianceTokens(Set<ParseDefinition> parseDefinitions) { //TODO ensure these chars cant appear anywhere else. i.e.in calls method names Etc
        parseDefinitions.add(new ParseDefinition("(\\[)([-+](?:[0-9,]+|[oiOI]+))", ParseType.VARIANCE_OPEN, ParseType.VARIANCE_DETAIL));// TODO should these be defined as constants somewhere?
        parseDefinitions.add(new ParseDefinition("\\]", ParseType.VARIANCE_CLOSE));
    }

    private void addMultiplierGroupTokens(Set<ParseDefinition> parseDefinitions) { //TODO ensure these chars cant appear anywhere else. i.e.in calls method names Etc
        parseDefinitions.add(new ParseDefinition("\\(", ParseType.MULTIPLIER_GROUP_OPEN));// TODO should these be defined as constants somewhere?
        parseDefinitions.add(new ParseDefinition("\\)", ParseType.MULTIPLIER_GROUP_CLOSE));
    }

}
