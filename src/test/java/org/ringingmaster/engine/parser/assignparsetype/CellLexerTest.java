package org.ringingmaster.engine.parser.assignparsetype;

import com.google.common.collect.Sets;
import org.junit.Test;
import org.ringingmaster.engine.parser.cell.ParsedCell;
import org.ringingmaster.engine.composition.cell.Cell;
import org.ringingmaster.engine.composition.cell.CellBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import static org.ringingmaster.engine.parser.AssertParse.assertParse;
import static org.ringingmaster.engine.parser.AssertParse.section;
import static org.ringingmaster.engine.parser.AssertParse.unparsed;
import static org.ringingmaster.engine.parser.AssertParse.valid;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.CALL;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.SPLICE;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.SPLICE_MULTIPLIER;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.VARIANCE_CLOSE;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.VARIANCE_DETAIL;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.VARIANCE_OPEN;


/**
 * TODO comments???
 *
 * @author Steve Lake
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
        a.add(new LexerDefinition(1,  0,"a", CALL));
        Cell cell = buildCell("zy");

        ParsedCell parsedCell = cellLexer.lexCell(cell, a, "");

        assertParse(parsedCell, unparsed(2));
    }

    @Test
    public void singleCharacterCellReturnsCorrectParseType() {
        Set<LexerDefinition> a = Sets.newHashSet();
        a.add(new LexerDefinition(1, 0,"a", CALL));
        Cell cell = buildCell("a");

        ParsedCell parsedCell = cellLexer.lexCell(cell, a, "");

        assertParse(parsedCell, valid(CALL));
    }

    @Test
    public void distinguishTwoNonOverlappingParseTypes() {
        Set<LexerDefinition> a = Sets.newHashSet();
        a.add(new LexerDefinition(1, 0,"a", CALL));
        a.add(new LexerDefinition(1, 0,"b", SPLICE));

        Cell cell = buildCell("ab");

        ParsedCell parsedCell = cellLexer.lexCell(cell, a, "");

        assertParse(parsedCell, valid(CALL), valid(SPLICE));
    }

    @Test
    public void overlappingTokensUseHigherPrecedenceFirst() {
        Set<LexerDefinition> a = Sets.newHashSet();
        a.add(new LexerDefinition(1, 0,"ab", CALL));
        a.add(new LexerDefinition(2, 0,"bcd", SPLICE));

        Cell cell = buildCell("abcd");

        ParsedCell parsedCell = cellLexer.lexCell(cell, a, "");

        assertParse(parsedCell, unparsed(1), valid(3, SPLICE));
    }

    @Test
    public void extendedRightTokensUseHigherPrecedenceFirst() {
        Set<LexerDefinition> a = Sets.newHashSet();
        a.add(new LexerDefinition(1, 0,"ab", CALL));
        a.add(new LexerDefinition(2, 0,"abc", SPLICE));

        Cell cell = buildCell("abc");

        ParsedCell parsedCell = cellLexer.lexCell(cell, a, "");

        assertParse(parsedCell, valid(3, SPLICE));
    }

    @Test
    public void extendedLeftTokensUseLongerTokenFirst() {
        Set<LexerDefinition> a = Sets.newHashSet();
        a.add(new LexerDefinition(1, 0,"bc", CALL));
        a.add(new LexerDefinition(1, 0,"abc", SPLICE));

        Cell cell = buildCell("abc");

        ParsedCell parsedCell = cellLexer.lexCell(cell, a, "");

        assertParse(parsedCell, valid(3, SPLICE));
    }

    @Test
    public void whitespaceInTokenGetsMarkedAsToken() {
        Set<LexerDefinition> a = Sets.newHashSet();
        a.add(new LexerDefinition(2, 0,"a b", CALL));

        Cell cell = buildCell("xa b s");

        ParsedCell parsedCell = cellLexer.lexCell(cell, a, "");

        assertParse(parsedCell, unparsed(1), valid(3, CALL), unparsed(2));

    }

    @Test
    public void backToBackParsingsDoNotResultInGaps() {
        Set<LexerDefinition> a = Sets.newHashSet();
        a.add(new LexerDefinition(1,  0,"-", CALL));

        Cell cell = buildCell("- ");

        ParsedCell parsedCell = cellLexer.lexCell(cell, a, "");

        assertParse(parsedCell, valid(CALL), unparsed());

    }


    @Test
    public void matchingRegexWithMultipleGroupsAddsMultipleGroups() {
        Set<LexerDefinition> a = Sets.newHashSet();
        a.add(new LexerDefinition(1, 0,"(\\[)([-+](?:[0-9,]+|[oiOI]+))", VARIANCE_OPEN, VARIANCE_DETAIL));

        Cell cell = buildCell("[-o]");

        ParsedCell parsedCell = cellLexer.lexCell(cell, a, "");

        assertParse(parsedCell, valid(section(VARIANCE_OPEN), section(2, VARIANCE_DETAIL)), unparsed());
    }

    @Test (expected = IllegalStateException.class)
    public void mismatchBetweenParseTypesCountAndRegexGroupsCountThrows() {
        Set<LexerDefinition> a = Sets.newHashSet();
        a.add(new LexerDefinition(1, 0,"(\\[)([-+](?:[0-9,]+|[oiOI]+))", VARIANCE_OPEN));

        Cell cell = buildCell("[-o]");

        ParsedCell parsedCell = cellLexer.lexCell(cell, a, "");
    }

    @Test (expected = IllegalStateException.class)
    public void notUsingRegexGroupsAndSupplyingMoreThanOneParseTypeThrows() {
        Set<LexerDefinition> a = Sets.newHashSet();
        a.add(new LexerDefinition(1, 0,"\\[", VARIANCE_OPEN, VARIANCE_DETAIL));

        Cell cell = buildCell("[-o]");

        ParsedCell parsedCell = cellLexer.lexCell(cell, a, "");
    }

    @Test (expected = IllegalArgumentException.class)
    public void notUsingRegexGroupsAndSupplyingLessThanOneParseTypeThrows() {
        Set<LexerDefinition> a = Sets.newHashSet();
        a.add(new LexerDefinition(1, 0,"\\["));

        Cell cell = buildCell("[-o]");

        ParsedCell parsedCell = cellLexer.lexCell(cell, a, "");
    }

    @Test
    public void matchComplexGroupsInsideOtherParsings() {
        Set<LexerDefinition> a = Sets.newHashSet();
        a.add(new LexerDefinition(5, 0, "(\\[)([-+](?:[0-9,]+|[oiOI]+))", VARIANCE_OPEN, VARIANCE_DETAIL));
        a.add(new LexerDefinition(4, 0, "\\]", VARIANCE_CLOSE));
        a.add(new LexerDefinition(3, 0, "-", CALL));
        a.add(new LexerDefinition(2, 0, "o", CALL));

        Cell cell = buildCell("o[-o -]");

        ParsedCell parsedCell = cellLexer.lexCell(cell, a, "");

        assertParse(parsedCell, valid(CALL), valid(section(VARIANCE_OPEN), section(2, VARIANCE_DETAIL)), unparsed(), valid(CALL), valid(VARIANCE_CLOSE));
    }

    @Test
    public void matchEmptyGroupGivesNoParseGroup() {
        Set<LexerDefinition> a = Sets.newHashSet();
        a.add(new LexerDefinition(1, 0,"(a)(b*)(c)", VARIANCE_OPEN, VARIANCE_DETAIL, VARIANCE_CLOSE));

        Cell cell = buildCell("ac");

        ParsedCell parsedCell = cellLexer.lexCell(cell, a, "");

        assertParse(parsedCell, valid(section(VARIANCE_OPEN), section(VARIANCE_CLOSE)));
    }

    @Test
    public void lexerDefinitionWithHighestPriorityTakesPrecedence() {
        Set<LexerDefinition> a = Sets.newHashSet();
        a.add(new LexerDefinition(10, 5, "a", SPLICE));
        a.add(new LexerDefinition(5, 10, "dad", CALL));

        Cell cell = buildCell("sdadk");

        ParsedCell parsedCell = cellLexer.lexCell(cell, a, "");

        assertParse(parsedCell, unparsed(2), valid(SPLICE), unparsed(2));
    }

    @Test
    public void lexerDefinitionWithHighestSubPriorityWithinTakesPrecedence() {
        Set<LexerDefinition> a = Sets.newHashSet();
        a.add(new LexerDefinition(1, 10, "a", SPLICE));
        a.add(new LexerDefinition(1, 5, "dad", CALL));

        Cell cell = buildCell("sdadk");

        ParsedCell parsedCell = cellLexer.lexCell(cell, a, "");

        assertParse(parsedCell, unparsed(2), valid(SPLICE), unparsed(2));
    }

    @Test
    public void lexerDefinitionWithHighestPriorityAndGroupsTakesPrecedence() {
        Set<LexerDefinition> a = Sets.newHashSet();
        a.add(new LexerDefinition(10, 0, "(a)(b)", SPLICE_MULTIPLIER,SPLICE));
        a.add(new LexerDefinition(5, 0, "dad", CALL));

        Cell cell = buildCell("sdabdk");

        ParsedCell parsedCell = cellLexer.lexCell(cell, a, "");

        assertParse(parsedCell, unparsed(2), valid(section(SPLICE_MULTIPLIER), section(SPLICE)), unparsed(2));
    }


    @Test
    public void escapedRegexCharactersMatchLiteral() {
        Set<LexerDefinition> a = Sets.newHashSet();
        a.add(new LexerDefinition(1, 0, "3\\*", CALL));

        Cell cell = buildCell("3*");

        ParsedCell parsedCell = cellLexer.lexCell(cell, a, "");

        assertParse(parsedCell, valid(2,CALL));
    }


    private Cell buildCell(String characters) {
        return new CellBuilder().insert(0, characters).build();
    }

}