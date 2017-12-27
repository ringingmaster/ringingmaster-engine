package org.ringingmaster.engine.parser.cell;

import com.google.common.collect.ImmutableList;
import org.ringingmaster.engine.touch.container.element.Element;

import java.util.Optional;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class EmptyParsedCell implements ParsedCell {

    public static ParsedCell INSTANCE = new EmptyParsedCell();

    private EmptyParsedCell() {
    }

    @Override
    public Element getElement(int index) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public int getElementSize() {
        return 0;
    }

    @Override
    public String getCharacters() {
        return "";
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
    public String getCharacters(ElementSequence elementSequence) {
        return "";
    }

    @Override
    public String toString() {
        return "EmptyParsedCell";
    }
}
