package org.ringingmaster.engine.parser.cell;

import com.google.common.collect.Sets;
import org.junit.Test;
import org.ringingmaster.engine.parser.ParseType;
import org.ringingmaster.engine.touch.container.cell.Cell;

import java.util.HashSet;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ParsedCellTest {

    @Test(expected = IndexOutOfBoundsException.class)
    public void gettingGroupAboveUpperBoundThrows() {

        ParsedCell parsedCell = buildParsedCell();

        parsedCell.getGroupAtElementIndex(parsedCell.getElementSize());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void gettingGroupAboveBelowBoundThrows() {

        ParsedCell parsedCell = buildParsedCell();

        parsedCell.getGroupAtElementIndex(-1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void gettingSectionAboveUpperBoundThrows() {

        ParsedCell parsedCell = buildParsedCell();

        parsedCell.getSectionAtElementIndex(parsedCell.getElementSize());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void gettingSectionAboveBelowBoundThrows() {

        ParsedCell parsedCell = buildParsedCell();

        parsedCell.getSectionAtElementIndex(-1);
    }

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