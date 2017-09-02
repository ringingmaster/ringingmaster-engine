package org.ringingmaster.engine.touch.newcontainer.cell;

import org.ringingmaster.engine.touch.newcontainer.element.Element;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class EmptyCell implements Cell{

    public static EmptyCell INSTANCE = new EmptyCell();


    @Override
    public Element getElement(int index) {
        throw new NotImplementedException();
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
