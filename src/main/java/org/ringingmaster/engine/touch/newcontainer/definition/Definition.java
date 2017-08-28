package org.ringingmaster.engine.touch.newcontainer.definition;

import com.google.errorprone.annotations.Immutable;
import org.ringingmaster.engine.touch.newcontainer.cell.Cell;
import org.ringingmaster.engine.touch.newcontainer.element.Element;

import java.util.List;

/**
 * TODO Comments
 *
 * @author Lake
 */
@Immutable
public class Definition extends Cell {

    private final String shorthand;

    public Definition(String shorthand, List<Element> elements) {
        super(elements);
        this.shorthand = shorthand;
    }

    public String getShorthand() {
        return shorthand;
    }

    @Override
    public String toString() {
        return "Definition{" +
                "shorthand='" + shorthand + '\'' + ", " +
                "elements=" + getCharacters() +
                '}';
    }
}
