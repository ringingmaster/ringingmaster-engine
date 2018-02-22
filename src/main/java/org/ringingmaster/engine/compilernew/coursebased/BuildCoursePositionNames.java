package org.ringingmaster.engine.compilernew.coursebased;

import com.google.common.collect.ImmutableList;
import org.ringingmaster.engine.arraytable.ImmutableArrayTable;
import org.ringingmaster.engine.parser.cell.Group;
import org.ringingmaster.engine.parser.cell.ParsedCell;
import org.ringingmaster.engine.parser.cell.Section;
import org.ringingmaster.engine.parser.parse.Parse;

import java.util.Optional;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkState;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.CALLING_POSITION;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class BuildCoursePositionNames implements Function<CourseBasedCompilerPipelineData, CourseBasedCompilerPipelineData> {

    @Override
    public CourseBasedCompilerPipelineData apply(CourseBasedCompilerPipelineData data) {

        final ImmutableList<Optional<String>> callPositionNames = buildCoursePositionNames(data.getParse());
        return data.setCallPositionNames(callPositionNames);
    }

    private ImmutableList<Optional<String>> buildCoursePositionNames(Parse parse) {
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
