package org.ringingmaster.engine.parser.cell;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import org.junit.Test;
import org.ringingmaster.engine.composition.cell.Cell;
import org.ringingmaster.engine.parser.assignparsetype.ParseType;
import org.ringingmaster.engine.parser.cell.grouping.Group;
import org.ringingmaster.engine.parser.cell.grouping.GroupingFactory;
import org.ringingmaster.engine.parser.cell.grouping.Section;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ParsedCellTest {

    private final Logger log = LoggerFactory.getLogger(ParsedCellTest.class);

    @Test(expected = IndexOutOfBoundsException.class)
    public void gettingGroupAboveUpperBoundThrows() {

        ParsedCell parsedCell = buildParsedCell();

        parsedCell.getGroupAtElementIndex(parsedCell.size());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void gettingGroupAboveBelowBoundThrows() {

        ParsedCell parsedCell = buildParsedCell();

        parsedCell.getGroupAtElementIndex(-1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void gettingSectionAboveUpperBoundThrows() {

        ParsedCell parsedCell = buildParsedCell();

        parsedCell.getSectionAtElementIndex(parsedCell.size());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void gettingSectionAboveBelowBoundThrows() {

        ParsedCell parsedCell = buildParsedCell();

        parsedCell.getSectionAtElementIndex(-1);
    }

    @Test
    public void callingPrettyPrint() {

        HashSet<Group> groups = Sets.newHashSet(
                GroupingFactory.buildGroup(0, 4, true, ImmutableList.of("MESSAGE"),
                        Sets.newHashSet(
                                GroupingFactory.buildSection(0, 2, ParseType.SPLICE),
                                GroupingFactory.buildSection(2, 1, ParseType.CALL),
                                GroupingFactory.buildSection(3, 1, ParseType.CALL_MULTIPLIER))),
                GroupingFactory.buildGroup(4, 3, false, ImmutableList.of("OTHER"),
                        Sets.newHashSet(
                                GroupingFactory.buildSection(4, 3, ParseType.PLAIN_LEAD))),
                GroupingFactory.buildGroup(7, 3, true, ImmutableList.of("MESSAGE"),
                        Sets.newHashSet(
                                GroupingFactory.buildSection(7, 1, ParseType.VARIANCE_OPEN),
                                GroupingFactory.buildSection(8, 2, ParseType.VARIANCE_DETAIL)))
        );


        Cell mockCell = mock(Cell.class);
        when(mockCell.size()).thenReturn(10);
        when(mockCell.get(anyInt())).thenReturn('c');

        ParsedCell parsedCell = ParsedCellFactory.buildParsedCellFromGroups(mockCell, groups);

        log.info(parsedCell.prettyPrint());
    }

    private ParsedCell buildParsedCell() {

        HashSet<Section> sections = Sets.newHashSet(
                GroupingFactory.buildSection(0, 2, ParseType.CALL),
                GroupingFactory.buildSection(2, 1, ParseType.CALL_MULTIPLIER),
                GroupingFactory.buildSection(3, 3, ParseType.PLAIN_LEAD));

        Cell mockCell = mock(Cell.class);
        when(mockCell.size()).thenReturn(6);

        return ParsedCellFactory.buildParsedCellFromSections(mockCell, sections);
    }
}