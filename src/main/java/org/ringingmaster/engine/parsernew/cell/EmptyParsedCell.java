package org.ringingmaster.engine.parsernew.cell;

import org.ringingmaster.engine.touch.newcontainer.element.Element;

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
    public Optional<Section> getSectionAtElementIndex(int elementIndex) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public Optional<Group> getGroupAtElementIndex(int elementIndex) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public String toString() {
        return "EmptyParsedCell";
    }
}
