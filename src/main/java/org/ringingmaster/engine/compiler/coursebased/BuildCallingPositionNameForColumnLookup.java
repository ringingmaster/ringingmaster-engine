package org.ringingmaster.engine.compiler.coursebased;

import com.google.common.collect.ImmutableList;
import org.ringingmaster.engine.arraytable.ImmutableArrayTable;
import org.ringingmaster.engine.parser.cell.ParsedCell;
import org.ringingmaster.engine.parser.cell.grouping.Group;
import org.ringingmaster.engine.parser.cell.grouping.Section;
import org.ringingmaster.engine.parser.parse.Parse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkState;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.CALLING_POSITION;

/**
 * Build the mapping between the name of the calling position (W, B, M, H Etc.) and the column index.
 *
 * @author Steve Lake
 */
public class BuildCallingPositionNameForColumnLookup implements Function<CourseBasedCompilerPipelineData, CourseBasedCompilerPipelineData> {

    private final Logger log = LoggerFactory.getLogger(BuildCallingPositionNameForColumnLookup.class);

    @Override
    public CourseBasedCompilerPipelineData apply(CourseBasedCompilerPipelineData input) {

        if (input.isTerminated()) {
            return input;
        }

        log.debug("{} > creating call position for column lookup", input.getLogPreamble());

        final ImmutableList<Optional<String>> callingPositionLookupForColumn = buildCallingPositionNameForColumnLookup(input.getParse());

        log.debug("{} < creating call position for column lookup. [{}]", input.getLogPreamble(), callingPositionLookupForColumn);

        return input.setCallingPositionNameLookupByColumn(callingPositionLookupForColumn);
    }

    private ImmutableList<Optional<String>> buildCallingPositionNameForColumnLookup(Parse parse) {
        final ImmutableArrayTable<ParsedCell> callingPositionCells = parse.callingPositionCells();
        checkState(callingPositionCells.getRowSize() == 1);

        final ImmutableList.Builder<Optional<String>> builder = ImmutableList.builder();

        for (int columnIndex=0;columnIndex<callingPositionCells.getColumnSize();columnIndex++) {
            final ParsedCell cell = callingPositionCells.get(0, columnIndex);
            builder.add(getCallingPositionFromCell(cell));
        }

        return builder.build();
    }

    private Optional<String> getCallingPositionFromCell(ParsedCell cell) {
        for (Group group : cell.allGroups()) {
            if (group.isValid()) {
                for (Section section : group.getSections()) {
                    if (section.getParseType() == CALLING_POSITION) {
                        return Optional.of(cell.getCharacters(section));
                    }
                }
            }
        }
        return Optional.empty();
    }
}
