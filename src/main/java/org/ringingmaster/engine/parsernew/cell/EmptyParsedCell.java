package org.ringingmaster.engine.parsernew.cell;

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
    public int getElementSize() {
        return 0;
    }

    @Override
    public Optional<Section> getSectionAtElementIndex(int elementIndex) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public Optional<Group> getWordAtElementIndex(int elementIndex) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public String toString() {
        return "EmptyParsedCell";
    }
}
