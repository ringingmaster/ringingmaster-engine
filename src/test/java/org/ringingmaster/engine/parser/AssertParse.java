package org.ringingmaster.engine.parser;

import com.google.common.base.Predicate;
import org.ringingmaster.engine.parser.assignparsetype.ParseType;
import org.ringingmaster.engine.parser.cell.ParsedCell;
import org.ringingmaster.engine.parser.cell.grouping.Group;
import org.ringingmaster.engine.parser.cell.grouping.Section;
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

        log.info("Pretty Print of ParsedCell {}{}", System.lineSeparator(), parsedCell.prettyPrint());

        assertNotNull(parsedCell);

        assertEquals("expected length does not match actual length", Arrays.stream(expecteds).mapToInt(Expected::getLength).sum(), parsedCell.getElementSize());

        long unexpectedGroupCount = Arrays.stream(expecteds).filter((Predicate<Expected>) input -> !(input instanceof UnparsedExpected)).count();
        assertEquals("expected top level count [" + unexpectedGroupCount + "] does not match group count [" + parsedCell.allGroups() .size() + "]", unexpectedGroupCount, parsedCell.allGroups().size());

        int elementIndex = 0;
        for (Expected expected : expecteds) {

            if (expected instanceof GroupExpected) {
                GroupExpected groupExpected = (GroupExpected) expected;

                log.info("Start Group at index [{}]", elementIndex);

                assertEquals("Group validity", groupExpected.validGroup, parsedCell.getGroupAtElementIndex(elementIndex).get().isValid());

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
            else if (expected instanceof UnparsedExpected) {
                UnparsedExpected unparsedExpected = (UnparsedExpected) expected;

                log.info("Checking Unparsed [{}, {}]", elementIndex, unparsedExpected);
                // Assert all sections and groups are empty.
                for (int i = 0; i < unparsedExpected.length; i++) {
                    assertFalse("Section is present when it should be unparsed [" + elementIndex + "], [" + parsedCell.getSectionAtElementIndex(elementIndex) + "]",
                            parsedCell.getSectionAtElementIndex(elementIndex).isPresent());
                    assertFalse("Group is present when it should be unparsed [" + elementIndex + "], [" + parsedCell.getGroupAtElementIndex(elementIndex) + "]",
                            parsedCell.getGroupAtElementIndex(elementIndex).isPresent());
                    elementIndex++;
                }
            }
            else if (expected instanceof SectionExpected) {
                throw new IllegalStateException("SectionExpected onlay valid in a GroupExpected");
            }
        }
    }

    private static int assertSection(ParsedCell parsedCell, int elementIndex, SectionExpected sectionExpected, int sectionIndexInGroup) {
        Optional<Section> sectionAtFirstElementIndex = parsedCell.getSectionAtElementIndex(elementIndex);
        assertTrue("Missing Section: " + sectionExpected, sectionAtFirstElementIndex.isPresent());
        assertEquals("Section ParseType wrong: " + sectionAtFirstElementIndex.get().toString(), sectionExpected.parseType, sectionAtFirstElementIndex.get().getParseType());
        assertEquals("Section length wrong: " + sectionAtFirstElementIndex.get().toString(), sectionExpected.length, sectionAtFirstElementIndex.get().getLength());

        for (int sectionIndex = 0; sectionIndex < sectionExpected.length; sectionIndex++) {
            // Checking all Section index's in section point to the same Section
            final Optional<Section> sectionAtElementIndex = parsedCell.getSectionAtElementIndex(elementIndex);
            assertTrue("Missing Section at element index [" + elementIndex + "]", sectionAtElementIndex.isPresent());
            assertEquals("Mismatch in Section lookup. Section Index [" + sectionIndex + "], Element Index [" + elementIndex + "]",
                    sectionAtFirstElementIndex.get(), sectionAtElementIndex.get());

            // Checking all Groups index's in section bounds point to the same Section
            final Optional<Group> groupAtElementIndex = parsedCell.getGroupAtElementIndex(elementIndex);
            assertTrue("Missing Group at element index [" + elementIndex + "]", groupAtElementIndex.isPresent());
            assertTrue("Getting group at element index [" + elementIndex + "], then finding the groups sections does not have a section at index : sectionIndexInGroup[" + sectionIndexInGroup + "]",
                    sectionIndexInGroup < groupAtElementIndex.get().getSections().size());
            assertEquals("Mismatch in group indexes: Section Index [" + sectionIndex + "], Element Index [" + elementIndex + "], sectionIndexInGroup[" + sectionIndexInGroup + "]",
                    sectionAtFirstElementIndex.get(), groupAtElementIndex.get().getSections().get(sectionIndexInGroup));
            elementIndex++;
        }
        return elementIndex;
    }


    public interface Expected {
        int getLength();
    }

    private static class GroupExpected implements Expected {

        final boolean validGroup;
        final Optional<String> groupMessage;
        final SectionExpected[] sectionExpecteds;

        GroupExpected(boolean validGroup, Optional<String> groupMessage, SectionExpected... sectionExpecteds) {
            this.validGroup = validGroup;
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


    public static class SectionExpected implements Expected {

        final int length;
        final ParseType parseType;

        SectionExpected(int length, ParseType parseType) {
            assertTrue(length > 0);
            this.parseType = checkNotNull(parseType);
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
                    parseType +
                    '}';
        }
    }


    // groups

    public static Expected valid(SectionExpected... sectionExpecteds) {
        return new GroupExpected(true, Optional.empty(), sectionExpecteds);
    }

    public static Expected invalid(SectionExpected... sectionExpecteds) {
        return new GroupExpected(false, Optional.empty(), sectionExpecteds);
    }

    public static Expected invalid(String message, SectionExpected... sectionExpecteds) {
        return new GroupExpected(false, Optional.of(message), sectionExpecteds);
    }

    // sections

    public static SectionExpected section(ParseType parseType) {
        return new SectionExpected(1, parseType);
    }

    public static SectionExpected section(int length, ParseType parseType) {
        return new SectionExpected(length, parseType);
    }


    // groups & section aligns

    public static Expected valid(ParseType parseType) {
        return valid(1, parseType);
    }

    public static Expected valid(int length, ParseType parseType) {
        return valid(section(length, parseType));
    }

    public static Expected invalid(ParseType parseType) {
        return invalid(section(parseType));
    }

    public static Expected invalid(int length, ParseType parseType) {
        return invalid(section(length,parseType));
    }

    public static Expected invalid(int length, ParseType parseType, String message) {
        return invalid(message, section(length,parseType));
    }

    public static Expected invalid(ParseType parseType, String message) {
        return invalid(message, section(1, parseType));
    }


    public static Expected unparsed() {
        return new UnparsedExpected(1);
    }

    public static Expected unparsed(int length) {
        return new UnparsedExpected(length);
    }
}