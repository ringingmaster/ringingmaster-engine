package org.ringingmaster.engine.parser.brace;

import com.google.common.collect.HashBasedTable;
import org.ringingmaster.engine.arraytable.BackingTableLocationAndValue;
import org.ringingmaster.engine.compiler.variance.VarianceFactory;
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

import static com.google.common.base.Preconditions.checkState;
import static org.ringingmaster.engine.composition.MutableComposition.TERMINATION_MAX_PARTS_MAX;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.VARIANCE_DETAIL;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.VARIANCE_OPEN;

/**
 * Validates that where a variance has part numbers specified, they are in bounds.
 *
 * @author Steve Lake
 */
public class ValidateVariancePartNumbersWithinRange implements Function<Parse, Parse> {

    private final Logger log = LoggerFactory.getLogger(ValidateVariancePartNumbersWithinRange.class);

    @Override
    public Parse apply(Parse input) {

        log.debug("[{}] > validate variance part number within range", input.getComposition().getTitle());


        HashBasedTable<Integer, Integer, ParsedCell> compositionCells =
                HashBasedTable.create(input.allCompositionCells().getBackingTable());
        for (BackingTableLocationAndValue<ParsedCell> locationAndCell : input.allCompositionCells()) {
            doInvalidation(compositionCells, locationAndCell);
        }

        HashBasedTable<Integer, Integer, ParsedCell> definitionCells =
                HashBasedTable.create(input.allDefinitionCells().getBackingTable());
        for (BackingTableLocationAndValue<ParsedCell> locationAndCell : input.allDefinitionCells()) {
            doInvalidation(definitionCells, locationAndCell);
        }

        Parse result = new ParseBuilder()
                .prototypeOf(input)
                .setCompositionTableCells(compositionCells)
                .setDefinitionTableCells(definitionCells)
                .build();

        log.debug("[{}] < validate variance part number within range", input.getComposition().getTitle());

        return result;
    }

    private void doInvalidation(HashBasedTable<Integer, Integer, ParsedCell> cells, BackingTableLocationAndValue<ParsedCell> locationAndCell) {

        final ParsedCell cell = locationAndCell.getValue();

        ParsedCellMutator builder = new ParsedCellMutator()
                .prototypeOf(cell);


        for (Group group : cell.allGroups()) {
            if (group.isValid() &&
                    group.getFirstSectionParseType() == VARIANCE_OPEN) {

                checkState(group.getSections().size() == 2);
                checkState(group.getSections().get(1).getParseType() == VARIANCE_DETAIL);

                String characters = cell.getCharacters(group.getSections().get(1)).toLowerCase();

                Set<Integer> invalidParts = VarianceFactory.parseJustPartsForValidation(characters).stream()
                        .filter(part -> part < 0 || part >= TERMINATION_MAX_PARTS_MAX) // The part value will already have 1 subtracted to make 0 based rather than 1 based.
                        .collect(Collectors.toSet());

                if (invalidParts.size() > 0) {
                    builder.invalidateGroup(0, "All variance part numbers must be between 1 and " + TERMINATION_MAX_PARTS_MAX );
                }
            }
        }

        cells.put(locationAndCell.getRow(), locationAndCell.getCol(), builder.build());
    }

}
