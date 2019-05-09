package org.ringingmaster.engine.parser.brace;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import org.ringingmaster.engine.arraytable.BackingTableLocationAndValue;
import org.ringingmaster.engine.arraytable.ImmutableArrayTable;
import org.ringingmaster.engine.parser.assignparsetype.ParseType;
import org.ringingmaster.engine.parser.cell.ParsedCell;
import org.ringingmaster.engine.parser.cell.grouping.Group;
import org.ringingmaster.engine.parser.cell.grouping.Section;
import org.ringingmaster.engine.parser.cell.mutator.ParsedCellMutator;
import org.ringingmaster.engine.parser.parse.Parse;
import org.ringingmaster.engine.parser.parse.ParseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
public class ValidateMultiplierGroupAndVarianceDontOverlap implements Function<Parse, Parse> {

    private final Logger log = LoggerFactory.getLogger(ValidateMultiplierGroupAndVarianceDontOverlap.class);

    public Parse apply(Parse input) {

        log.debug("[{}] > validate multiplier group and variance dine overlap", input.getComposition().getTitle());

        HashBasedTable<Integer, Integer, ParsedCell> resultCells =
                HashBasedTable.create(input.allCompositionCells().getBackingTable());

        parseCells(input.mainBodyCells(), resultCells, input.getComposition().getTitle());
        parseCells(input.splicedCells(), resultCells, input.getComposition().getTitle());

        // We parse definitions individually. This is so that any open/close brace in a definition
        // must be complete sets within the definition. i.e a matched open and close brace.
        HashBasedTable<Integer, Integer, ParsedCell> definitionTableResult =
                HashBasedTable.create(input.definitionDefinitionCells().getBackingTable());
        final ImmutableArrayTable<ParsedCell> definitionDefinitionCells = input.definitionDefinitionCells();
        for(int rowIndex = 0; rowIndex < definitionDefinitionCells.getRowSize();rowIndex++) {
            final ImmutableArrayTable<ParsedCell> cell = definitionDefinitionCells.subTable(rowIndex, rowIndex + 1, 0, 1);
            parseCells(cell, definitionTableResult, input.getComposition().getTitle());
        }

        Parse result = new ParseBuilder()
                .prototypeOf(input)
                .setCompositionTableCells(resultCells)
                .setDefinitionTableCells(definitionTableResult)
                .build();

        log.debug("[{}] < validate multiplier group and variance dine overlap", input.getComposition().getTitle());

        return result;

    }

    private void parseCells(ImmutableArrayTable<ParsedCell> originalCells, HashBasedTable<Integer, Integer, ParsedCell> resultCells, String logPreamble) {

        Map<CoordinateAndSection, String> invalidSections = Maps.newHashMap();

        Deque<CoordinateAndSection> openBraces = new ArrayDeque<>();

        for (BackingTableLocationAndValue<ParsedCell> locationAndCell : originalCells) {
            for (Group group : locationAndCell.getValue().allGroups()) {

                if (group.isValid()){

                    for (Section section : group.getSections()) {
                        if (section.getParseType().equals(ParseType.MULTIPLIER_GROUP_OPEN) ||
                                section.getParseType().equals(ParseType.VARIANCE_OPEN)) {
                            openBraces.addFirst(new CoordinateAndSection(locationAndCell.getRow(), locationAndCell.getCol(), section));
                        }
                        else if (section.getParseType().equals(ParseType.MULTIPLIER_GROUP_CLOSE) ||
                                section.getParseType().equals(ParseType.VARIANCE_CLOSE)) {

                            // We may only have an opening brace.
                            if (!openBraces.isEmpty()) {

                                final CoordinateAndSection head = openBraces.remove();

                                if ((head.section.getParseType().equals(ParseType.MULTIPLIER_GROUP_OPEN) && section.getParseType().equals(ParseType.VARIANCE_CLOSE)) ||
                                        (head.section.getParseType().equals(ParseType.VARIANCE_OPEN) && section.getParseType().equals(ParseType.MULTIPLIER_GROUP_CLOSE))) {
                                    invalidSections.put(head, "Variances and Groups can't overlap");
                                    invalidSections.put(new CoordinateAndSection(locationAndCell.getRow(), locationAndCell.getCol(), section), "Variances and Groups can't overlap");
                                }
                            }
                        }
                    }
                }
            }
        }
        while (openBraces.peekFirst() != null ) {
            invalidSections.put(openBraces.removeFirst(), "Variances and groups can't overlap");
        }
        if (!invalidSections.isEmpty()) {
            log.debug("[{}]  removing invalid sections [{}]", logPreamble, invalidSections);
        }


        for (Map.Entry<CoordinateAndSection, String> invalidSection : invalidSections.entrySet()) {

            Section section = invalidSection.getKey().section;

            ParsedCell originalParsedCell = resultCells.get(invalidSection.getKey().row,invalidSection.getKey().col);
            ParsedCellMutator parsedCellMutator = new ParsedCellMutator()
                    .prototypeOf(originalParsedCell)
                    .invalidateGroup(section.getStartIndex(), invalidSection.getValue());

            resultCells.put(invalidSection.getKey().row,
                    invalidSection.getKey().col,
                    parsedCellMutator.build());
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
