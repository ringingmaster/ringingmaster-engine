package org.ringingmaster.engine.parser.brace;

import com.google.common.collect.HashBasedTable;
import org.ringingmaster.engine.arraytable.BackingTableLocationAndValue;
import org.ringingmaster.engine.compiler.variance.Variance;
import org.ringingmaster.engine.parser.cell.ParsedCell;
import org.ringingmaster.engine.parser.cell.grouping.Group;
import org.ringingmaster.engine.parser.cell.mutator.ParsedCellMutator;
import org.ringingmaster.engine.parser.parse.Parse;
import org.ringingmaster.engine.parser.parse.ParseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

import static com.google.common.base.Preconditions.checkState;
import static org.ringingmaster.engine.compiler.variance.VarianceFactory.parseVariance;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.VARIANCE_DETAIL;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.VARIANCE_OPEN;

/**
 * Validates that where a variance has part numbers specified, they are in bounds.
 *
 * @author Steve Lake
 */
public class SetVarianceMessage implements Function<Parse, Parse> {

    private final Logger log = LoggerFactory.getLogger(SetVarianceMessage.class);

    @Override
    public Parse apply(Parse input) {

        log.debug("[{}] > set variance message", input.getComposition().getLoggingTag());


        HashBasedTable<Integer, Integer, ParsedCell> compositionCells =
                HashBasedTable.create(input.allCompositionCells().getBackingTable());
        for (BackingTableLocationAndValue<ParsedCell> locationAndCell : input.allCompositionCells()) {
            doSetMessage(compositionCells, locationAndCell);
        }

        HashBasedTable<Integer, Integer, ParsedCell> definitionCells =
                HashBasedTable.create(input.allDefinitionCells().getBackingTable());
        for (BackingTableLocationAndValue<ParsedCell> locationAndCell : input.allDefinitionCells()) {
            doSetMessage(definitionCells, locationAndCell);
        }

        Parse result = new ParseBuilder()
                .prototypeOf(input)
                .setCompositionTableCells(compositionCells)
                .setDefinitionTableCells(definitionCells)
                .build();

        log.debug("[{}] < set variance message", input.getComposition().getLoggingTag());

        return result;
    }

    private void doSetMessage(HashBasedTable<Integer, Integer, ParsedCell> cells, BackingTableLocationAndValue<ParsedCell> locationAndCell) {

        final ParsedCell cell = locationAndCell.getValue();

        ParsedCellMutator builder = new ParsedCellMutator()
                .prototypeOf(cell);


        for (Group group : cell.allGroups()) {
            if (group.isValid() &&
                    group.getFirstSectionParseType() == VARIANCE_OPEN) {

                checkState(group.getSections().size() == 2);
                checkState(group.getSections().get(1).getParseType() == VARIANCE_DETAIL);

                String characters = cell.getCharacters(group.getSections().get(1)).toLowerCase();
                Variance variance = parseVariance(characters);

                builder.setGroupMessage(group.getStartIndex(), variance.toHumanReadableString());
            }
        }

        cells.put(locationAndCell.getRow(), locationAndCell.getCol(), builder.build());
    }

}
