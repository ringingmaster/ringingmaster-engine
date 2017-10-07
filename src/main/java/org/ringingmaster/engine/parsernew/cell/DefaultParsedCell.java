package org.ringingmaster.engine.parsernew.cell;

import org.ringingmaster.engine.touch.newcontainer.cell.Cell;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkPositionIndex;

/**
 * TODO comments???
 *
 * @author stevelake
 */
class DefaultParsedCell implements ParsedCell {

    private final Cell parentCell;
    private final Section[] sectionByElement;
    private final Group[] groupByElement;

    DefaultParsedCell(Cell parentCell, Section[] sectionByElement, Group[] groupByElement) {
        this.parentCell = checkNotNull(parentCell);
        this.sectionByElement = checkNotNull(sectionByElement);
        this.groupByElement = checkNotNull(groupByElement);
    }

    @Override
    public int getElementSize() {
        return parentCell.size();
    }

    @Override
    public Optional<Section> getSectionAtElementIndex(final int elementIndex) {
        checkPositionIndex(elementIndex, sectionByElement.length);
        return Optional.ofNullable(sectionByElement[elementIndex]);
    }

    @Override
    public Optional<Group> getWordAtElementIndex(int elementIndex) {
        checkPositionIndex(elementIndex, groupByElement.length);
        return Optional.ofNullable(groupByElement[elementIndex]);
    }

}
