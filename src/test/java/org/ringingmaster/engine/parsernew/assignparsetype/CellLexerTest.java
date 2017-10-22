package org.ringingmaster.engine.parsernew.assignparsetype;

import com.google.common.collect.Maps;
import org.junit.Test;
import org.ringingmaster.engine.parser.ParseType;
import org.ringingmaster.engine.parsernew.cell.ParsedCell;
import org.ringingmaster.engine.touch.newcontainer.cell.Cell;
import org.ringingmaster.engine.touch.newcontainer.cell.CellBuilder;

import java.util.HashMap;

import static org.ringingmaster.engine.parser.ParseType.CALL;
import static org.ringingmaster.engine.parser.ParseType.SPLICE;
import static org.ringingmaster.engine.parser.ParseType.WHITESPACE;
import static org.ringingmaster.engine.parsernew.assignparsetype.AssertParse.assertParse;
import static org.ringingmaster.engine.parsernew.assignparsetype.AssertParse.parsed;
import static org.ringingmaster.engine.parsernew.assignparsetype.AssertParse.unparsed;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class CellLexerTest {

    private CellLexer cellLexer = new CellLexer();

    @Test
    public void emptyCellReturnsEmptyParseType() {

        HashMap<String, ParseType> a = Maps.newHashMap();

        Cell cell = buildCell("");

        ParsedCell parsedCell = cellLexer.lexCell(cell, a);

        assertParse(parsedCell);

    }

    @Test
    public void parseFailReturnsUnparsed() {
        HashMap<String, ParseType> a = Maps.newHashMap();
        a.put("a", CALL);
        Cell cell = buildCell("zy");

        ParsedCell parsedCell = cellLexer.lexCell(cell, a);

        assertParse(parsedCell, unparsed(2));
    }

    @Test
    public void singleCharacterCellReturnsCorrectParseType() {
        HashMap<String, ParseType> a = Maps.newHashMap();
        a.put("a", CALL);
        Cell cell = buildCell("a");

        ParsedCell parsedCell = cellLexer.lexCell(cell, a);

        assertParse(parsedCell, parsed(CALL));
    }

    @Test
    public void distinguishTwoNonOverlappingParseTypes() {
        HashMap<String, ParseType> a = Maps.newHashMap();
        a.put("a", CALL);
        a.put("b", SPLICE);

        Cell cell = buildCell("ab");

        ParsedCell parsedCell = cellLexer.lexCell(cell, a);

        assertParse(parsedCell, parsed(CALL), parsed(SPLICE));
    }

    @Test
    public void overlappingTokensUseLongerTokenFirst() {
        HashMap<String, ParseType> a = Maps.newHashMap();
        a.put("ab", CALL);
        a.put("bcd", SPLICE);

        Cell cell = buildCell("abcd");

        ParsedCell parsedCell = cellLexer.lexCell(cell, a);

        assertParse(parsedCell, unparsed(1), parsed(3, SPLICE));
    }

    @Test
    public void extendedRightTokensUseLongerTokenFirst() {
        HashMap<String, ParseType> a = Maps.newHashMap();
        a.put("ab", CALL);
        a.put("abc", SPLICE);

        Cell cell = buildCell("abc");

        ParsedCell parsedCell = cellLexer.lexCell(cell, a);

        assertParse(parsedCell, parsed(3, SPLICE));
    }

    @Test
    public void extendedLeftTokensUseLongerTokenFirst() {
        HashMap<String, ParseType> a = Maps.newHashMap();
        a.put("bc", CALL);
        a.put("abc", SPLICE);

        Cell cell = buildCell("abc");

        ParsedCell parsedCell = cellLexer.lexCell(cell, a);

        assertParse(parsedCell, parsed(3, SPLICE));
    }

    @Test
    public void whitespaceInTokenGetsMarkedAsToken() {
        HashMap<String, ParseType> a = Maps.newHashMap();
        a.put("a b", CALL);
        a.put(" ", WHITESPACE);

        Cell cell = buildCell("xa b s");

        ParsedCell parsedCell = cellLexer.lexCell(cell, a);

        assertParse(parsedCell, unparsed(1), parsed(3, CALL), parsed(WHITESPACE), unparsed(1));

    }




    private Cell buildCell(String characters) {
        return new CellBuilder().insert(0, characters).build();
    }

}