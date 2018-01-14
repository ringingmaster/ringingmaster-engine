package org.ringingmaster.engine.parser.functions;

import com.google.common.collect.HashBasedTable;
import org.ringingmaster.engine.arraytable.BackingTableLocationAndValue;
import org.ringingmaster.engine.arraytable.ImmutableArrayTable;
import org.ringingmaster.engine.parser.cell.ParsedCell;
import org.ringingmaster.engine.parser.cell.ParsedCellMutator;
import org.ringingmaster.engine.parser.cell.Section;

import java.util.Set;
import java.util.function.Function;

public class DefinitionFunctions {

    public void markInvalid(ImmutableArrayTable<ParsedCell> originalCells, Set<String> invalidDefinitions, HashBasedTable<Integer, Integer, ParsedCell> resultCells, Function<String, String> createErrorMessage) {
        for (BackingTableLocationAndValue<ParsedCell> locationAndCell : originalCells) {
            ParsedCell originalCell = locationAndCell.getValue();
            ParsedCellMutator parsedCellMutator = new ParsedCellMutator().prototypeOf(originalCell);

            for (Section section : originalCell.allSections()) {
                String characters = originalCell.getCharacters(section);
                if (invalidDefinitions.contains(characters)) {
                    String message = createErrorMessage.apply(characters);
                    parsedCellMutator.invalidateGroup(section.getElementStartIndex(), message);
                }
            }

            resultCells.put(locationAndCell.getRow(), locationAndCell.getCol(), parsedCellMutator.build());
        }
    }
}