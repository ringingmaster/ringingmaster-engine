package org.ringingmaster.engine.parsernew.assignparsetype;

import org.ringingmaster.engine.parser.ParseType;
import org.ringingmaster.engine.parsernew.cell.Group;
import org.ringingmaster.engine.parsernew.cell.ParsedCell;
import org.ringingmaster.engine.parsernew.cell.Section;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private final static Logger log = LoggerFactory.getLogger(AssertParse.class);


    public static void assertParse(ParsedCell parsedCell, SectionExpected... expecteds) {
        assertNotNull(parsedCell);

        assertEquals(Arrays.stream(expecteds).mapToInt(e -> e.length).sum(), parsedCell.getElementSize());

        int elementIndex = 0;
        for (SectionExpected expected : expecteds) {
            Optional<Section> sectionAtFirstElementIndex = parsedCell.getSectionAtElementIndex(elementIndex);
            Optional<Group> groupAtFirstElementIndex = parsedCell.getGroupAtElementIndex(elementIndex);

            if (expected.parseType == null) {
                assertFalse(sectionAtFirstElementIndex.isPresent());
                assertFalse(groupAtFirstElementIndex.isPresent());

                for (int i=0;i<expected.length;i++) {
                    assertFalse(parsedCell.getSectionAtElementIndex(elementIndex).isPresent());
                    assertFalse(parsedCell.getGroupAtElementIndex(elementIndex).isPresent());
                    elementIndex++;
                }
            }
            else {
                assertTrue("Missing Section " + expected, sectionAtFirstElementIndex.isPresent());
                assertEquals(expected.parseType, sectionAtFirstElementIndex.get().getParseType());

                assertTrue(groupAtFirstElementIndex.isPresent());
                assertEquals(1, groupAtFirstElementIndex.get().getSections().size());
                assertEquals(sectionAtFirstElementIndex.get(), groupAtFirstElementIndex.get().getSections().get(0));

                for (int i = 0; i < expected.length; i++) {
                    assertEquals(sectionAtFirstElementIndex.get(), parsedCell.getSectionAtElementIndex(elementIndex).get());
                    assertEquals(sectionAtFirstElementIndex.get(), parsedCell.getGroupAtElementIndex(elementIndex).get().getSections().get(0));

                    elementIndex++;
                }
            }
            log.info("Section [{}] OK", expected);
        }
    }


    public static class SectionExpected {
        final int length;
        final ParseType parseType;

        SectionExpected(int length, ParseType parseType) {
            this.length = length;
            this.parseType = parseType;
        }

        @Override
        public String toString() {
            return "{" +
                    length +
                    ", " + parseType +
                    '}';
        }
    }

    public static SectionExpected parsed(ParseType parseType) {
        return parsed(1, parseType);
    }

    public static SectionExpected parsed(int length, ParseType parseType) {
        assertTrue(length > 0);
        return new SectionExpected(length, parseType);
    }

    public static SectionExpected unparsed(int length) {
        return parsed(length, null);
    }

    public static SectionExpected unparsed() {
        return parsed(1, null);
    }
}