package org.ringingmaster.engine.parsernew.assignparsetype;

import com.google.common.collect.Maps;
import org.junit.Test;
import org.ringingmaster.engine.parser.ParseType;
import org.ringingmaster.engine.parsernew.cell.Group;
import org.ringingmaster.engine.parsernew.cell.ParsedCell;
import org.ringingmaster.engine.parsernew.cell.Section;
import org.ringingmaster.engine.touch.newcontainer.cell.Cell;
import org.ringingmaster.engine.touch.newcontainer.cell.CellBuilder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.ringingmaster.engine.parser.ParseType.CALL;
import static org.ringingmaster.engine.parser.ParseType.SPLICE;
import static org.ringingmaster.engine.parser.ParseType.WHITESPACE;

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

    private void assertParse(ParsedCell parsedCell, SectionExpected... expecteds) {
        assertNotNull(parsedCell);

        assertEquals(parsedCell.getElementSize(), Arrays.stream(expecteds).mapToInt(e -> e.length).sum());

        int elementIndex = 0;
        for (SectionExpected expected : expecteds) {
            Optional<Section> sectionAtFirstElementIndex = parsedCell.getSectionAtElementIndex(elementIndex);
            Optional<Group> wordAtFirstElementIndex = parsedCell.getWordAtElementIndex(elementIndex);

            if (expected.parseType == null) {
                assertFalse(sectionAtFirstElementIndex.isPresent());
                assertFalse(wordAtFirstElementIndex.isPresent());

                for (int i=0;i<expected.length;i++) {
                    assertFalse(parsedCell.getSectionAtElementIndex(elementIndex).isPresent());
                    assertFalse(parsedCell.getWordAtElementIndex(elementIndex).isPresent());
                    elementIndex++;
                }
            }
            else {
                assertTrue(sectionAtFirstElementIndex.isPresent());
                assertEquals(expected.parseType, sectionAtFirstElementIndex.get().getParseType());

                assertTrue(wordAtFirstElementIndex.isPresent());
                assertEquals(1, wordAtFirstElementIndex.get().getSections().size());
                assertEquals(sectionAtFirstElementIndex.get(), wordAtFirstElementIndex.get().getSections().get(0));

                for (int i = 0; i < expected.length; i++) {
                    assertEquals(sectionAtFirstElementIndex.get(), parsedCell.getSectionAtElementIndex(elementIndex).get());
                    assertEquals(sectionAtFirstElementIndex.get(), parsedCell.getWordAtElementIndex(elementIndex).get().getSections().get(0));

                    elementIndex++;
                }
            }
        }
    }

    SectionExpected unparsed(int length) {
        return parsed(length, null);
    }

    SectionExpected parsed(ParseType parseType) {
        return parsed(1, parseType);
    }

    SectionExpected parsed(int length, ParseType parseType) {
        return new SectionExpected(length, parseType);
    }

    private class SectionExpected {
        final int length;
        final ParseType parseType;

        SectionExpected(int length, ParseType parseType) {
            this.length = length;
            this.parseType = parseType;
        }
    }

    private Cell buildCell(String characters) {
        return new CellBuilder().insert(0, characters).build();
    }

}