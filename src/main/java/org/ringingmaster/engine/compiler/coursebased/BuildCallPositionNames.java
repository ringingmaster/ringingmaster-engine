package org.ringingmaster.engine.compiler.coursebased;

import com.google.common.collect.ImmutableList;
import org.ringingmaster.engine.arraytable.ImmutableArrayTable;
import org.ringingmaster.engine.parser.cell.grouping.Group;
import org.ringingmaster.engine.parser.cell.ParsedCell;
import org.ringingmaster.engine.parser.cell.grouping.Section;
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
public class BuildCallPositionNames implements Function<CourseBasedCompilePipelineData, CourseBasedCompilePipelineData> {

    @Override
    public CourseBasedCompilePipelineData apply(CourseBasedCompilePipelineData data) {

        final ImmutableList<Optional<String>> callPositionNames = buildCallPositionNames(data.getParse());
        return data.setCallPositionNames(callPositionNames);
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
