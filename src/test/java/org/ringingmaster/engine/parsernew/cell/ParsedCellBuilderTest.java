package org.ringingmaster.engine.parsernew.cell;


import com.google.common.collect.Sets;
import org.junit.Test;
import org.ringingmaster.engine.parsernew.ParseType;
import org.ringingmaster.engine.touch.newcontainer.cell.Cell;

import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class ParsedCellBuilderTest {

    @Test
    public void buildingFromPrototypeWithNoChangeEmitsIdenticalObject() {
        ParsedCell mockedCell = mock(ParsedCell.class);
        ParsedCellBuilder builder = new ParsedCellBuilder()
                .prototypeOf(mockedCell);

        ParsedCell builtCell = builder.build();
        assertEquals(mockedCell, builtCell);
    }

    @Test
    public void settingGroupInvalidEmmittsInvalidGroupWithCorrectMessage() {

        ParsedCell parsedCell = buildParsedCell();

        ParsedCell builtCell = new ParsedCellBuilder()
                .prototypeOf(parsedCell)
                .setInvalid(parsedCell.getGroupAtElementIndex(0).get(), "MESSAGE")
                .build();

        assertFalse(builtCell.getGroupAtElementIndex(0).get().isValid());
        assertEquals("MESSAGE", builtCell.getGroupAtElementIndex(0).get().getMessage().get());

    }

    @Test
    public void settingMutipleGroupsInvalidCorrectlyRebuildsGroups() {

        ParsedCell parsedCell = buildParsedCell();

        ParsedCell builtCell = new ParsedCellBuilder()
                .prototypeOf(parsedCell)
                .setInvalid(parsedCell.getGroupAtElementIndex(3).get(), "MESSAGE3")
                .setInvalid(parsedCell.getGroupAtElementIndex(2).get(), "MESSAGE2")
                .setInvalid(parsedCell.getGroupAtElementIndex(0).get(), "MESSAGE1")
                .build();

        assertFalse(builtCell.getGroupAtElementIndex(0).get().isValid());
        assertEquals("MESSAGE1", builtCell.getGroupAtElementIndex(0).get().getMessage().get());

        assertFalse(builtCell.getGroupAtElementIndex(2).get().isValid());
        assertEquals("MESSAGE2", builtCell.getGroupAtElementIndex(2).get().getMessage().get());

        assertFalse(builtCell.getGroupAtElementIndex(3).get().isValid());
        assertEquals("MESSAGE3", builtCell.getGroupAtElementIndex(3).get().getMessage().get());
    }

    @Test
    public void mergingGroupsCorrectlyCombinesLength() {

        ParsedCell parsedCell = buildParsedCell();

        ParsedCell builtCell = new ParsedCellBuilder()
                .prototypeOf(parsedCell)
                .merge(parsedCell.getGroupAtElementIndex(0).get(), parsedCell.getGroupAtElementIndex(2).get())
                .build();

        assertEquals(builtCell.getGroupAtElementIndex(0).get(), builtCell.getGroupAtElementIndex(2).get());
        assertEquals(0, builtCell.getGroupAtElementIndex(0).get().getElementStartIndex());
        assertEquals(3, builtCell.getGroupAtElementIndex(0).get().getElementLength());
        assertEquals(2, builtCell.getGroupAtElementIndex(0).get().getSections().size());
        assertFalse(builtCell.getGroupAtElementIndex(0).get().getMessage().isPresent());
        assertTrue(builtCell.getGroupAtElementIndex(0).get().isValid());

    }

    @Test
    public void invalidatingAndMergingGroupsCorrectlyCombinesLengthAndInvalidState() {

        ParsedCell parsedCell = buildParsedCell();

        ParsedCell builtCell = new ParsedCellBuilder()
                .prototypeOf(parsedCell)
                .setInvalid(parsedCell.getGroupAtElementIndex(0).get(), "MESSAGE")
                .merge(parsedCell.getGroupAtElementIndex(0).get(), parsedCell.getGroupAtElementIndex(2).get())
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

        ParsedCell builtCell = new ParsedCellBuilder()
                .prototypeOf(parsedCell)
                .setInvalid(parsedCell.getGroupAtElementIndex(0).get(), "MESSAGE")
                .setInvalid(parsedCell.getGroupAtElementIndex(3).get(), "COMBINED")
                .merge(parsedCell.getGroupAtElementIndex(0).get(),
                        parsedCell.getGroupAtElementIndex(2).get(),
                        parsedCell.getGroupAtElementIndex(3).get())
                .build();

        assertEquals("MESSAGE,COMBINED", builtCell.getGroupAtElementIndex(0).get().getMessage().get());
    }

    @Test(expected = IllegalArgumentException.class)
    public void mergingSameGroupTwiceThrows() {
        ParsedCell parsedCell = buildParsedCell();

        new ParsedCellBuilder()
                .prototypeOf(parsedCell)
                .merge(parsedCell.getGroupAtElementIndex(0).get(),
                        parsedCell.getGroupAtElementIndex(2).get(),
                        parsedCell.getGroupAtElementIndex(2).get())
                .build();
    }

    @Test
    public void mergingMultipleBlockssCorrectlyCombinesLength() {

        ParsedCell parsedCell = buildParsedCell();

        ParsedCell builtCell = new ParsedCellBuilder()
                .prototypeOf(parsedCell)
                .merge(parsedCell.getGroupAtElementIndex(0).get(), parsedCell.getGroupAtElementIndex(2).get(), parsedCell.getGroupAtElementIndex(3).get())
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

        new ParsedCellBuilder()
                .prototypeOf(parsedCell)
                .merge(parsedCell.getGroupAtElementIndex(0).get(), parsedCell.getGroupAtElementIndex(2).get())
                .merge(parsedCell.getGroupAtElementIndex(2).get(), parsedCell.getGroupAtElementIndex(3).get());
    }

    @Test(expected = IllegalArgumentException.class)
    public void mergingNonContiguousGroupsThrows() {

        ParsedCell parsedCell = buildParsedCell();

        new ParsedCellBuilder()
                .prototypeOf(parsedCell)
                .merge(parsedCell.getGroupAtElementIndex(0).get(), parsedCell.getGroupAtElementIndex(3).get());
    }

    @Test(expected = IllegalArgumentException.class)
    public void mergingLessThanTwoGroupsThrows() {

        ParsedCell parsedCell = buildParsedCell();

        new ParsedCellBuilder()
                .prototypeOf(parsedCell)
                .merge(parsedCell.getGroupAtElementIndex(0).get());
    }


    // TODO Merge out of order groups


    private ParsedCell buildParsedCell() {

        HashSet<Section> sections = Sets.newHashSet(
                ParsedCellFactory.buildSection(0, 2, ParseType.CALL),
                ParsedCellFactory.buildSection(2, 1, ParseType.CALL_MULTIPLIER),
                ParsedCellFactory.buildSection(3, 3, ParseType.PLAIN_LEAD));

        Cell mock = mock(Cell.class);
        when(mock.getElementSize()).thenReturn(6);

        return ParsedCellFactory.buildParsedCell(mock, sections);
    }

}