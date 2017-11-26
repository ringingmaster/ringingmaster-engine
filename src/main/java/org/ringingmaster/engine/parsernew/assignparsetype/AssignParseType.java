package org.ringingmaster.engine.parsernew.assignparsetype;

import com.google.common.base.Strings;
import com.google.common.collect.HashBasedTable;
import net.jcip.annotations.Immutable;
import org.ringingmaster.engine.arraytable.BackingTableLocationAndValue;
import org.ringingmaster.engine.arraytable.ImmutableArrayTable;
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.notation.NotationCall;
import org.ringingmaster.engine.notation.NotationMethodCallingPosition;
import org.ringingmaster.engine.parser.ParseType;
import org.ringingmaster.engine.parsernew.Parse;
import org.ringingmaster.engine.parsernew.ParseBuilder;
import org.ringingmaster.engine.parsernew.cell.ParsedCell;
import org.ringingmaster.engine.parsernew.cell.ParsedDefinitionCell;
import org.ringingmaster.engine.touch.newcontainer.Touch;
import org.ringingmaster.engine.touch.newcontainer.cell.Cell;
import org.ringingmaster.engine.touch.newcontainer.checkingtype.CheckingType;
import org.ringingmaster.engine.touch.newcontainer.definition.DefinitionCell;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Parses all cells and assigns a parse type where possible
 *
 * @author stevelake
 */
@Immutable
public class AssignParseType {

    private final CellLexer lexer = new CellLexer();

    public Parse parse(Touch touch) {
        HashBasedTable<Integer, Integer, ParsedCell> parsedCells = HashBasedTable.create();
        parseCallPositionArea(touch, parsedCells);
        parseMainBodyArea(touch, parsedCells);
        parseSpliceArea(touch, parsedCells);

        List<ParsedDefinitionCell> parsedDefinitionCells =
                parseDefinitions(touch, parsedCells);

        return new ParseBuilder()
                .prototypeOf(touch)
                .setParsedCells(parsedCells)
                .setDefinitions(parsedDefinitionCells)
                .build();
    }

    private void parseCallPositionArea(Touch touch, HashBasedTable<Integer, Integer, ParsedCell> parsedCells) {
        if (touch.getCheckingType() != CheckingType.COURSE_BASED) {
            return;
        }
        Map<String, ParseType> parseTokenMappings = new HashMap<>();
        addCallingPositionTokens(touch, parseTokenMappings);
        addWhitespaceTokens(parseTokenMappings);

        parse(parsedCells, parseTokenMappings, touch.callPositionCells());
    }

    private void parseMainBodyArea(Touch touch, HashBasedTable<Integer, Integer, ParsedCell> parsedCells) {
        Map<String, ParseType> parseTokenMappings = new HashMap<>();
        addCallTokens(touch, parseTokenMappings);
        addPlainLeadToken(touch, parseTokenMappings);
        addVarianceTokens(parseTokenMappings);
        addGroupTokens(parseTokenMappings);
        addDefinitionTokens(touch, parseTokenMappings);
        addWhitespaceTokens(parseTokenMappings);

        parse(parsedCells, parseTokenMappings, touch.mainBodyCells());
    }

    private void parseSpliceArea(Touch touch, HashBasedTable<Integer, Integer, ParsedCell> parsedCells) {
        if (!touch.isSpliced()) {
            return;
        }
        Map<String, ParseType> parseTokenMappings = new HashMap<>();
        addSpliceTokens(touch, parseTokenMappings);
        addVarianceTokens(parseTokenMappings);
        addGroupTokens(parseTokenMappings);
        addDefinitionTokens(touch, parseTokenMappings);
        addWhitespaceTokens(parseTokenMappings);

        parse(parsedCells, parseTokenMappings, touch.splicedCells());
    }

    private List<ParsedDefinitionCell> parseDefinitions(Touch touch, HashBasedTable<Integer, Integer, ParsedCell> parsedCells) {
        Map<String, ParseType> parseTokenMappings = new HashMap<>();
        addCallTokens(touch, parseTokenMappings);
        addPlainLeadToken(touch, parseTokenMappings);
//TODO should we allow variance in definitions?	 Probably not.	addVarianceTokens(parseTokenMappings);
        addGroupTokens(parseTokenMappings);
//TODO should we allow embedded definitions in definitions?	probably, but will need some good tests. addDefinitionTokens(touch, parseTokenMappings);
        addWhitespaceTokens(parseTokenMappings);

        return touch.getAllDefinitions().stream()
                .map((definitionCell) -> lexer.lexCell(definitionCell, parseTokenMappings))
                .collect(Collectors.toList());
    }

    private void parse(HashBasedTable<Integer, Integer, ParsedCell> parsedCells, Map<String, ParseType> parseTokenMappings, ImmutableArrayTable<Cell> cells) {
        for (BackingTableLocationAndValue<Cell> cellAndLocation : cells) {
            ParsedCell parsedCell = lexer.lexCell(cellAndLocation.getValue(), parseTokenMappings);
            parsedCells.put(cellAndLocation.getRow(), cellAndLocation.getCol(), parsedCell);
        }
    }


    private void addCallingPositionTokens(Touch touch, Map<String, ParseType> parsings) {
        for (NotationBody notation : touch.getInUseNotations()) {
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
        for (NotationBody notation : touch.getInUseNotations()) {
            for (NotationCall notationCall : notation.getCalls()) {
                parsings.put(notationCall.getNameShorthand(), ParseType.CALL);
                parsings.put(notationCall.getName(), ParseType.CALL);
            }
        }
    }

    private void addSpliceTokens(Touch touch, Map<String, ParseType> parsings) {
        for (NotationBody notation : touch.getInUseNotations()) {
            if (!Strings.isNullOrEmpty(notation.getSpliceIdentifier())){
                parsings.put(notation.getSpliceIdentifier(), ParseType.SPLICE);
            }
            parsings.put(notation.getName(), ParseType.SPLICE);
            parsings.put(notation.getNameIncludingNumberOfBells(), ParseType.SPLICE);
        }
    }

    private void addDefinitionTokens(Touch touch, Map<String, ParseType> parsings) {
        for (DefinitionCell definitionCell : touch.getAllDefinitions()) {
            parsings.put(definitionCell.getShorthand(), ParseType.DEFINITION);
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
