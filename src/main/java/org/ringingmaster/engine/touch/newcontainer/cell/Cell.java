package org.ringingmaster.engine.touch.newcontainer.cell;

import org.ringingmaster.engine.touch.newcontainer.element.Element;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public interface Cell {

    Element getElement(int index);

    int getElementSize();

    String getCharacters();
}
