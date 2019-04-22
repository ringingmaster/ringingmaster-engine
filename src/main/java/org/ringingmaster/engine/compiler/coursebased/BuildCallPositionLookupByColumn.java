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
 * TODO comments???
 *
 * @author stevelake
 */
public class BuildCallPositionLookupByColumn implements Function<CourseBasedCompilerPipelineData, CourseBasedCompilerPipelineData> {

    private final Logger log = LoggerFactory.getLogger(BuildCallPositionLookupByColumn.class);

    @Override
    public CourseBasedCompilerPipelineData apply(CourseBasedCompilerPipelineData input) {

        if (input.isTerminated()) {
            return input;
        }

        log.debug("{} > creating call position lookup by column", input.getLogPreamble());

        final ImmutableList<Optional<String>> callPositionLookupByColumn = buildCallPositionNames(input.getParse());
        CourseBasedCompilerPipelineData result = input.setCallPositionLookupByColumn(callPositionLookupByColumn);

        log.debug("{} > creating call position lookup by column", input.getLogPreamble());

        return result;
    }

    private ImmutableList<Optional<String>> buildCallPositionNames(Parse parse) {
        final ImmutableArrayTable<ParsedCell> callPositionCells = parse.callPositionCells();
        checkState(callPositionCells.getRowSize() == 1);

        final ImmutableList.Builder<Optional<String>> builder = ImmutableList.builder();

        for (int columnIndex=0;columnIndex<callPositionCells.getColumnSize();columnIndex++) {
            final ParsedCell cell = callPositionCells.get(0, columnIndex);
            builder.add(getCallPositionFromCell(cell));
        }

        return builder.build();
    }

    private Optional<String> getCallPositionFromCell(ParsedCell cell) {
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
