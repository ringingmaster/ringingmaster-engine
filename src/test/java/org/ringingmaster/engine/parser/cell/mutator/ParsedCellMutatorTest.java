package org.ringingmaster.engine.parser.cell.mutator;


import com.google.common.collect.Sets;
import org.junit.Test;
import org.ringingmaster.engine.parser.cell.ParsedCell;
import org.ringingmaster.engine.parser.cell.ParsedCellFactory;
import org.ringingmaster.engine.parser.cell.grouping.GroupingFactory;
import org.ringingmaster.engine.parser.cell.grouping.Section;
import org.ringingmaster.engine.touch.cell.Cell;
import org.ringingmaster.engine.touch.element.Element;

import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.ringingmaster.engine.parser.AssertParse.assertParse;
import static org.ringingmaster.engine.parser.AssertParse.invalid;
import static org.ringingmaster.engine.parser.AssertParse.valid;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.CALL;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.CALL_MULTIPLIER;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.PLAIN_LEAD;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class ParsedCellMutatorTest {

    @Test
    public void buildingFromPrototypeWithNoChangeEmitsIdenticalObject() {
        ParsedCell parsedCell = buildParsedCell();
        ParsedCell builtCell = new ParsedCellMutator()
                .prototypeOf(parsedCell).build();
        assertParse(builtCell, valid(2, CALL), valid(CALL_MULTIPLIER), valid(3, PLAIN_LEAD));

    }

    @Test
    public void settingGroupInvalidEmmittsInvalidGroupWithCorrectMessage() {

        ParsedCell parsedCell = buildParsedCell();

        ParsedCell builtCell = new ParsedCellMutator()
                .prototypeOf(parsedCell)
                .invalidateGroup(0, "MESSAGE")
                .build();

        assertParse(builtCell, invalid(2, CALL), valid(CALL_MULTIPLIER), valid(3, PLAIN_LEAD));
        assertEquals("MESSAGE", builtCell.getGroupAtElementIndex(0).get().getMessage().get());
    }

    @Test
    public void settingInvalidInTwoRunsConcatanatesTheMessage() {

        ParsedCell parsedCell = buildParsedCell();

        ParsedCell builtCell1 = new ParsedCellMutator()
                .prototypeOf(parsedCell)
                .invalidateGroup(0, "MESSAGE")
                .build();

        ParsedCell builtCell2 = new ParsedCellMutator()
                .prototypeOf(builtCell1)
                .invalidateGroup(0, "ADDITIONAL")
                .build();

        assertParse(builtCell1, invalid(2, CALL, "MESSAGE"), valid(CALL_MULTIPLIER), valid(3, PLAIN_LEAD));
        assertParse(builtCell2, invalid(2, CALL, "MESSAGE, ADDITIONAL"), valid(CALL_MULTIPLIER), valid(3, PLAIN_LEAD));
    }

    @Test
    public void settingMutipleGroupsInvalidCorrectlyRebuildsGroups() {

        ParsedCell parsedCell = buildParsedCell();

        ParsedCell builtCell = new ParsedCellMutator()
                .prototypeOf(parsedCell)
                .invalidateGroup(3, "MESSAGE3")
                .invalidateGroup(2, "MESSAGE2")
                .invalidateGroup(0, "MESSAGE1")
                .build();

        assertParse(builtCell,
                invalid(2, CALL, "MESSAGE1"),
                invalid(1, CALL_MULTIPLIER, "MESSAGE2"),
                invalid(3, PLAIN_LEAD, "MESSAGE3"));
    }

    //TODO clean up after removing functionality

//    @Test
//    public void mergingGroupsCorrectlyCombinesLength() {
//
//        ParsedCell parsedCell = buildParsedCell();
//
//        ParsedCell builtCell = new ParsedCellMutator()
//                .prototypeOf(parsedCell)
//                .mergeGroups(Sets.newHashSet(parsedCell.getGroupAtElementIndex(0).get(), parsedCell.getGroupAtElementIndex(2).get()))
//                .build();
//
//        assertParse(builtCell,
//                valid(section(2, CALL), section(CALL_MULTIPLIER)),
//                valid(3, PLAIN_LEAD));
//    }

//    @Test
//    public void invalidatingAndMergingGroupsCorrectlyCombinesLengthAndInvalidState() {
//
//        ParsedCell parsedCell = buildParsedCell();
//
//        ParsedCell builtCell = new ParsedCellMutator()
//                .prototypeOf(parsedCell)
//                .invalidateGroup(0, "MESSAGE")
//                .mergeGroups(Sets.newHashSet(parsedCell.getGroupAtElementIndex(0).get(), parsedCell.getGroupAtElementIndex(2).get()))
//                .build();
//
//        assertEquals(builtCell.getGroupAtElementIndex(0).get(), builtCell.getGroupAtElementIndex(2).get());
//        assertEquals(0, builtCell.getGroupAtElementIndex(0).get().getStartIndex());
//        assertEquals(3, builtCell.getGroupAtElementIndex(0).get().getLength());
//        assertEquals(2, builtCell.getGroupAtElementIndex(0).get().getSections().size());
//        assertEquals("MESSAGE", builtCell.getGroupAtElementIndex(0).get().getMessage().get());
//        assertFalse(builtCell.getGroupAtElementIndex(0).get().isValid());
//    }

//    @Test
//    public void mergingCorrectlyCombinesMessage() {
//
//        ParsedCell parsedCell = buildParsedCell();
//
//        ParsedCell builtCell = new ParsedCellMutator()
//                .prototypeOf(parsedCell)
//                .invalidateGroup(0, "MESSAGE")
//                .invalidateGroup(3, "COMBINED")
//                .mergeGroups(Sets.newHashSet(parsedCell.getGroupAtElementIndex(0).get(),
//                        parsedCell.getGroupAtElementIndex(2).get(),
//                        parsedCell.getGroupAtElementIndex(3).get()))
//                .build();
//
//        assertParse(builtCell, invalid( "MESSAGE,COMBINED", section(2, CALL), section(1, CALL_MULTIPLIER), section(3, PLAIN_LEAD)));
//    }

//    @Test
//    public void mergingMultipleGroupsCorrectlyCombinesLength() {
//
//        ParsedCell parsedCell = buildParsedCell();
//
//        ParsedCell builtCell = new ParsedCellMutator()
//                .prototypeOf(parsedCell)
//                .mergeGroups(Sets.newHashSet(parsedCell.getGroupAtElementIndex(0).get(), parsedCell.getGroupAtElementIndex(2).get(), parsedCell.getGroupAtElementIndex(3).get()))
//                .build();
//
//        assertParse(builtCell, valid(section(2, CALL), section(1, CALL_MULTIPLIER), section(3, PLAIN_LEAD)));
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public void overlappingMergeSetsThrows() {
//
//        ParsedCell parsedCell = buildParsedCell();
//
//        new ParsedCellMutator()
//                .prototypeOf(parsedCell)
//                .mergeGroups(Sets.newHashSet(parsedCell.getGroupAtElementIndex(0).get(), parsedCell.getGroupAtElementIndex(2).get()))
//                .mergeGroups(Sets.newHashSet(parsedCell.getGroupAtElementIndex(2).get(), parsedCell.getGroupAtElementIndex(3).get()));
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public void mergingNonContiguousGroupsThrows() {
//
//        ParsedCell parsedCell = buildParsedCell();
//
//        new ParsedCellMutator()
//                .prototypeOf(parsedCell)
//                .mergeGroups(Sets.newHashSet(parsedCell.getGroupAtElementIndex(0).get(), parsedCell.getGroupAtElementIndex(3).get()));
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public void mergingLessThanTwoGroupsThrows() {
//
//        ParsedCell parsedCell = buildParsedCell();
//
//        new ParsedCellMutator()
//                .prototypeOf(parsedCell)
//                .mergeGroups(Sets.newHashSet(parsedCell.getGroupAtElementIndex(0).get()));
//    }
//
//    // TODO Merge out of order groups
//
//    @Test
//    public void addingNewSectionToNewGroup() {
//        ParsedCell parsedCell = buildParsedCellWithGap();
//
//        ParsedCell builtCell = new ParsedCellMutator()
//                .prototypeOf(parsedCell)
//                .addSectionAndGenerateMatchingNewGroup(GroupingFactory.buildSection(1, 2, PLAIN_LEAD_MULTIPLIER))
//                .build();
//
//        assertParse(builtCell, valid(CALL), valid(2, PLAIN_LEAD_MULTIPLIER), valid(PLAIN_LEAD), unparsed(2));
//    }
//
//    @Test//(expected = IllegalArgumentException.class)
//    public void addingNewSectionOverlappingExistingSectionThrows() {
//        ParsedCell parsedCell = buildParsedCellWithGap();
//
//        new ParsedCellMutator()
//                .prototypeOf(parsedCell)
//                .addSectionAndGenerateMatchingNewGroup(GroupingFactory.buildSection(3, 1, PLAIN_LEAD_MULTIPLIER))
//                .build();
//        }
//
//    @Test
//    public void addingNewSectionToStartOfExistingGroup() {
//        ParsedCell parsedCell = buildParsedCellWithGap();
//
//        ParsedCell builtCell = new ParsedCellMutator()
//                .prototypeOf(parsedCell)
//                .addSectionIntoExistingGroup(GroupingFactory.buildSection(1, 2, PLAIN_LEAD_MULTIPLIER),3)
//                .build();
//
//        assertParse(builtCell, valid(CALL), valid(section(2, PLAIN_LEAD_MULTIPLIER), section(PLAIN_LEAD)), unparsed(2));
//
//    }
//
//    @Test
//    public void addingTwoNewSectionsToStartOfExistingGroup() {
//        ParsedCell parsedCell = buildParsedCellWithGap();
//
//        ParsedCell builtCell = new ParsedCellMutator()
//                .prototypeOf(parsedCell)
//                .addSectionIntoExistingGroup(GroupingFactory.buildSection(1, 1, PLAIN_LEAD_MULTIPLIER),3)
//                .addSectionIntoExistingGroup(GroupingFactory.buildSection(2, 1, CALL_MULTIPLIER),3)
//                .build();
//
//        assertParse(builtCell, valid(CALL), valid(section(PLAIN_LEAD_MULTIPLIER), section(CALL_MULTIPLIER), section(PLAIN_LEAD)), unparsed(2));
//    }

//    @Test
//    public void addingNewSectionToEndOfExistingGroup() {
//        ParsedCell parsedCell = buildParsedCellWithGap();
//
//        ParsedCell builtCell = new ParsedCellMutator()
//                .prototypeOf(parsedCell)
//                .addSectionIntoExistingGroup(GroupingFactory.buildSection(4, 2, PLAIN_LEAD_MULTIPLIER),3)
//                .build();
//
//        assertParse(builtCell, valid(CALL), unparsed(2), valid(section(PLAIN_LEAD),section(2, PLAIN_LEAD_MULTIPLIER)));
//
//    }
//
//    @Test
//    public void addingTwoNewSectionsToEndOfExistingGroup() {
//        ParsedCell parsedCell = buildParsedCellWithGap();
//
//        ParsedCell builtCell = new ParsedCellMutator()
//                .prototypeOf(parsedCell)
//                .addSectionIntoExistingGroup(GroupingFactory.buildSection(4, 1, PLAIN_LEAD_MULTIPLIER),3)
//                .addSectionIntoExistingGroup(GroupingFactory.buildSection(5, 1, CALL_MULTIPLIER),3)
//                .build();
//
//        assertParse(builtCell, valid(CALL), unparsed(2), valid(section(PLAIN_LEAD), section(PLAIN_LEAD_MULTIPLIER), section(CALL_MULTIPLIER)));
//    }
//
//    @Test
//    public void addingANewSectionsToEachEndOfExistingGroup() {
//        ParsedCell parsedCell = buildParsedCellWithGap();
//
//        ParsedCell builtCell = new ParsedCellMutator()
//                .prototypeOf(parsedCell)
//                .addSectionIntoExistingGroup(GroupingFactory.buildSection(1, 2, PLAIN_LEAD_MULTIPLIER),3)
//                .addSectionIntoExistingGroup(GroupingFactory.buildSection(4, 2, CALL_MULTIPLIER),3)
//                .build();
//
//        assertParse(builtCell, valid(CALL), valid(section(2, PLAIN_LEAD_MULTIPLIER),  section(PLAIN_LEAD), section(2, CALL_MULTIPLIER)));
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public void addingOverlappingNewSectionThrows() {
//        ParsedCell parsedCell = buildParsedCellWithGap();
//
//        new ParsedCellMutator()
//                .prototypeOf(parsedCell)
//                .addSectionIntoExistingGroup(GroupingFactory.buildSection(2, 3, PLAIN_LEAD_MULTIPLIER),3)
//                .build();
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public void addingNonContiguousSectionThrows() {
//        ParsedCell parsedCell = buildParsedCellWithGap();
//
//        new ParsedCellMutator()
//                .prototypeOf(parsedCell)
//                .addSectionIntoExistingGroup(GroupingFactory.buildSection(1, 1, PLAIN_LEAD_MULTIPLIER),3)
//                .build();
//    }
//
//    @Test(expected = IndexOutOfBoundsException.class)
//    public void addingSectionOutsideParentCellThrows() {
//        ParsedCell parsedCell = buildParsedCell();
//
//        new ParsedCellMutator()
//                .prototypeOf(parsedCell)
//                .addSectionIntoExistingGroup(GroupingFactory.buildSection(6, 1, PLAIN_LEAD_MULTIPLIER),3)
//                .build();
//    }
//
//    @Test
//    public void widenSectionToRightReturnsWiderSection() {
//        ParsedCell parsedCell = buildParsedCellWithGap();
//
//        ParsedCell builtCell = new ParsedCellMutator()
//                .prototypeOf(parsedCell)
//                .widenSectionRight(0, 2)
//                .build();
//
//        assertParse(builtCell, valid(3, CALL), valid( PLAIN_LEAD), unparsed(2));
//    }

//    @Test
//    public void widenSectionCompoundToRightReturnsWiderSection() {
//        ParsedCell parsedCell = buildParsedCellWithGap();
//
//        ParsedCell builtCell = new ParsedCellMutator()
//                .prototypeOf(parsedCell)
//                .widenSectionRight(0, 1)
//                .widenSectionRight(1, 1)
//                .build();
//
//        assertParse(builtCell, valid(3, CALL), valid( PLAIN_LEAD), unparsed(2));
//    }
//
//    @Test
//    public void widenSectionToLeftReturnsWiderSection() {
//        ParsedCell parsedCell = buildParsedCellWithGap();
//
//        ParsedCell builtCell = new ParsedCellMutator()
//                .prototypeOf(parsedCell)
//                .widenSectionLeft(3, 2)
//                .build();
//
//        assertParse(builtCell, valid( CALL), valid(3, PLAIN_LEAD), unparsed(2));
//    }
//
//    @Test
//    public void widenSectionCompoundToLeftReturnsWiderSection() {
//        ParsedCell parsedCell = buildParsedCellWithGap();
//
//        ParsedCell builtCell = new ParsedCellMutator()
//                .prototypeOf(parsedCell)
//                .widenSectionLeft(3, 1)
//                .widenSectionLeft(2, 1)
//                .build();
//
//        assertParse(builtCell, valid( CALL), valid(3, PLAIN_LEAD), unparsed(2));
//    }
//
//    @Test(expected = IllegalStateException.class)
//    public void widenNonExistentSectionThrows() {
//        ParsedCell parsedCell = buildParsedCellWithGap();
//
//        new ParsedCellMutator()
//                .prototypeOf(parsedCell)
//                .widenSectionLeft(2, 1)
//                .build();
//    }
//
//    @Test(expected = IndexOutOfBoundsException.class)
//    public void widenSectionGreaterThanCellWidthThrows() {
//        ParsedCell parsedCell = buildParsedCellWithGap();
//
//        new ParsedCellMutator()
//                .prototypeOf(parsedCell)
//                .widenSectionRight(3, 3)
//                .build();
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public void widenSectionLessThan0Throws() {
//        ParsedCell parsedCell = buildParsedCellWithGap();
//
//        new ParsedCellMutator()
//                .prototypeOf(parsedCell)
//                .widenSectionLeft(0, 1)
//                .build();
//    }

//    @Test(expected = IllegalArgumentException.class)
//    public void widenSectionOverlappingRightThrows() {
//        ParsedCell parsedCell = buildParsedCellWithGap();
//
//        new ParsedCellMutator()
//                .prototypeOf(parsedCell)
//                .widenSectionRight(0, 3)
//                .build();
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public void widenSectionOverlappingLeftThrows() {
//        ParsedCell parsedCell = buildParsedCellWithGap();
//
//        new ParsedCellMutator()
//                .prototypeOf(parsedCell)
//                .widenSectionLeft(3, 3)
//                .build();
//    }
//
//    @Test
//    public void widenAndInvalidateCompound() {
//        ParsedCell parsedCell = buildParsedCellWithGap();
//
//        ParsedCell builtCell = new ParsedCellMutator()
//                .prototypeOf(parsedCell)
//                .widenSectionLeft(3, 2)
//                .invalidateGroup(3, "MESSAGE")
//                .build();
//
//        assertParse(builtCell, valid( CALL), invalid(3, PLAIN_LEAD, "MESSAGE"), unparsed(2));
//    }

//TODO other compound actions

//    private ParsedCell buildParsedCellWithGap() {
//
//        HashSet<Section> sections = Sets.newHashSet(
//                GroupingFactory.buildSection(0, 1, CALL),
//                // Gap: 1,2
//                GroupingFactory.buildSection(3, 1, PLAIN_LEAD)
//        );
//
//        Element e = new Element('c');
//
//        Cell mock = mock(Cell.class);
//        when(mock.getElementSize()).thenReturn(6);
//        when(mock.getElement(anyInt())).thenReturn(e);
//
//        return ParsedCellFactory.buildParsedCellFromSections(mock, sections);
//    }

    private ParsedCell buildParsedCell() {

        HashSet<Section> sections = Sets.newHashSet(
                GroupingFactory.buildSection(0, 2, CALL),
                GroupingFactory.buildSection(2, 1, CALL_MULTIPLIER),
                GroupingFactory.buildSection(3, 3, PLAIN_LEAD));

        Element e = new Element('c');

        Cell mock = mock(Cell.class);
        when(mock.getElementSize()).thenReturn(6);
        when(mock.getElement(anyInt())).thenReturn(e);

        return ParsedCellFactory.buildParsedCellFromSections(mock, sections);
    }

}