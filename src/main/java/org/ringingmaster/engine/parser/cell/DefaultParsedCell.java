package org.ringingmaster.engine.parser.cell;

import com.google.common.collect.ImmutableList;
import org.ringingmaster.engine.composition.cell.Cell;
import org.ringingmaster.engine.parser.cell.grouping.ElementRange;
import org.ringingmaster.engine.parser.cell.grouping.Group;
import org.ringingmaster.engine.parser.cell.grouping.Section;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkElementIndex;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
class DefaultParsedCell implements ParsedCell {

    private final Cell parentCell;
    private final Section[] sectionByElementIndex;
    private final Group[] groupByElementIndex;
    private final ImmutableList<Section> allSections;
    private final ImmutableList<Group> allGroups;

    DefaultParsedCell(Cell parentCell, Section[] sectionByElementIndex, Group[] groupByElementIndex,
                      ImmutableList<Section> allSections, ImmutableList<Group> allGroups) {
        this.parentCell = checkNotNull(parentCell);
        this.sectionByElementIndex = checkNotNull(sectionByElementIndex);
        this.groupByElementIndex = checkNotNull(groupByElementIndex);
        this.allSections = checkNotNull(allSections);
        this.allGroups = checkNotNull(allGroups);
    }

    @Override
    public Cell getParentCell() {
        return parentCell;
    }

    @Override
    public int size() {
        return parentCell.size();
    }

    @Override
    public char get(int index) {
        return parentCell.get(index);
    }

    @Override
    public String getCharacters() {
        return parentCell.getCharacters();
    }

    @Override
    public ImmutableList<Section> allSections() {
        return allSections;
    }

    @Override
    public ImmutableList<Group> allGroups() {
        return allGroups;
    }

    @Override
    public Optional<Section> getSectionAtElementIndex(final int elementIndex) {
        checkElementIndex(elementIndex, sectionByElementIndex.length);
        return Optional.ofNullable(sectionByElementIndex[elementIndex]);
    }

    @Override
    public Optional<Group> getGroupAtElementIndex(int elementIndex) {
        checkElementIndex(elementIndex, groupByElementIndex.length);
        return Optional.ofNullable(groupByElementIndex[elementIndex]);
    }

    @Override
    public Group getGroupForSection(Section section) {
        return getGroupAtElementIndex(section.getStartIndex())
                .orElseThrow(IllegalStateException::new);
    }

    @Override
    public String getCharacters(ElementRange elementRange) {
        checkNotNull(elementRange);
        checkArgument(elementRange.getStartIndex() + elementRange.getLength() <= size());

        StringBuilder buff = new StringBuilder();
        for (int index = elementRange.getStartIndex(); index< elementRange.getStartIndex() + elementRange.getLength(); index++) {
            buff.append(get(index));
        }
        return buff.toString();
    }

    @Override
    public String prettyPrint() {
        StringBuilder groupRow = new StringBuilder("|");
        StringBuilder sectionRow = new StringBuilder("|");
        StringBuilder parsedTypeRow = new StringBuilder("|");
        Map<Integer, String> messages = new HashMap<>();

        for (int groupIndex = 0;groupIndex< allGroups.size();groupIndex++) {
            Group group = allGroups.get(groupIndex);
            groupRow.append("[").append(groupIndex).append("]").append(group.getStartIndex());
            groupRow.append(group.isValid()?"-✓":"-X");
            if (!group.getMessages().isEmpty()){
                messages.put(groupIndex, group.getMessages().stream().collect(Collectors.joining(",", "[", "]")));
            }

            for (Section section : group.getSections()) {

                for (int i=0;i<section.getLength();i++) {
                    addBlockChar(groupRow, parsedTypeRow.length()-groupRow.length(), '-');
                    addBlockChar(sectionRow, parsedTypeRow.length()-sectionRow.length(), '-');

                    parsedTypeRow.append(" ").append(section.getParseType().toString()).append(" ");
                    sectionRow.append(" ")
                            .append(parentCell.get(section.getStartIndex() + i))
                            .append(' ');

                    addBlockChar(groupRow, parsedTypeRow.length()-groupRow.length(), '-');
                    addBlockChar(sectionRow, parsedTypeRow.length()-sectionRow.length(), '-');

                    parsedTypeRow.append("|");
                }
                sectionRow.append("|");
            }
            groupRow.append("|");
        }

        StringBuilder result = new StringBuilder();

        result.append(groupRow);
        result.append(System.lineSeparator()).append(sectionRow);
        result.append(System.lineSeparator()).append(parsedTypeRow);

        for (Map.Entry<Integer, String> entry : messages.entrySet()) {
            result.append(System.lineSeparator())
                    .append("[").append(entry.getKey()).append("]=")
                    .append(entry.getValue());

        }

        return result.toString();
    }

    private void addBlockChar(StringBuilder builder, int length, char character) {
        for (int j = 0; j < length; j++) {
            builder.append(character);
        }
    }

    @Override
    public String toString() {
        return "DefaultParsedCell{" +
                "cell=" + parentCell +
                ", allSections=" + allSections +
                ", allGroups=" + allGroups +
                '}';
    }
}
