package org.ringingmaster.engine.parsernew.cell;

import com.google.common.collect.ImmutableList;
import org.ringingmaster.engine.parser.ParseType;
import org.ringingmaster.engine.touch.newcontainer.cell.Cell;
import org.ringingmaster.engine.touch.newcontainer.definition.DefinitionCell;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class ParsedCellFactory {

    public static ParsedCell buildParsedCell(Cell cell, Set<Section> sections) {
        Set<Group> groups = createGroupsToMatchSections(sections);
        return buildParsedCell(cell, sections, groups);
    }

    public static ParsedDefinitionCell buildParsedCell(DefinitionCell cell, Set<Section> sections) {
        Set<Group> groups = createGroupsToMatchSections(sections);
        return buildParsedCell(cell, sections, groups);
    }

    private static Set<Group> createGroupsToMatchSections(Set<Section> sections) {
        return sections.stream()
                .map(section ->new DefaultGroup(section.getElementStartIndex(), section.getElementLength(), true, Optional.empty(), section))
                .collect(Collectors.toSet());
    }

    public static ParsedCell buildParsedCell(Cell parentCell, Set<Section> sections, Set<Group> groups) {
        Section[] sectionByElementIndex = new Section[parentCell.getElementSize()];
        createSectionIndex(sections, sectionByElementIndex);

        Group[] groupByElementIndex = new Group[parentCell.getElementSize()];
        createGroupIndex(groups, groupByElementIndex);

        ImmutableList<Section> allSections = ImmutableList.sortedCopyOf(Section.BY_START_POSITION, sections);
        ImmutableList<Group> allGroups = ImmutableList.sortedCopyOf(Group.BY_START_POSITION, groups);

        return new DefaultParsedCell(parentCell, sectionByElementIndex, groupByElementIndex, allSections, allGroups);
    }

    public static ParsedDefinitionCell buildParsedCell(DefinitionCell parentCell, Set<Section> sections, Set<Group> groups) {
        Section[] sectionByElementIndex = new Section[parentCell.getElementSize()];
        createSectionIndex(sections, sectionByElementIndex);

        Group[] groupByElementIndex = new Group[parentCell.getElementSize()];
        createGroupIndex(groups, groupByElementIndex);

        ImmutableList<Section> allSections = ImmutableList.sortedCopyOf(Section.BY_START_POSITION, sections);
        ImmutableList<Group> allGroups = ImmutableList.sortedCopyOf(Group.BY_START_POSITION, groups);

        return new DefaultParsedDefinitionCell(parentCell, sectionByElementIndex, groupByElementIndex, allSections, allGroups);
    }

    private static void createSectionIndex(Set<Section> sections, Section[] result) {
        for (Section section : sections) {
            for (int index = section.getElementStartIndex(); index < section.getElementStartIndex()+section.getElementLength() ; index++) {
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
}
