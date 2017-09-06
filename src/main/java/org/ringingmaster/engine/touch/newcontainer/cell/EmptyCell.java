package org.ringingmaster.engine.touch.newcontainer.cell;

import org.ringingmaster.engine.touch.newcontainer.element.Element;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class EmptyCell implements Cell{

    public static EmptyCell INSTANCE = new EmptyCell();


    @Override
    public Element getElement(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public String getCharacters() {
        return "";
    }
}
