package org.ringingmaster.engine.touch.container.cell;

import org.ringingmaster.engine.touch.container.element.Element;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class EmptyCell implements Cell{

    public static EmptyCell INSTANCE = new EmptyCell();

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
    public String toString() {
        return "EmptyCell";
    }
}
