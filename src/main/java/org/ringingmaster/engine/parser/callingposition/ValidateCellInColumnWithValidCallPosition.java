package org.ringingmaster.engine.parser.callingposition;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Streams;
import org.ringingmaster.engine.arraytable.BackingTableLocationAndValue;
import org.ringingmaster.engine.composition.compositiontype.CompositionType;
import org.ringingmaster.engine.parser.cell.ParsedCell;
import org.ringingmaster.engine.parser.cell.grouping.Group;
import org.ringingmaster.engine.parser.cell.mutator.ParsedCellMutator;
import org.ringingmaster.engine.parser.parse.Parse;
import org.ringingmaster.engine.parser.parse.ParseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.ringingmaster.engine.parser.assignparsetype.ParseType.CALLING_POSITION;

/**
 * Validates that any main body cell is in a column with valid call position at the top
 *
 * @author Steve Lake
 */
public class ValidateCellInColumnWithValidCallPosition implements Function<Parse, Parse> {

    private final Logger log = LoggerFactory.getLogger(ValidateCellInColumnWithValidCallPosition.class);

    @Override
    public Parse apply(Parse input) {

        if (input.getComposition().getCompositionType() != CompositionType.COURSE_BASED) {
            return input;
        }

        log.debug("[{}] > validate cell in column with valid call position", input.getComposition().getLoggingTag());

        Set<Integer> validColumns = buildValidColumns(input);


        HashBasedTable<Integer, Integer, ParsedCell> compositionCells =
                HashBasedTable.create(input.allCompositionCells().getBackingTable());
        for (BackingTableLocationAndValue<ParsedCell> locationAndCell : input.mainBodyCells()) {
            if (!validColumns.contains(locationAndCell.getCol())) {
                doInvalidation(compositionCells, locationAndCell);
            }
        }

        Parse result = new ParseBuilder()
                .prototypeOf(input)
                .setCompositionTableCells(compositionCells)
                .build();

        log.debug("[{}] < validate cell in column with valid call position", input.getComposition().getLoggingTag());

        return result;
    }

    private Set<Integer> buildValidColumns(Parse input) {
        return Streams.stream(input.callingPositionCells())
                    .filter(cell ->
                            cell.getValue().allGroups().stream()
                                    .filter(group -> group.isValid() && group.getFirstSectionParseType() == CALLING_POSITION)
                                    .count() == 1)
                    .map(BackingTableLocationAndValue::getCol)
                    .collect(Collectors.toSet());
    }

    private void doInvalidation(HashBasedTable<Integer, Integer, ParsedCell> cells, BackingTableLocationAndValue<ParsedCell> locationAndCell) {

        final ParsedCell cell = locationAndCell.getValue();

        ParsedCellMutator builder = new ParsedCellMutator()
                .prototypeOf(cell);

        for (Group group : cell.allGroups()) {
            builder.invalidateGroup(group.getStartIndex(), "Column does not have a valid calling position");
        }

        cells.put(locationAndCell.getRow(), locationAndCell.getCol(), builder.build());
    }

}
