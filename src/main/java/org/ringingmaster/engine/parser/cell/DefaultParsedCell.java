package org.ringingmaster.engine.parser.cell;

import com.google.common.collect.ImmutableList;
import org.ringingmaster.engine.parser.cell.grouping.ElementRange;
import org.ringingmaster.engine.parser.cell.grouping.Group;
import org.ringingmaster.engine.parser.cell.grouping.Section;
import org.ringingmaster.engine.touch.cell.Cell;
import org.ringingmaster.engine.touch.element.Element;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkElementIndex;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO comments???
 *
 * @author stevelake
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
    public int getElementSize() {
        return parentCell.getElementSize();
    }

    @Override
    public Element getElement(int index) {
        return parentCell.getElement(index);
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
        checkArgument(elementRange.getStartIndex() + elementRange.getLength() <= getElementSize());

        StringBuilder buff = new StringBuilder();
        for (int index = elementRange.getStartIndex(); index< elementRange.getStartIndex() + elementRange.getLength(); index++) {
            buff.append(getElement(index).getCharacter());
        }
        return buff.toString();
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
