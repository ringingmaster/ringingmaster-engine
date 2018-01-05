package org.ringingmaster.engine.parser;

import org.ringingmaster.engine.parser.cell.Group;
import org.ringingmaster.engine.parser.cell.ParsedCell;
import org.ringingmaster.engine.parser.cell.Section;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;
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


    public static void assertParse(ParsedCell parsedCell, Expected... expecteds) {
        assertNotNull(parsedCell);

        assertEquals("expected length does not match actual length", Arrays.stream(expecteds).mapToInt(Expected::getLength).sum(), parsedCell.getElementSize());

        int elementIndex = 0;
        for (Expected expected : expecteds) {

            if (expected instanceof GroupExpected) {
                GroupExpected groupExpected = (GroupExpected) expected;

                log.info("Start Group [{}]", elementIndex);
                if (groupExpected.groupMessage.isPresent()) {
                    assertEquals("Group message", groupExpected.groupMessage, parsedCell.getGroupAtElementIndex(elementIndex).get().getMessage());
                }
                int sectionIndexInGroup = 0;
                for (SectionExpected sectionExpected : groupExpected.sectionExpecteds) {
                    log.info("  Checking Section [{}, {}]", elementIndex, sectionExpected);
                    elementIndex = assertSection(parsedCell, elementIndex, sectionExpected, sectionIndexInGroup++);
                }
                log.info("End Group [{}]", elementIndex);
            }
            else if (expected instanceof GroupSectionExpected) {
                GroupSectionExpected groupSectionExpected = (GroupSectionExpected) expected;

                log.info("Checking Group Section [{}, {}]", elementIndex, groupSectionExpected);
                assertGroupForSingleSection(parsedCell, elementIndex, groupSectionExpected);
                elementIndex = assertSection(parsedCell, elementIndex, groupSectionExpected,0);

            }
            else if (expected instanceof UnparsedExpected) {
                UnparsedExpected unparsedExpected = (UnparsedExpected) expected;

                log.info("Checking Unparsed [{}, {}]", elementIndex, unparsedExpected);

                // Assert all sections and groups are empty.
                for (int i = 0; i < unparsedExpected.length; i++) {
                    assertFalse(parsedCell.getSectionAtElementIndex(elementIndex).isPresent());
                    assertFalse(parsedCell.getGroupAtElementIndex(elementIndex).isPresent());
                    elementIndex++;
                }
            }
        }
    }

    private static void assertGroupForSingleSection(ParsedCell parsedCell, int elementIndex, GroupSectionExpected groupSectionExpected) {
        // Assert the group matches the section.

        Optional<Section> sectionAtFirstElementIndex = parsedCell.getSectionAtElementIndex(elementIndex);
        Optional<Group> groupAtFirstElementIndex = parsedCell.getGroupAtElementIndex(elementIndex);
        assertTrue("No group at elementIndex [" + elementIndex + "]", groupAtFirstElementIndex.isPresent());
        assertEquals("Incorrect number of Sections", 1, groupAtFirstElementIndex.get().getSections().size());
        assertEquals(sectionAtFirstElementIndex.get(), groupAtFirstElementIndex.get().getSections().get(0));

        assertEquals("Group validity", groupSectionExpected.validGroup, groupAtFirstElementIndex.get().isValid());
        if (groupSectionExpected.groupMessage.isPresent()) {
            assertEquals("Group message", groupSectionExpected.groupMessage, groupAtFirstElementIndex.get().getMessage());
        }
    }

    private static int assertSection(ParsedCell parsedCell, int elementIndex, SectionExpected sectionExpected, int sectionIndexInGroup) {
        Optional<Section> sectionAtFirstElementIndex = parsedCell.getSectionAtElementIndex(elementIndex);
        assertTrue("Missing Section: " + sectionExpected, sectionAtFirstElementIndex.isPresent());
        assertEquals("Section: " + sectionAtFirstElementIndex.get().toString(), sectionExpected.parseType, sectionAtFirstElementIndex.get().getParseType());

        for (int i = 0; i < sectionExpected.length; i++) {
            // Checking all Section index's in section point to the same Section
            assertEquals(sectionAtFirstElementIndex.get(), parsedCell.getSectionAtElementIndex(elementIndex).get());
            // Checking all Groups index's in section point to the same Section
            assertEquals(sectionAtFirstElementIndex.get(), parsedCell.getGroupAtElementIndex(elementIndex).get().getSections().get(sectionIndexInGroup));
            elementIndex++;
        }
        return elementIndex;
    }


    public interface Expected {
        int getLength();
    }

    private static class GroupExpected implements Expected {

        final Optional<String> groupMessage;
        final SectionExpected[] sectionExpecteds;

        GroupExpected(Optional<String> groupMessage, SectionExpected... sectionExpecteds) {
            this.sectionExpecteds = sectionExpecteds;
            this.groupMessage = groupMessage;
        }

        @Override
        public int getLength() {
            return Stream.of(sectionExpecteds)
                    .mapToInt(SectionExpected::getLength)
                    .sum();
        }
    }

    public static class UnparsedExpected implements Expected {

        final int length;

        UnparsedExpected(int length) {
            assertTrue(length > 0);

            this.length = length;
        }

        @Override
        public int getLength() {
            return length;
        }

        @Override
        public String toString() {
            return "{" +
                    length + ", " +
                    "unparsed" +
                    '}';
        }
    }


    public static class SectionExpected extends UnparsedExpected {

        final ParseType parseType;

        SectionExpected(int length, ParseType parseType) {
            super(length);
            this.parseType = checkNotNull(parseType);
        }

        @Override
        public String toString() {
            return "{" +
                    length + ", " +
                    parseType +
                    '}';
        }
    }


    /**
     * Used where a section and group align, and specifies both the group and section semantics.
     */
    public static class GroupSectionExpected extends SectionExpected {
        final boolean validGroup;
        final Optional<String> groupMessage;

        GroupSectionExpected(int length, ParseType parseType, boolean groupVaid, Optional<String> groupMessage) {
            super(length, parseType);
            this.validGroup = groupVaid;
            this.groupMessage = checkNotNull(groupMessage);
        }

        @Override
        public String toString() {
            return "{" +
                    length + ", " +
                    parseType + ", " +
                    (validGroup ? "valid":"invalid") +
                    '}';
        }
    }




    public static Expected valid(SectionExpected... sectionExpecteds) {
        return new GroupExpected(Optional.empty(), sectionExpecteds);
    }

    public static Expected invalid(String messge, SectionExpected... sectionExpecteds) {
        return new GroupExpected(Optional.of(messge), sectionExpecteds);
    }

    public static Expected unparsed() {
        return new UnparsedExpected(1);
    }

    public static Expected unparsed(int length) {
        return new UnparsedExpected(length);
    }


    public static SectionExpected section(ParseType parseType) {
        return new SectionExpected(1, parseType);
    }

    public static SectionExpected section(int length, ParseType parseType) {
        return new SectionExpected(length, parseType);
    }


    public static Expected valid(ParseType parseType) {
        return valid(1, parseType);
    }

    public static Expected valid(int length, ParseType parseType) {
        return new GroupSectionExpected(length, parseType, true, Optional.empty());
    }

    public static Expected invalid(ParseType parseType) {
        return new GroupSectionExpected(1, parseType, false, Optional.empty());
    }

    public static Expected invalid(int length, ParseType parseType) {
        return new GroupSectionExpected(length, parseType, false, Optional.empty());
    }

    public static Expected invalid(int length, ParseType parseType, String message) {
        return new GroupSectionExpected(length, parseType, false, Optional.of(message));
    }
}