package org.ringingmaster.engine.parsernew.grouplogic;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import org.ringingmaster.engine.arraytable.BackingTableLocationAndValue;
import org.ringingmaster.engine.arraytable.ImmutableArrayTable;
import org.ringingmaster.engine.parsernew.Parse;
import org.ringingmaster.engine.parsernew.ParseBuilder;
import org.ringingmaster.engine.parsernew.cell.Group;
import org.ringingmaster.engine.parsernew.cell.ParsedCell;
import org.ringingmaster.engine.parsernew.cell.ParsedCellBuilder;
import org.ringingmaster.engine.parsernew.cell.Section;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class GroupLogic {

    public Parse parse(Parse parse) {

        HashBasedTable<Integer, Integer, ParsedCell> resultCells =
                HashBasedTable.create(parse.allTouchCells().getBackingTable());

        parseCells(parse.mainBodyCells(), resultCells);
        parseCells(parse.splicedCells(), resultCells);

        // We parse definitions individually. This is so that any grouping in a definition
        // must be complete sets within the definition. i.e a matched open and close brace.
//        TODO for (ParsedDefinitionCell definition : parse.getDefinitions()) {
//            parseCells(Lists.<ParsedDefinitionCell>newArrayList(definition));
//        }

        return new ParseBuilder()
                .prototypeOf(parse)
                .setTouchTableCells(resultCells)
//                .setDefinitions()
                .build();

    }

    private void parseCells(ImmutableArrayTable<ParsedCell> originalCells, HashBasedTable<Integer, Integer, ParsedCell> resultCells) {

        Map<CoordinateAndSection, String> invalidSections = Maps.newHashMap();

        Deque<CoordinateAndSection> openGroups = new ArrayDeque<>();

        for (BackingTableLocationAndValue<ParsedCell> locationAndCell : originalCells) {
            for (Section section : locationAndCell.getValue().allSections()) {
                switch (section.getParseType()) {
                    case GROUP_OPEN:
                        openGroups.addFirst(new CoordinateAndSection(locationAndCell.getRow(), locationAndCell.getCol(), section));
                        break;
                    case GROUP_CLOSE:
                        if (openGroups.size() > 0) {
                            openGroups.removeFirst();
                        }
                        else {
                            invalidSections.put(new CoordinateAndSection(locationAndCell.getRow(), locationAndCell.getCol(), section), "No matching opening brace");
                        }
                        break;
                }
            }
        }
        while (openGroups.peekFirst() != null ) {
            invalidSections.put(openGroups.removeFirst(), "No matching closing brace");
        }

        for (Map.Entry<CoordinateAndSection, String> invalidSection : invalidSections.entrySet()) {

            Section section = invalidSection.getKey().section;

            ParsedCell originalParsedCell = resultCells.get(invalidSection.getKey().row,invalidSection.getKey().col);
            Group group = originalParsedCell.getGroupForSection(section);
            ParsedCellBuilder parsedCellBuilder = new ParsedCellBuilder()
                    .prototypeOf(originalParsedCell)
                    .setInvalid(group, invalidSection.getValue());

            resultCells.put(invalidSection.getKey().row,
                    invalidSection.getKey().col,
                    parsedCellBuilder.build());
        }

    }

    class CoordinateAndSection {
        private final int row;
        private final int col;
        final Section section;

        CoordinateAndSection(int row, int col, Section section) {
            this.row = checkNotNull(row);
            this.col = checkNotNull(col);
            this.section = checkNotNull(section);
        }

        @Override
        public String toString() {
            return "CoordinateAndSection{" +
                    "location=[" + row + "," + col +
                    "], section=" + section +
                    '}';
        }
    }
}