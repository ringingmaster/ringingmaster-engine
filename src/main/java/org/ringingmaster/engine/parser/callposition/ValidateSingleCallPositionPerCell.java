package org.ringingmaster.engine.parser.callposition;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.errorprone.annotations.Immutable;
import org.ringingmaster.engine.arraytable.BackingTableLocationAndValue;
import org.ringingmaster.engine.arraytable.ImmutableArrayTable;
import org.ringingmaster.engine.parser.assignparsetype.ParseType;
import org.ringingmaster.engine.parser.cell.ParsedCell;
import org.ringingmaster.engine.parser.cell.mutator.ParsedCellMutator;
import org.ringingmaster.engine.parser.cell.grouping.Section;
import org.ringingmaster.engine.parser.parse.Parse;
import org.ringingmaster.engine.parser.parse.ParseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

/**
 * Enforces that only one call position is allowed in a cell.
 * Works with CallPosition cells only.
 *
 * @author stevelake
 */
@Immutable
public class ValidateSingleCallPositionPerCell implements Function<Parse, Parse> {

    private final Logger log = LoggerFactory.getLogger(ValidateSingleCallPositionPerCell.class);

    public Parse apply(Parse input) {

        log.debug("[{}] > validate single call position per cell", input.getTouch().getTitle());

        HashBasedTable<Integer, Integer, ParsedCell> resultCells =
                HashBasedTable.create(input.allTouchCells().getBackingTable());

        parseCallPositionArea(input, resultCells);

        Parse build = new ParseBuilder()
                .prototypeOf(input)
                .setTouchTableCells(resultCells)
                .build();

        log.debug("[{}] < validate single call position per cell", input.getTouch().getTitle());

        return build;
    }

    private void parseCallPositionArea(Parse input, HashBasedTable<Integer, Integer, ParsedCell> resultCells) {
        ImmutableArrayTable<ParsedCell> cells = input.callPositionCells();

        for (BackingTableLocationAndValue<ParsedCell> locationAndCell : cells) {

            ParsedCell originalParsedCell = locationAndCell.getValue();
            ParsedCell parsedCell = transformCell(originalParsedCell);
            resultCells.put(locationAndCell.getRow(), locationAndCell.getCol(), parsedCell);
        }
    }

    private ParsedCell transformCell(ParsedCell cell) {
        ParsedCellMutator builder = new ParsedCellMutator()
            .prototypeOf(cell);

        boolean seenValidCallingPosition = false;
        ImmutableList<Section> sections = cell.allSections();
        for (Section section : sections) {
            if (section.getParseType().equals(ParseType.CALLING_POSITION)){
                if (!seenValidCallingPosition) {
                    seenValidCallingPosition = true;
                }
                else {
                    builder.invalidateGroup(section.getStartIndex(), "Only one Calling Position allowed in this cell");
                }
            }
            else {
                builder.invalidateGroup(section.getStartIndex(), "Only Calling Positions are allowed in this cell");
            }
        }

        return builder.build();
    }

}
