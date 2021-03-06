package org.ringingmaster.engine.parser.cell;

import com.google.common.collect.ImmutableList;
import org.ringingmaster.engine.composition.cell.Cell;
import org.ringingmaster.engine.composition.cell.EmptyCell;
import org.ringingmaster.engine.parser.cell.grouping.ElementRange;
import org.ringingmaster.engine.parser.cell.grouping.Group;
import org.ringingmaster.engine.parser.cell.grouping.Section;

import java.util.Optional;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
public class EmptyParsedCell implements ParsedCell {

    public static ParsedCell INSTANCE = new EmptyParsedCell();

    private EmptyParsedCell() {
    }

    @Override
    public char get(int index) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public String getCharacters() {
        return "";
    }

    @Override
    public Cell getParentCell() {
        return EmptyCell.INSTANCE;
    }

    @Override
    public ImmutableList<Section> allSections() {
        return ImmutableList.of();
    }

    @Override
    public ImmutableList<Group> allGroups() {
        return ImmutableList.of();
    }

    @Override
    public Optional<Section> getSectionAtElementIndex(int elementIndex) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public Optional<Group> getGroupAtElementIndex(int elementIndex) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public Group getGroupForSection(Section section) {
        throw new IllegalStateException();
    }

    @Override
    public String getCharacters(ElementRange elementRange) {
        return "";
    }

    @Override
    public String prettyPrint() {
        return "<EMPTY_CELL>";
    }

    @Override
    public String toString() {
        return "EmptyParsedCell";
    }
}
