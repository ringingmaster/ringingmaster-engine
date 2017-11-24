package org.ringingmaster.engine.parsernew.cell;

import com.google.common.collect.Sets;
import org.junit.Test;
import org.ringingmaster.engine.parser.ParseType;
import org.ringingmaster.engine.touch.newcontainer.cell.Cell;

import java.util.HashSet;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ParsedCellFactoryTest {

    @Test(expected = IllegalArgumentException.class)
    public void overlappingSectionsThrows() {
        HashSet<Section> sections = Sets.newHashSet(
                ParsedCellFactory.buildSection(0, 2, ParseType.CALL),
                ParsedCellFactory.buildSection(1, 1, ParseType.CALL_MULTIPLIER));

        Cell mock = mock(Cell.class);
        when(mock.getElementSize()).thenReturn(3);

        ParsedCellFactory.buildParsedCell(mock, sections);
    }

}