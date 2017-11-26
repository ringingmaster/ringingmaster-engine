package org.ringingmaster.engine.parsernew.multiplecallpositionsinonecell;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.errorprone.annotations.Immutable;
import org.ringingmaster.engine.arraytable.BackingTableLocationAndValue;
import org.ringingmaster.engine.arraytable.ImmutableArrayTable;
import org.ringingmaster.engine.parser.ParseType;
import org.ringingmaster.engine.parsernew.Parse;
import org.ringingmaster.engine.parsernew.ParseBuilder;
import org.ringingmaster.engine.parsernew.cell.ParsedCell;
import org.ringingmaster.engine.parsernew.cell.ParsedCellBuilder;
import org.ringingmaster.engine.parsernew.cell.Section;

/**
 * TODO comments???
 *
 * @author stevelake
 */
@Immutable
public class MultipleCallPositionsInOneCell {

    public Parse parse(Parse parse) {
        HashBasedTable<Integer, Integer, ParsedCell> parsedCells = HashBasedTable.create();

        parseCallPositionArea(parse, parsedCells);

        return new ParseBuilder()
                .prototypeOf(parse)
                .setParsedCells(parsedCells)
                .build();
    }

    private void parseCallPositionArea(Parse originalParse, HashBasedTable<Integer, Integer, ParsedCell> parsedCells) {
        ImmutableArrayTable<ParsedCell> cells = originalParse.allCells();

        for (BackingTableLocationAndValue<ParsedCell> locationAndCell : cells) {

            ParsedCell originalParsedCell = locationAndCell.getValue();
            ParsedCell parsedCell = transformCell(originalParsedCell);
            parsedCells.put(locationAndCell.getRow(), locationAndCell.getCol(), parsedCell);
        }
    }

    private ParsedCell transformCell(ParsedCell cell) {
        ParsedCellBuilder builder = new ParsedCellBuilder()
            .prototypeOf(cell);

        boolean seenValidCallingPosition = false;
        ImmutableList<Section> sections = cell.allSections();
        for (Section section : sections) {
            if (section.getParseType().equals(ParseType.CALLING_POSITION)){
                if (!seenValidCallingPosition) {
                    seenValidCallingPosition = true;
                }
                else {
                    builder.setInvalid(cell.getGroupForSection(section), "Only one Calling Position allowed in this cell");
                }
            }
            else {
                builder.setInvalid(cell.getGroupForSection(section), "Only Calling Positions are allowed in this cell");
            }
        }

        return builder.build();
    }

}