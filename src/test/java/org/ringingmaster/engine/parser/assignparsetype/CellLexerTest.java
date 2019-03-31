package org.ringingmaster.engine.parser.assignparsetype;

import com.google.common.collect.Sets;
import org.junit.Test;
import org.ringingmaster.engine.parser.cell.ParsedCell;
import org.ringingmaster.engine.touch.cell.Cell;
import org.ringingmaster.engine.touch.cell.CellBuilder;

import java.util.Set;

import static org.ringingmaster.engine.parser.AssertParse.assertParse;
import static org.ringingmaster.engine.parser.AssertParse.unparsed;
import static org.ringingmaster.engine.parser.AssertParse.valid;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.CALL;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.SPLICE;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.VARIANCE_CLOSE;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.VARIANCE_DETAIL;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.VARIANCE_OPEN;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.WHITESPACE;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class CellLexerTest {

    private CellLexer cellLexer = new CellLexer();

    @Test
    public void emptyCellReturnsEmptyParseType() {

        Set<ParseDefinition> a = Sets.newHashSet();

        Cell cell = buildCell("");

        ParsedCell parsedCell = cellLexer.lexCell(cell, a);

        assertParse(parsedCell);

    }

    @Test
    public void parseFailReturnsUnparsed() {
        Set<ParseDefinition> a = Sets.newHashSet();
        a.add(new ParseDefinition("a", CALL));
        Cell cell = buildCell("zy");

        ParsedCell parsedCell = cellLexer.lexCell(cell, a);

        assertParse(parsedCell, unparsed(2));
    }

    @Test
    public void singleCharacterCellReturnsCorrectParseType() {
        Set<ParseDefinition> a = Sets.newHashSet();
        a.add(new ParseDefinition("a", CALL));
        Cell cell = buildCell("a");

        ParsedCell parsedCell = cellLexer.lexCell(cell, a);

        assertParse(parsedCell, valid(CALL));
    }

    @Test
    public void distinguishTwoNonOverlappingParseTypes() {
        Set<ParseDefinition> a = Sets.newHashSet();
        a.add(new ParseDefinition("a", CALL));
        a.add(new ParseDefinition("b", SPLICE));

        Cell cell = buildCell("ab");

        ParsedCell parsedCell = cellLexer.lexCell(cell, a);

        assertParse(parsedCell, valid(CALL), valid(SPLICE));
    }

    @Test
    public void overlappingTokensUseLongerTokenFirst() {
        Set<ParseDefinition> a = Sets.newHashSet();
        a.add(new ParseDefinition("ab", CALL));
        a.add(new ParseDefinition("bcd", SPLICE));

        Cell cell = buildCell("abcd");

        ParsedCell parsedCell = cellLexer.lexCell(cell, a);

        assertParse(parsedCell, unparsed(1), valid(3, SPLICE));
    }

    @Test
    public void extendedRightTokensUseLongerTokenFirst() {
        Set<ParseDefinition> a = Sets.newHashSet();
        a.add(new ParseDefinition("ab", CALL));
        a.add(new ParseDefinition("abc", SPLICE));

        Cell cell = buildCell("abc");

        ParsedCell parsedCell = cellLexer.lexCell(cell, a);

        assertParse(parsedCell, valid(3, SPLICE));
    }

    @Test
    public void extendedLeftTokensUseLongerTokenFirst() {
        Set<ParseDefinition> a = Sets.newHashSet();
        a.add(new ParseDefinition("bc", CALL));
        a.add(new ParseDefinition("abc", SPLICE));

        Cell cell = buildCell("abc");

        ParsedCell parsedCell = cellLexer.lexCell(cell, a);

        assertParse(parsedCell, valid(3, SPLICE));
    }

    @Test
    public void whitespaceInTokenGetsMarkedAsToken() {
        Set<ParseDefinition> a = Sets.newHashSet();
        a.add(new ParseDefinition("a b", CALL));
        a.add(new ParseDefinition(" ", WHITESPACE));

        Cell cell = buildCell("xa b s");

        ParsedCell parsedCell = cellLexer.lexCell(cell, a);

        assertParse(parsedCell, unparsed(1), valid(3, CALL), valid(WHITESPACE), unparsed(1));

    }

    @Test
    public void backToBackParsingsDoNotResultInGaps() {
        Set<ParseDefinition> a = Sets.newHashSet();
        a.add(new ParseDefinition("-", CALL));
        a.add(new ParseDefinition(" ", WHITESPACE));

        Cell cell = buildCell("- ");

        ParsedCell parsedCell = cellLexer.lexCell(cell, a);

        assertParse(parsedCell, valid(CALL), valid(WHITESPACE));

    }


    @Test
    public void matchingRegexWithMultipleGroupsAddsMultipleGroups() {
        Set<ParseDefinition> a = Sets.newHashSet();
        a.add(new ParseDefinition("(\\[)([-+](?:[0-9,]+|[oiOI]+))", VARIANCE_OPEN, VARIANCE_DETAIL));

        Cell cell = buildCell("[-o]");

        ParsedCell parsedCell = cellLexer.lexCell(cell, a);

        assertParse(parsedCell, valid(VARIANCE_OPEN), valid(2, VARIANCE_DETAIL), unparsed());
    }

    @Test (expected = IllegalStateException.class)
    public void mismatchBetweenParseTypesCountAndRegexGroupsCountThrows() {
        Set<ParseDefinition> a = Sets.newHashSet();
        a.add(new ParseDefinition("(\\[)([-+](?:[0-9,]+|[oiOI]+))", VARIANCE_OPEN));

        Cell cell = buildCell("[-o]");

        ParsedCell parsedCell = cellLexer.lexCell(cell, a);
    }

    @Test (expected = IllegalStateException.class)
    public void notUsingRegexGroupsAndSupplyingMoreThanOneParseTypeThrows() {
        Set<ParseDefinition> a = Sets.newHashSet();
        a.add(new ParseDefinition("\\[", VARIANCE_OPEN, VARIANCE_DETAIL));

        Cell cell = buildCell("[-o]");

        ParsedCell parsedCell = cellLexer.lexCell(cell, a);
    }

    @Test (expected = IllegalStateException.class)
    public void notUsingRegexGroupsAndSupplyingLessThanOneParseTypeThrows() {
        Set<ParseDefinition> a = Sets.newHashSet();
        a.add(new ParseDefinition("\\["));

        Cell cell = buildCell("[-o]");

        ParsedCell parsedCell = cellLexer.lexCell(cell, a);
    }

    @Test
    public void matchComplexGroupsInsideOtherParsings() {
        Set<ParseDefinition> a = Sets.newHashSet();
        a.add(new ParseDefinition("(\\[)([-+](?:[0-9,]+|[oiOI]+))", VARIANCE_OPEN, VARIANCE_DETAIL));
        a.add(new ParseDefinition("\\]", VARIANCE_CLOSE));
        a.add(new ParseDefinition("-", CALL));
        a.add(new ParseDefinition("o", CALL));
        a.add(new ParseDefinition("\\s", WHITESPACE));

        Cell cell = buildCell("o[-o -]");

        ParsedCell parsedCell = cellLexer.lexCell(cell, a);

        assertParse(parsedCell, valid(CALL), valid(VARIANCE_OPEN), valid(2, VARIANCE_DETAIL), valid(WHITESPACE), valid(CALL), valid(VARIANCE_CLOSE));
    }




    private Cell buildCell(String characters) {
        return new CellBuilder().insert(0, characters).build();
    }

}