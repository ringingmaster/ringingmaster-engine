package org.ringingmaster.engine.parser.assignparse;

import com.google.common.base.Strings;
import com.google.common.collect.HashBasedTable;
import org.ringingmaster.engine.arraytable.BackingTableLocationAndValue;
import org.ringingmaster.engine.arraytable.ImmutableArrayTable;
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.notation.NotationCall;
import org.ringingmaster.engine.notation.NotationMethodCallingPosition;
import org.ringingmaster.engine.parser.Parse;
import org.ringingmaster.engine.parser.ParseBuilder;
import org.ringingmaster.engine.parser.ParseType;
import org.ringingmaster.engine.parser.cell.ParsedCell;
import org.ringingmaster.engine.touch.Touch;
import org.ringingmaster.engine.touch.cell.Cell;
import org.ringingmaster.engine.touch.checkingtype.CheckingType;

import javax.annotation.concurrent.Immutable;
import java.util.Collections;
import java.util.HashMap;
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

    private final CellLexer lexer = new CellLexer();

    public Parse apply(Touch touch) {

        final HashBasedTable<Integer, Integer, ParsedCell> parsedTouchCells = HashBasedTable.create();
        parseCallPositionArea(touch, parsedTouchCells);
        final Set<String> mainBodyDefinitions =
                parseMainBodyArea(touch, parsedTouchCells);
        final Set<String> spliceAreaDefinitions =
                parseSpliceArea(touch, parsedTouchCells);

        final HashBasedTable<Integer, Integer, ParsedCell> parsedDefinitionCells = HashBasedTable.create();
        parseDefinitionShorthandArea(touch, parsedDefinitionCells);
        parseDefinitionDefinitionArea(touch, parsedDefinitionCells, mainBodyDefinitions, spliceAreaDefinitions);

        
        //TODO should we allow variance in definitions?	 Probably not.	addVarianceTokens(parseTokenMappings);


        return new ParseBuilder()
                .prototypeOf(touch)
                .setTouchTableCells(parsedTouchCells)
                .setDefinitionTableCells(parsedDefinitionCells)
                .build();
    }

    private void parseDefinitionDefinitionArea(Touch touch, HashBasedTable<Integer, Integer, ParsedCell> parsedDefinitionCells, Set<String> mainBodyDefinitions, Set<String> spliceAreaDefinitions) {

        Map<String, ParseType> mainBodyParseTokenMappings = buildMainBodyParseTokenMap(touch);
        Map<String, ParseType> spliceAreaParseTokenMappings = buildSpliceAreaParseTokenMap(touch);

        for (String shorthand : touch.getAllDefinitionShorthands()) {
            touch.findDefinitionByShorthand(shorthand).ifPresent(
                    (definitionTable) -> {
                        final ImmutableArrayTable<Cell> definitionCellAsTable = definitionTable.subTable(0, 1, DEFINITION_COLUMN, DEFINITION_COLUMN + 1);
                        // We only use splices mappings when token is not in main body but is in spliced.
                        Map<String, ParseType> chosenMappings = (!mainBodyDefinitions.contains(shorthand))&&
                                                                    spliceAreaDefinitions.contains(shorthand) ? spliceAreaParseTokenMappings : mainBodyParseTokenMappings;
                        parse(parsedDefinitionCells, chosenMappings, definitionCellAsTable, (parsedCell) -> {});
                    }
            );
        }

    }

    private void parseDefinitionShorthandArea(Touch touch, HashBasedTable<Integer, Integer, ParsedCell> parsedDefinitionCells) {
        // This is a special parse - we just take trimmed versions of every definition and add it as a parse
        Map<String, ParseType> parseTokenMappings = new HashMap<>();
        addDefinitionTokens(touch, parseTokenMappings);
        addWhitespaceTokens(parseTokenMappings);

        parse(parsedDefinitionCells, parseTokenMappings, touch.definitionShorthandCells(), (parsedCell) -> {});
    }

    private void parseCallPositionArea(Touch touch, HashBasedTable<Integer, Integer, ParsedCell> parsedCells) {
        if (touch.getCheckingType() != CheckingType.COURSE_BASED) {
            return;
        }
        Map<String, ParseType> parseTokenMappings = new HashMap<>();
        addCallingPositionTokens(touch, parseTokenMappings);
        addWhitespaceTokens(parseTokenMappings);

        parse(parsedCells, parseTokenMappings, touch.callPositionCells(), (parsedCell) -> {});
    }

    private Set<String> parseMainBodyArea(Touch touch, HashBasedTable<Integer, Integer, ParsedCell> parsedCells) {
        Map<String, ParseType> parseTokenMappings = buildMainBodyParseTokenMap(touch);

        Set<String> definitionsInUse = new HashSet<>();
        parse(parsedCells, parseTokenMappings, touch.mainBodyCells(), (parsedCell) ->
                parsedCell.allSections().stream()
                        .filter(section -> section.getParseType().equals(ParseType.DEFINITION))
                        .map(parsedCell::getCharacters)
                        .forEach(definitionsInUse::add)
        );

        return definitionsInUse;
    }

    private Map<String, ParseType> buildMainBodyParseTokenMap(Touch touch) {
        Map<String, ParseType> parseTokenMappings = new HashMap<>();
        addCallTokens(touch, parseTokenMappings);
        addPlainLeadToken(touch, parseTokenMappings);
        addVarianceTokens(parseTokenMappings);
        addGroupTokens(parseTokenMappings);
        addDefinitionTokens(touch, parseTokenMappings);
        addWhitespaceTokens(parseTokenMappings);
        return parseTokenMappings;
    }

    private Set<String> parseSpliceArea(Touch touch, HashBasedTable<Integer, Integer, ParsedCell> parsedCells) {
        if (!touch.isSpliced()) {
            return Collections.emptySet();
        }
        Map<String, ParseType> parseTokenMappings = buildSpliceAreaParseTokenMap(touch);

        Set<String> definitionsInUse = new HashSet<>();
        parse(parsedCells, parseTokenMappings, touch.splicedCells(), (parsedCell) ->
                parsedCell.allSections().stream()
                    .filter(section -> section.getParseType().equals(ParseType.DEFINITION))
                    .map(parsedCell::getCharacters)
                    .forEach(definitionsInUse::add)
        );

        return definitionsInUse;
    }

    private Map<String, ParseType> buildSpliceAreaParseTokenMap(Touch touch) {
        Map<String, ParseType> parseTokenMappings = new HashMap<>();
        addSpliceTokens(touch, parseTokenMappings);
        addGroupTokens(parseTokenMappings);
        addDefinitionTokens(touch, parseTokenMappings);
        addWhitespaceTokens(parseTokenMappings);
        return parseTokenMappings;
    }


    private void parse(HashBasedTable<Integer, Integer, ParsedCell> parsedCells, Map<String, ParseType> parseTokenMappings, ImmutableArrayTable<Cell> cells, Consumer<ParsedCell> observer) {
        for (BackingTableLocationAndValue<Cell> cellAndLocation : cells) {
            ParsedCell parsedCell = lexer.lexCell(cellAndLocation.getValue(), parseTokenMappings);
            observer.accept(parsedCell);
            parsedCells.put(cellAndLocation.getRow(), cellAndLocation.getCol(), parsedCell);
        }
    }


    private void addCallingPositionTokens(Touch touch, Map<String, ParseType> parsings) {
        for (NotationBody notation : touch.getAvailableNotations()) {
            for (NotationMethodCallingPosition callingPosition : notation.getMethodBasedCallingPositions()) {
                parsings.put(callingPosition.getName(), ParseType.CALLING_POSITION);
            }
        }
    }


    private void addPlainLeadToken(Touch touch, Map<String, ParseType> parsings) {
        if (touch.getCheckingType() == CheckingType.LEAD_BASED) {
            parsings.put(touch.getPlainLeadToken(), ParseType.PLAIN_LEAD);
        }
    }

    private void addCallTokens(Touch touch, Map<String, ParseType> parsings) {
        for (NotationBody notation : touch.getAvailableNotations()) {
            for (NotationCall notationCall : notation.getCalls()) {
                parsings.put(notationCall.getNameShorthand(), ParseType.CALL);
                parsings.put(notationCall.getName(), ParseType.CALL);
            }
        }
    }

    private void addSpliceTokens(Touch touch, Map<String, ParseType> parsings) {
        for (NotationBody notation : touch.getAvailableNotations()) {
            if (!Strings.isNullOrEmpty(notation.getSpliceIdentifier())){
                parsings.put(notation.getSpliceIdentifier(), ParseType.SPLICE);
            }
            parsings.put(notation.getName(), ParseType.SPLICE);
            parsings.put(notation.getNameIncludingNumberOfBells(), ParseType.SPLICE);
        }
    }

    private void addDefinitionTokens(Touch touch, Map<String, ParseType> parsings) {
        for (String shorthand : touch.getAllDefinitionShorthands()) {
            parsings.put(shorthand, ParseType.DEFINITION);
        }
    }

    private void addWhitespaceTokens(Map<String, ParseType> parsings) {
        parsings.put(" ", ParseType.WHITESPACE);
    }

    private void addVarianceTokens(Map<String, ParseType> parsings) { //TODO ensure these chars cant appear anywhere else. i.e.in calls method names Etc
        parsings.put("[", ParseType.VARIANCE_OPEN);// TODO should these be defined as constants somewhere?
        parsings.put("]", ParseType.VARIANCE_CLOSE);
    }

    private void addGroupTokens(Map<String, ParseType> parsings) { //TODO ensure these chars cant appear anywhere else. i.e.in calls method names Etc
        parsings.put("(", ParseType.GROUP_OPEN);// TODO should these be defined as constants somewhere?
        parsings.put(")", ParseType.GROUP_CLOSE);
    }

}
