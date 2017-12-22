package org.ringingmaster.engine.parser;

import org.ringingmaster.engine.parser.cell.Group;
import org.ringingmaster.engine.parser.cell.ParsedCell;
import org.ringingmaster.engine.parser.cell.Section;
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

        assertEquals("checking length", Arrays.stream(expecteds).mapToInt(e -> e.length).sum(), parsedCell.getElementSize());

        int elementIndex = 0;
        for (SectionExpected expected : expecteds) {
            log.info("Checking Section [{}]", expected);

            if (expected.parseType.isPresent()) {
                Optional<Section> sectionAtFirstElementIndex = parsedCell.getSectionAtElementIndex(elementIndex);
                Optional<Group> groupAtFirstElementIndex = parsedCell.getGroupAtElementIndex(elementIndex);
                assertTrue("Missing Section: " + expected, sectionAtFirstElementIndex.isPresent());
                assertEquals("Section: " + sectionAtFirstElementIndex.get().toString(), expected.parseType.get(), sectionAtFirstElementIndex.get().getParseType());

                // Assert the group matches the section.
                assertTrue(groupAtFirstElementIndex.isPresent());
                assertEquals(1, groupAtFirstElementIndex.get().getSections().size());
                assertEquals(sectionAtFirstElementIndex.get(), groupAtFirstElementIndex.get().getSections().get(0));

                assertEquals("Section validity", expected.valid, groupAtFirstElementIndex.get().isValid());

                // Assert all subsequent element points have the same Section
                for (int i = 0; i < expected.length; i++) {
                    assertEquals(sectionAtFirstElementIndex.get(), parsedCell.getSectionAtElementIndex(elementIndex).get());
                    assertEquals(sectionAtFirstElementIndex.get(), parsedCell.getGroupAtElementIndex(elementIndex).get().getSections().get(0));
                    elementIndex++;
                }
            } else {
                // Assert all sections and groups are empty.
                for (int i=0;i<expected.length;i++) {
                    assertFalse(parsedCell.getSectionAtElementIndex(elementIndex).isPresent());
                    assertFalse(parsedCell.getGroupAtElementIndex(elementIndex).isPresent());
                    elementIndex++;
                }
            }
        }
    }


    public static class SectionExpected {
        final int length;
        final Optional<ParseType> parseType;
        final boolean valid;

        SectionExpected(int length, ParseType parseType, boolean valid) {
            assertTrue(length > 0);

            this.length = length;
            this.parseType = Optional.ofNullable(parseType);
            this.valid = valid;
        }

        @Override
        public String toString() {
            return "{" +
                    length + ", " +
                    (parseType.map(parseType ->  parseType + (valid ? ", valid":", invalid")).orElse("unparsed")) +
                    '}';
        }
    }

    public static SectionExpected valid(ParseType parseType) {
        return valid(1, parseType);
    }

    public static SectionExpected valid(int length, ParseType parseType) {
        return new SectionExpected(length, parseType, true);
    }


    public static SectionExpected unparsed() {
        return unparsed(1);
    }

    public static SectionExpected unparsed(int length) {
        return new SectionExpected(length, null, false);
    }

    public static SectionExpected invalid(ParseType parseType) {
        return invalid(1, parseType);
    }

    public static SectionExpected invalid(int length, ParseType parseType) {
        return new SectionExpected(length, parseType, false);
    }

}