package org.ringingmaster.engine.parser.cell;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.ringingmaster.engine.parser.cell.grouping.Group;
import org.ringingmaster.engine.parser.cell.grouping.GroupingFactory;
import org.ringingmaster.engine.parser.cell.grouping.Section;
import org.ringingmaster.engine.composition.cell.Cell;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkElementIndex;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class ParsedCellFactory {

    @Deprecated //TODO Deprecated or simply used in tests
    public static ParsedCell buildParsedCellFromSections(Cell parentCell, Set<Section> sections) {
        Set<Group> groups = buildGroupsToMatchSections(sections);
        return buildParsedCellFromGroups(parentCell, groups);
    }

    private static Set<Group> buildGroupsToMatchSections(Set<Section> sections) {
        return sections.stream()
                .map(GroupingFactory::buildGroupToMatchSection)
                .collect(Collectors.toSet());
    }

    public static ParsedCell buildParsedCellFromGroups(Cell parentCell, Set<Group> groups) {
        Set<Section> sections = groups.stream().flatMap(new Function<Group, Stream<Section>>() {
            @Nullable
            @Override
            public Stream<Section> apply(@Nullable Group input) {
                return input.getSections().stream();
            }
        }).collect(Collectors.toSet());

        Section[] sectionByElementIndex = new Section[parentCell.getElementSize()];
        createSectionIndex(sections, sectionByElementIndex);

        Group[] groupByElementIndex = new Group[parentCell.getElementSize()];
        createGroupIndex(groups, groupByElementIndex);

        ImmutableList<Section> allSections = ImmutableList.sortedCopyOf(Section.BY_START_INDEX, sections);
        ImmutableList<Group> allGroups = ImmutableList.sortedCopyOf(Group.BY_START_INDEX, groups);

        return new DefaultParsedCell(parentCell, sectionByElementIndex, groupByElementIndex, allSections, allGroups);
    }


    private static void createSectionIndex(Set<Section> sections, Section[] result) {
        for (Section section : sections) {
            for (int index = section.getStartIndex(); index < section.getEndIndex() ; index++) {
                checkElementIndex(index, result.length, "Section [" + section + "] outside underlying cell element size.");
                checkArgument(result[index] == null,
                        "Section [%s] overlaps into section [%s]", section, result[index]);
                result[index] = section;
            }
        }
    }

    private static void createGroupIndex(Set<Group> groups, Group[] result) {
        for (Group section : groups) {
            for (int index = section.getStartIndex(); index < section.getStartIndex()+section.getLength() ; index++) {
                result[index] = section;
            }
        }
    }
}
