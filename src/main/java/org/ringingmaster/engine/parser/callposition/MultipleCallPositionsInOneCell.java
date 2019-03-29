package org.ringingmaster.engine.parser.callposition;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.errorprone.annotations.Immutable;
import org.ringingmaster.engine.arraytable.BackingTableLocationAndValue;
import org.ringingmaster.engine.arraytable.ImmutableArrayTable;
import org.ringingmaster.engine.parser.assignparsetype.ParseType;
import org.ringingmaster.engine.parser.cell.ParsedCell;
import org.ringingmaster.engine.parser.cell.ParsedCellMutator;
import org.ringingmaster.engine.parser.cell.Section;
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
public class MultipleCallPositionsInOneCell implements Function<Parse, Parse> {

    private final Logger log = LoggerFactory.getLogger(MultipleCallPositionsInOneCell.class);

    public Parse apply(Parse parse) {

        log.debug("[{}] > multiple call positions in one cell", parse.getUnderlyingTouch().getTitle());

        HashBasedTable<Integer, Integer, ParsedCell> resultCells =
                HashBasedTable.create(parse.allTouchCells().getBackingTable());

        parseCallPositionArea(parse, resultCells);

        Parse build = new ParseBuilder()
                .prototypeOf(parse)
                .setTouchTableCells(resultCells)
                .build();

        log.debug("[{}] < multiple call positions in one cell", parse.getUnderlyingTouch().getTitle());

        return build;
    }

    private void parseCallPositionArea(Parse originalParse, HashBasedTable<Integer, Integer, ParsedCell> resultCells) {
        ImmutableArrayTable<ParsedCell> cells = originalParse.callPositionCells();

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
                    builder.invalidateGroup(section.getElementStartIndex(), "Only one Calling Position allowed in this cell");
                }
            }
            else {
                builder.invalidateGroup(section.getElementStartIndex(), "Only Calling Positions are allowed in this cell");
            }
        }

        return builder.build();
    }

}
