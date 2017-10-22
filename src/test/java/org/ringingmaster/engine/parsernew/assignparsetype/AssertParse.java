package org.ringingmaster.engine.parsernew.assignparsetype;

import org.ringingmaster.engine.parser.ParseType;
import org.ringingmaster.engine.parsernew.cell.Group;
import org.ringingmaster.engine.parsernew.cell.ParsedCell;
import org.ringingmaster.engine.parsernew.cell.Section;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * TODO comments???
 *
 * @author stevelake
 */

public class AssertParse {


    public static void assertParse(ParsedCell parsedCell, SectionExpected... expecteds) {
        assertNotNull(parsedCell);

        assertEquals(Arrays.stream(expecteds).mapToInt(e -> e.length).sum(), parsedCell.getElementSize());

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


    public static class SectionExpected {
        final int length;
        final ParseType parseType;

        SectionExpected(int length, ParseType parseType) {
            this.length = length;
            this.parseType = parseType;
        }
    }

    public static SectionExpected parsed(ParseType parseType) {
        return parsed(1, parseType);
    }

    public static SectionExpected parsed(int length, ParseType parseType) {
        return new SectionExpected(length, parseType);
    }

    public static SectionExpected unparsed(int length) {
        return parsed(length, null);
    }

}