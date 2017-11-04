package org.ringingmaster.engine.parsernew.multiplecallpositionsinonecell;

import com.google.common.collect.HashBasedTable;
import com.google.errorprone.annotations.Immutable;
import org.ringingmaster.engine.arraytable.BackingTableLocationAndValue;
import org.ringingmaster.engine.arraytable.ImmutableArrayTable;
import org.ringingmaster.engine.parser.ParseType;
import org.ringingmaster.engine.parsernew.Parse;
import org.ringingmaster.engine.parsernew.ParseBuilder;
import org.ringingmaster.engine.parsernew.cell.Group;
import org.ringingmaster.engine.parsernew.cell.ParsedCell;

import java.util.Optional;

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

        for (BackingTableLocationAndValue<ParsedCell> locationAndValue : cells) {

            ParsedCell cell = locationAndValue.getValue();
            boolean seenValidCallingPosition = false;
            for (int elementIndex = 0; elementIndex< cell.getElementSize(); elementIndex++) {

                Optional<Group> group = cell.getGroupAtElementIndex(elementIndex);
                if (group.isPresent()) {
                    if (group.get().getSections().size() != 0) {
                        need builder / factory to mutate the group

                        group.setInvalid("Only one Calling Position allowed in this cell");
                    }

                    if (group.get().getParseType().equals(ParseType.CALLING_POSITION)) {
                        if (!seenValidCallingPosition) {
                            seenValidCallingPosition = true;
                        }
                        else {
                            group.setInvalid("Only one Calling Position allowed in this cell");
                        }
                    }
                    else {
                        group.setInvalid("Only one Calling Position allowed in this cell");
                    }
                }
            }
        }
    }

}
