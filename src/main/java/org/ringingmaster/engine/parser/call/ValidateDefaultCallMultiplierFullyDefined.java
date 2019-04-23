package org.ringingmaster.engine.parser.call;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import org.ringingmaster.engine.arraytable.BackingTableLocationAndValue;
import org.ringingmaster.engine.arraytable.ImmutableArrayTable;
import org.ringingmaster.engine.composition.Composition;
import org.ringingmaster.engine.notation.Notation;
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
 * Checks to see if a default call multiplier is used without being defined (or fully in the case of spliced)
 * NOTE: a number on its own acts as a default call
 *
 * @author Steve Lake
 */
public class ValidateDefaultCallMultiplierFullyDefined implements Function<Parse, Parse> {

    private final Logger log = LoggerFactory.getLogger(ValidateDefaultCallMultiplierFullyDefined.class);

    @Override
    public Parse apply(Parse input) {

        log.debug("[{}] > validate default call is fully defined", input.getComposition().getTitle());

        Parse result = input;
        final boolean hasFullyDefinedDefaultCall = hasFullyDefinedDefaultCall(input.getComposition());
        if (!hasFullyDefinedDefaultCall) {
            HashBasedTable<Integer, Integer, ParsedCell> resultCells =
                    HashBasedTable.create(input.allCompositionCells().getBackingTable());

            parseMainBodyArea(input, resultCells,input.getComposition().isSpliced());

            result = new ParseBuilder()
                    .prototypeOf(input)
                    .setCompositionTableCells(resultCells)
                    .build();
        }

        log.debug("[{}] < validate default call is fully defined", input.getComposition().getTitle());

        return result;
    }

    private void parseMainBodyArea(Parse input, HashBasedTable<Integer, Integer, ParsedCell> resultCells, boolean isSpliced) {
        ImmutableArrayTable<ParsedCell> cells = input.mainBodyCells();

        for (BackingTableLocationAndValue<ParsedCell> locationAndCell : cells) {

            ParsedCell originalParsedCell = locationAndCell.getValue();
            ParsedCell parsedCell = transformCell(originalParsedCell, isSpliced);
            resultCells.put(locationAndCell.getRow(), locationAndCell.getCol(), parsedCell);
        }
    }

    private ParsedCell transformCell(ParsedCell cell, boolean isSpliced) {
        ParsedCellMutator builder = new ParsedCellMutator()
                .prototypeOf(cell);

        ImmutableList<Section> sections = cell.allSections();
        for (Section section : sections) {
            if (section.getParseType().equals(ParseType.DEFAULT_CALL_MULTIPLIER)){
                if (isSpliced) {
                    builder.invalidateGroup(section.getStartIndex(), "Default call not defined in all methods");
                } else {
                    builder.invalidateGroup(section.getStartIndex(), "No default call defined");
                }
            }
        }

        return builder.build();
    }

    //TODO we should be able to detect the actual notations being used for spliced, and only check those.
    private boolean hasFullyDefinedDefaultCall(Composition composition) {
        for (Notation notation : composition.getAvailableNotations()) {
            if (notation.getDefaultCall() == null) {
                return false;
            }
        }

        if (composition.getAvailableNotations().size() == 0) {
            return false;
        }
        return true;
    }
}
