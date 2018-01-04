package org.ringingmaster.engine.parser.cell;


import com.google.common.collect.Sets;
import org.junit.Test;
import org.ringingmaster.engine.touch.cell.Cell;

import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.ringingmaster.engine.parser.AssertParse.assertParse;
import static org.ringingmaster.engine.parser.AssertParse.invalid;
import static org.ringingmaster.engine.parser.AssertParse.section;
import static org.ringingmaster.engine.parser.AssertParse.unparsed;
import static org.ringingmaster.engine.parser.AssertParse.valid;
import static org.ringingmaster.engine.parser.ParseType.CALL;
import static org.ringingmaster.engine.parser.ParseType.CALL_MULTIPLIER;
import static org.ringingmaster.engine.parser.ParseType.PLAIN_LEAD;
import static org.ringingmaster.engine.parser.ParseType.PLAIN_LEAD_MULTIPLIER;

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
                .invalidateGroup(parsedCell.getGroupAtElementIndex(0).get(), "MESSAGE")
                .build();

        assertParse(builtCell, invalid(2, CALL), valid(CALL_MULTIPLIER), valid(3, PLAIN_LEAD));
        assertEquals("MESSAGE", builtCell.getGroupAtElementIndex(0).get().getMessage().get());
    }

    @Test
    public void settingInvalidInTwoRunsConcatanatesTheMessage() {

        ParsedCell parsedCell = buildParsedCell();

        ParsedCell builtCell1 = new ParsedCellMutator()
                .prototypeOf(parsedCell)
                .invalidateGroup(parsedCell.getGroupAtElementIndex(0).get(), "MESSAGE")
                .build();

        ParsedCell builtCell2 = new ParsedCellMutator()
                .prototypeOf(builtCell1)
                .invalidateGroup(builtCell1.getGroupAtElementIndex(0).get(), "ADDITIONAL")
                .build();

        assertParse(builtCell1, invalid(2, CALL, "MESSAGE"), valid(CALL_MULTIPLIER), valid(3, PLAIN_LEAD));
        assertParse(builtCell2, invalid(2, CALL, "MESSAGE, ADDITIONAL"), valid(CALL_MULTIPLIER), valid(3, PLAIN_LEAD));
    }

    @Test
    public void settingMutipleGroupsInvalidCorrectlyRebuildsGroups() {

        ParsedCell parsedCell = buildParsedCell();

        ParsedCell builtCell = new ParsedCellMutator()
                .prototypeOf(parsedCell)
                .invalidateGroup(parsedCell.getGroupAtElementIndex(3).get(), "MESSAGE3")
                .invalidateGroup(parsedCell.getGroupAtElementIndex(2).get(), "MESSAGE2")
                .invalidateGroup(parsedCell.getGroupAtElementIndex(0).get(), "MESSAGE1")
                .build();

        assertParse(builtCell,
                invalid(2, CALL, "MESSAGE1"),
                invalid(1, CALL_MULTIPLIER, "MESSAGE2"),
                invalid(3, PLAIN_LEAD, "MESSAGE3"));
    }

    @Test
    public void mergingGroupsCorrectlyCombinesLength() {

        ParsedCell parsedCell = buildParsedCell();

        ParsedCell builtCell = new ParsedCellMutator()
                .prototypeOf(parsedCell)
                .mergeGroups(Sets.newHashSet(parsedCell.getGroupAtElementIndex(0).get(), parsedCell.getGroupAtElementIndex(2).get()))
                .build();

        assertParse(builtCell,
                valid(section(2, CALL), section(CALL_MULTIPLIER)),
                valid(3, PLAIN_LEAD));
    }

    @Test
    public void invalidatingAndMergingGroupsCorrectlyCombinesLengthAndInvalidState() {

        ParsedCell parsedCell = buildParsedCell();

        ParsedCell builtCell = new ParsedCellMutator()
                .prototypeOf(parsedCell)
                .invalidateGroup(parsedCell.getGroupAtElementIndex(0).get(), "MESSAGE")
                .mergeGroups(Sets.newHashSet(parsedCell.getGroupAtElementIndex(0).get(), parsedCell.getGroupAtElementIndex(2).get()))
                .build();

        assertEquals(builtCell.getGroupAtElementIndex(0).get(), builtCell.getGroupAtElementIndex(2).get());
        assertEquals(0, builtCell.getGroupAtElementIndex(0).get().getElementStartIndex());
        assertEquals(3, builtCell.getGroupAtElementIndex(0).get().getElementLength());
        assertEquals(2, builtCell.getGroupAtElementIndex(0).get().getSections().size());
        assertEquals("MESSAGE", builtCell.getGroupAtElementIndex(0).get().getMessage().get());
        assertFalse(builtCell.getGroupAtElementIndex(0).get().isValid());
    }

    @Test
    public void mergingCorrectlyCombinesMessage() {

        ParsedCell parsedCell = buildParsedCell();

        ParsedCell builtCell = new ParsedCellMutator()
                .prototypeOf(parsedCell)
                .invalidateGroup(parsedCell.getGroupAtElementIndex(0).get(), "MESSAGE")
                .invalidateGroup(parsedCell.getGroupAtElementIndex(3).get(), "COMBINED")
                .mergeGroups(Sets.newHashSet(parsedCell.getGroupAtElementIndex(0).get(),
                        parsedCell.getGroupAtElementIndex(2).get(),
                        parsedCell.getGroupAtElementIndex(3).get()))
                .build();

        assertParse(builtCell, invalid( "MESSAGE,COMBINED", section(2, CALL), section(1, CALL_MULTIPLIER), section(3, PLAIN_LEAD)));
    }

    @Test
    public void mergingMultipleBlocksCorrectlyCombinesLength() {

        ParsedCell parsedCell = buildParsedCell();

        ParsedCell builtCell = new ParsedCellMutator()
                .prototypeOf(parsedCell)
                .mergeGroups(Sets.newHashSet(parsedCell.getGroupAtElementIndex(0).get(), parsedCell.getGroupAtElementIndex(2).get(), parsedCell.getGroupAtElementIndex(3).get()))
                .build();

        assertEquals(builtCell.getGroupAtElementIndex(0).get(), builtCell.getGroupAtElementIndex(5).get());
        assertEquals(0, builtCell.getGroupAtElementIndex(0).get().getElementStartIndex());
        assertEquals(6, builtCell.getGroupAtElementIndex(0).get().getElementLength());
        assertEquals(3, builtCell.getGroupAtElementIndex(0).get().getSections().size());
        assertFalse(builtCell.getGroupAtElementIndex(0).get().getMessage().isPresent());
        assertTrue(builtCell.getGroupAtElementIndex(0).get().isValid());
    }

    @Test(expected = IllegalArgumentException.class)
    public void overlappingMergeSetsThrows() {

        ParsedCell parsedCell = buildParsedCell();

        new ParsedCellMutator()
                .prototypeOf(parsedCell)
                .mergeGroups(Sets.newHashSet(parsedCell.getGroupAtElementIndex(0).get(), parsedCell.getGroupAtElementIndex(2).get()))
                .mergeGroups(Sets.newHashSet(parsedCell.getGroupAtElementIndex(2).get(), parsedCell.getGroupAtElementIndex(3).get()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void mergingNonContiguousGroupsThrows() {

        ParsedCell parsedCell = buildParsedCell();

        new ParsedCellMutator()
                .prototypeOf(parsedCell)
                .mergeGroups(Sets.newHashSet(parsedCell.getGroupAtElementIndex(0).get(), parsedCell.getGroupAtElementIndex(3).get()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void mergingLessThanTwoGroupsThrows() {

        ParsedCell parsedCell = buildParsedCell();

        new ParsedCellMutator()
                .prototypeOf(parsedCell)
                .mergeGroups(Sets.newHashSet(parsedCell.getGroupAtElementIndex(0).get()));
    }

    // TODO Merge out of order groups

    @Test
    public void addingNewSectionToNewGroup() {
        ParsedCell parsedCell = buildParsedCellWithGap();

        ParsedCell builtCell = new ParsedCellMutator()
                .prototypeOf(parsedCell)
                .addSectionAndGenerateNewGroup(ParsedCellFactory.buildSection(1, 2, PLAIN_LEAD_MULTIPLIER))
                .build();

        assertParse(builtCell, valid(CALL), valid(2, PLAIN_LEAD_MULTIPLIER), valid(PLAIN_LEAD), unparsed(2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void addingNewSectionOverlappingExistingSectionThrows() {
        ParsedCell parsedCell = buildParsedCellWithGap();

        new ParsedCellMutator()
                .prototypeOf(parsedCell)
                .addSectionAndGenerateNewGroup(ParsedCellFactory.buildSection(3, 1, PLAIN_LEAD_MULTIPLIER))
                .build();
        }

    @Test
    public void addingNewSectionToStartOfExistingGroup() {
        ParsedCell parsedCell = buildParsedCellWithGap();

        ParsedCell builtCell = new ParsedCellMutator()
                .prototypeOf(parsedCell)
                .addSectionIntoGroup(ParsedCellFactory.buildSection(1, 2, PLAIN_LEAD_MULTIPLIER),
                        parsedCell.getGroupAtElementIndex(3).get())
                .build();

        assertParse(builtCell, valid(CALL), valid(section(2, PLAIN_LEAD_MULTIPLIER), section(PLAIN_LEAD)), unparsed(2));

    }

    @Test
    public void addingTwoNewSectionsToStartOfExistingGroup() {
        ParsedCell parsedCell = buildParsedCellWithGap();

        ParsedCell builtCell = new ParsedCellMutator()
                .prototypeOf(parsedCell)
                .addSectionIntoGroup(ParsedCellFactory.buildSection(1, 1, PLAIN_LEAD_MULTIPLIER),
                        parsedCell.getGroupAtElementIndex(3).get())
                .addSectionIntoGroup(ParsedCellFactory.buildSection(2, 1, CALL_MULTIPLIER),
                        parsedCell.getGroupAtElementIndex(3).get())
                .build();

        assertParse(builtCell, valid(CALL), valid(section(PLAIN_LEAD_MULTIPLIER), section(CALL_MULTIPLIER), section(PLAIN_LEAD)), unparsed(2));
    }

    @Test
    public void addingNewSectionToEndOfExistingGroup() {
        ParsedCell parsedCell = buildParsedCellWithGap();

        ParsedCell builtCell = new ParsedCellMutator()
                .prototypeOf(parsedCell)
                .addSectionIntoGroup(ParsedCellFactory.buildSection(4, 2, PLAIN_LEAD_MULTIPLIER),
                        parsedCell.getGroupAtElementIndex(3).get())
                .build();

        assertParse(builtCell, valid(CALL), unparsed(2), valid(section(PLAIN_LEAD),section(2, PLAIN_LEAD_MULTIPLIER)));

    }

    @Test
    public void addingTwoNewSectionsToEndOfExistingGroup() {
        ParsedCell parsedCell = buildParsedCellWithGap();

        ParsedCell builtCell = new ParsedCellMutator()
                .prototypeOf(parsedCell)
                .addSectionIntoGroup(ParsedCellFactory.buildSection(4, 1, PLAIN_LEAD_MULTIPLIER),
                        parsedCell.getGroupAtElementIndex(3).get())
                .addSectionIntoGroup(ParsedCellFactory.buildSection(5, 1, CALL_MULTIPLIER),
                        parsedCell.getGroupAtElementIndex(3).get())
                .build();

        assertParse(builtCell, valid(CALL), unparsed(2), valid(section(PLAIN_LEAD), section(PLAIN_LEAD_MULTIPLIER), section(CALL_MULTIPLIER)));
    }

    @Test
    public void addingANewSectionsToEachEndOfExistingGroup() {
        ParsedCell parsedCell = buildParsedCellWithGap();

        ParsedCell builtCell = new ParsedCellMutator()
                .prototypeOf(parsedCell)
                .addSectionIntoGroup(ParsedCellFactory.buildSection(1, 2, PLAIN_LEAD_MULTIPLIER),
                        parsedCell.getGroupAtElementIndex(3).get())
                .addSectionIntoGroup(ParsedCellFactory.buildSection(4, 2, CALL_MULTIPLIER),
                        parsedCell.getGroupAtElementIndex(3).get())
                .build();

        assertParse(builtCell, valid(CALL), valid(section(2, PLAIN_LEAD_MULTIPLIER),  section(PLAIN_LEAD), section(2, CALL_MULTIPLIER)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void addingOverlappingNewSectionThrows() {
        ParsedCell parsedCell = buildParsedCellWithGap();

        new ParsedCellMutator()
                .prototypeOf(parsedCell)
                .addSectionIntoGroup(ParsedCellFactory.buildSection(2, 3, PLAIN_LEAD_MULTIPLIER),
                        parsedCell.getGroupAtElementIndex(3).get())
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void addingNonContiguousSectionThrows() {
        ParsedCell parsedCell = buildParsedCellWithGap();

        new ParsedCellMutator()
                .prototypeOf(parsedCell)
                .addSectionIntoGroup(ParsedCellFactory.buildSection(1, 1, PLAIN_LEAD_MULTIPLIER),
                        parsedCell.getGroupAtElementIndex(3).get())
                .build();
    }

    private ParsedCell buildParsedCellWithGap() {

        HashSet<Section> sections = Sets.newHashSet(
                ParsedCellFactory.buildSection(0, 1, CALL),
                // Gap: 1,2
                ParsedCellFactory.buildSection(3, 1, PLAIN_LEAD)
        );

        Cell mock = mock(Cell.class);
        when(mock.getElementSize()).thenReturn(6);

        return ParsedCellFactory.buildParsedCell(mock, sections);
    }

    private ParsedCell buildParsedCell() {

        HashSet<Section> sections = Sets.newHashSet(
                ParsedCellFactory.buildSection(0, 2, CALL),
                ParsedCellFactory.buildSection(2, 1, CALL_MULTIPLIER),
                ParsedCellFactory.buildSection(3, 3, PLAIN_LEAD));

        Cell mock = mock(Cell.class);
        when(mock.getElementSize()).thenReturn(6);

        return ParsedCellFactory.buildParsedCell(mock, sections);
    }

}