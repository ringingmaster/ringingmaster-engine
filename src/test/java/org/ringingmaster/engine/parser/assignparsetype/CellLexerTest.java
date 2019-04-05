package org.ringingmaster.engine.parser.assignparsetype;

import com.google.common.collect.Range;
import com.google.common.collect.Sets;
import org.junit.Test;
import org.ringingmaster.engine.parser.cell.ParsedCell;
import org.ringingmaster.engine.touch.cell.Cell;
import org.ringingmaster.engine.touch.cell.CellBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import static org.ringingmaster.engine.parser.AssertParse.assertParse;
import static org.ringingmaster.engine.parser.AssertParse.section;
import static org.ringingmaster.engine.parser.AssertParse.unparsed;
import static org.ringingmaster.engine.parser.AssertParse.valid;
import static org.ringingmaster.engine.parser.assignparsetype.LexerDefinition.PRIORITY_HIGHEST;
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

    private final Logger log = LoggerFactory.getLogger(CellLexerTest.class);

    private final CellLexer cellLexer = new CellLexer();

    @Test
    public void emptyCellReturnsEmptyParseType() {

        Set<LexerDefinition> a = Sets.newHashSet();

        Cell cell = buildCell("");

        ParsedCell parsedCell = cellLexer.lexCell(cell, a, "");

        assertParse(parsedCell);

    }

    @Test
    public void parseFailReturnsUnparsed() {
        Set<LexerDefinition> a = Sets.newHashSet();
        a.add(new LexerDefinition("a", CALL));
        Cell cell = buildCell("zy");

        ParsedCell parsedCell = cellLexer.lexCell(cell, a, "");

        assertParse(parsedCell, unparsed(2));
    }

    @Test
    public void singleCharacterCellReturnsCorrectParseType() {
        Set<LexerDefinition> a = Sets.newHashSet();
        a.add(new LexerDefinition("a", CALL));
        Cell cell = buildCell("a");

        ParsedCell parsedCell = cellLexer.lexCell(cell, a, "");

        assertParse(parsedCell, valid(CALL));
    }

    @Test
    public void distinguishTwoNonOverlappingParseTypes() {
        Set<LexerDefinition> a = Sets.newHashSet();
        a.add(new LexerDefinition("a", CALL));
        a.add(new LexerDefinition("b", SPLICE));

        Cell cell = buildCell("ab");

        ParsedCell parsedCell = cellLexer.lexCell(cell, a, "");

        assertParse(parsedCell, valid(CALL), valid(SPLICE));
    }

    @Test
    public void overlappingTokensUseLongerTokenFirst() {
        Set<LexerDefinition> a = Sets.newHashSet();
        a.add(new LexerDefinition("ab", CALL));
        a.add(new LexerDefinition("bcd", SPLICE));

        Cell cell = buildCell("abcd");

        ParsedCell parsedCell = cellLexer.lexCell(cell, a, "");

        assertParse(parsedCell, unparsed(1), valid(3, SPLICE));
    }

    @Test
    public void extendedRightTokensUseLongerTokenFirst() {
        Set<LexerDefinition> a = Sets.newHashSet();
        a.add(new LexerDefinition("ab", CALL));
        a.add(new LexerDefinition("abc", SPLICE));

        Cell cell = buildCell("abc");

        ParsedCell parsedCell = cellLexer.lexCell(cell, a, "");

        assertParse(parsedCell, valid(3, SPLICE));
    }

    @Test
    public void extendedLeftTokensUseLongerTokenFirst() {
        Set<LexerDefinition> a = Sets.newHashSet();
        a.add(new LexerDefinition("bc", CALL));
        a.add(new LexerDefinition("abc", SPLICE));

        Cell cell = buildCell("abc");

        ParsedCell parsedCell = cellLexer.lexCell(cell, a, "");

        assertParse(parsedCell, valid(3, SPLICE));
    }

    @Test
    public void whitespaceInTokenGetsMarkedAsToken() {
        Set<LexerDefinition> a = Sets.newHashSet();
        a.add(new LexerDefinition("a b", CALL));
        a.add(new LexerDefinition(" ", WHITESPACE));

        Cell cell = buildCell("xa b s");

        ParsedCell parsedCell = cellLexer.lexCell(cell, a, "");

        assertParse(parsedCell, unparsed(1), valid(3, CALL), valid(WHITESPACE), unparsed(1));

    }

    @Test
    public void backToBackParsingsDoNotResultInGaps() {
        Set<LexerDefinition> a = Sets.newHashSet();
        a.add(new LexerDefinition("-", CALL));
        a.add(new LexerDefinition(" ", WHITESPACE));

        Cell cell = buildCell("- ");

        ParsedCell parsedCell = cellLexer.lexCell(cell, a, "");

        assertParse(parsedCell, valid(CALL), valid(WHITESPACE));

    }


    @Test
    public void matchingRegexWithMultipleGroupsAddsMultipleGroups() {
        Set<LexerDefinition> a = Sets.newHashSet();
        a.add(new LexerDefinition("(\\[)([-+](?:[0-9,]+|[oiOI]+))", VARIANCE_OPEN, VARIANCE_DETAIL));

        Cell cell = buildCell("[-o]");

        ParsedCell parsedCell = cellLexer.lexCell(cell, a, "");

        assertParse(parsedCell, valid(section(VARIANCE_OPEN), section(2, VARIANCE_DETAIL)), unparsed());
    }

    @Test (expected = IllegalStateException.class)
    public void mismatchBetweenParseTypesCountAndRegexGroupsCountThrows() {
        Set<LexerDefinition> a = Sets.newHashSet();
        a.add(new LexerDefinition("(\\[)([-+](?:[0-9,]+|[oiOI]+))", VARIANCE_OPEN));

        Cell cell = buildCell("[-o]");

        ParsedCell parsedCell = cellLexer.lexCell(cell, a, "");
    }

    @Test (expected = IllegalStateException.class)
    public void notUsingRegexGroupsAndSupplyingMoreThanOneParseTypeThrows() {
        Set<LexerDefinition> a = Sets.newHashSet();
        a.add(new LexerDefinition("\\[", VARIANCE_OPEN, VARIANCE_DETAIL));

        Cell cell = buildCell("[-o]");

        ParsedCell parsedCell = cellLexer.lexCell(cell, a, "");
    }

    @Test (expected = IllegalArgumentException.class)
    public void notUsingRegexGroupsAndSupplyingLessThanOneParseTypeThrows() {
        Set<LexerDefinition> a = Sets.newHashSet();
        a.add(new LexerDefinition("\\["));

        Cell cell = buildCell("[-o]");

        ParsedCell parsedCell = cellLexer.lexCell(cell, a, "");
    }

    @Test
    public void matchComplexGroupsInsideOtherParsings() {
        Set<LexerDefinition> a = Sets.newHashSet();
        a.add(new LexerDefinition("(\\[)([-+](?:[0-9,]+|[oiOI]+))", VARIANCE_OPEN, VARIANCE_DETAIL));
        a.add(new LexerDefinition("\\]", VARIANCE_CLOSE));
        a.add(new LexerDefinition("-", CALL));
        a.add(new LexerDefinition("o", CALL));
        a.add(new LexerDefinition("\\s", WHITESPACE));

        Cell cell = buildCell("o[-o -]");

        ParsedCell parsedCell = cellLexer.lexCell(cell, a, "");

        assertParse(parsedCell, valid(CALL), valid(section(VARIANCE_OPEN), section(2, VARIANCE_DETAIL)), valid(WHITESPACE), valid(CALL), valid(VARIANCE_CLOSE));
    }

    @Test
    public void lexerDefinitionWithHighestPriorityTakesPrecedence() {
        Set<LexerDefinition> a = Sets.newHashSet();
        a.add(new LexerDefinition(PRIORITY_HIGHEST, "a", SPLICE));
        a.add(new LexerDefinition("dad", CALL));

        Cell cell = buildCell("sdadk");

        ParsedCell parsedCell = cellLexer.lexCell(cell, a, "");

        assertParse(parsedCell, unparsed(2), valid(SPLICE), unparsed(2));
    }

    @Test
    public void testRange() {

        Range<Integer> range = Range.openClosed(2, 3);

        log.info(range.toString());
        log.info("2 [{}]",range.contains(2));
        log.info("3 [{}]",range.contains(3));
    }


    private Cell buildCell(String characters) {
        return new CellBuilder().insert(0, characters).build();
    }

}