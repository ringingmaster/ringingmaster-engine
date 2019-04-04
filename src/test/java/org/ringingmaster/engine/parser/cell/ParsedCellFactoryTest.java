package org.ringingmaster.engine.parser.cell;

import com.google.common.collect.Sets;
import org.junit.Test;
import org.ringingmaster.engine.parser.cell.grouping.GroupingFactory;
import org.ringingmaster.engine.parser.cell.grouping.Section;
import org.ringingmaster.engine.touch.cell.Cell;

import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.CALL;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.CALLING_POSITION;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.CALL_MULTIPLIER;

public class ParsedCellFactoryTest {

    @Test
    public void buildSingleSectionParsedCellHasCorrectDimensions() {
        HashSet<Section> sections = Sets.newHashSet(
                GroupingFactory.buildSection(0, 1, CALL));

        Cell mock = mock(Cell.class);
        when(mock.getElementSize()).thenReturn(1);

        final ParsedCell parsedCell = ParsedCellFactory.buildParsedCell(mock, sections);

        assertEquals(1, parsedCell.getElementSize());
        assertEquals(CALL, parsedCell.getGroupAtElementIndex(0).get().getSections().get(0).getParseType());
    }

    @Test
    public void buildTwoSectionNonContiguousSectionsHasCorrectDimensions() {
        HashSet<Section> sections = Sets.newHashSet(
                GroupingFactory.buildSection(0, 1, CALL),
                //gap 1,1
                GroupingFactory.buildSection(2, 1, CALLING_POSITION)
        );

        Cell mock = mock(Cell.class);
        when(mock.getElementSize()).thenReturn(3);

        final ParsedCell parsedCell = ParsedCellFactory.buildParsedCell(mock, sections);

        assertEquals(CALL, parsedCell.getSectionAtElementIndex(0).get().getParseType());
        assertFalse(parsedCell.getSectionAtElementIndex(1).isPresent());
        assertEquals(CALLING_POSITION, parsedCell.getSectionAtElementIndex(2).get().getParseType());
    }

    @Test(expected = IllegalArgumentException.class)
    public void overlappingSectionsThrows() {
        HashSet<Section> sections = Sets.newHashSet(
                GroupingFactory.buildSection(0, 2, CALL),
                GroupingFactory.buildSection(1, 1, CALL_MULTIPLIER));

        Cell mock = mock(Cell.class);
        when(mock.getElementSize()).thenReturn(3);

        ParsedCellFactory.buildParsedCell(mock, sections);
    }

}