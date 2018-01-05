package org.ringingmaster.engine.parser.cell;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.ringingmaster.engine.parser.ParseType;
import org.ringingmaster.engine.touch.cell.Cell;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkElementIndex;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class ParsedCellFactory {

    public static ParsedCell buildParsedCell(Cell parentCell, Set<Section> sections) {
        Set<Group> groups = buildGroupsToMatchSections(sections);
        return buildParsedCell(parentCell, sections, groups);
    }

    private static Set<Group> buildGroupsToMatchSections(Set<Section> sections) {
        return sections.stream()
                .map(ParsedCellFactory::buildGroupToMatchSection)
                .collect(Collectors.toSet());
    }

    public static Group buildGroupToMatchSection(Section section) {
        return buildGroup(section.getElementStartIndex(), section.getElementLength(), true, Optional.empty(), Lists.newArrayList(section));
    }

    public static ParsedCell buildParsedCell(Cell parentCell, Set<Section> sections, Set<Group> groups) {
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
            for (int index = section.getElementStartIndex(); index < section.getElementStartIndex()+section.getElementLength() ; index++) {
                checkElementIndex(index, result.length, "Section [" + section + "] outside underlying cell element size.");
                checkArgument(result[index] == null,
                        "Section [%s] overlaps into section [%s]", section, result[index]);
                result[index] = section;
            }
        }
    }

    private static void createGroupIndex(Set<Group> groups, Group[] result) {
        for (Group section : groups) {
            for (int index = section.getElementStartIndex(); index < section.getElementStartIndex()+section.getElementLength() ; index++) {
                result[index] = section;
            }
        }
    }

    public static Section buildSection(int elementStartIndex, int elementLength, ParseType parseType) {
        return new DefaultSection(elementStartIndex, elementLength, parseType);
    }

    public static Group buildGroup(int elementStartIndex, int elementLength, boolean valid, Optional<String> message, Collection<Section> sections) {
        return new DefaultGroup(elementStartIndex, elementLength, valid, message, sections);
    }
}
