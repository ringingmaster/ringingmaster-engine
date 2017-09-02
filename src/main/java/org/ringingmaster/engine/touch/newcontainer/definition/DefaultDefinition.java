package org.ringingmaster.engine.touch.newcontainer.definition;

import com.google.errorprone.annotations.Immutable;
import org.ringingmaster.engine.touch.newcontainer.cell.DefaultCell;
import org.ringingmaster.engine.touch.newcontainer.element.Element;

import java.util.List;

/**
 * TODO Comments
 *
 * @author Lake
 */
@Immutable
public class DefaultDefinition extends DefaultCell implements Definition {

    private final String shorthand;

    public DefaultDefinition(String shorthand, List<Element> elements) {
        super(elements);
        this.shorthand = shorthand;
    }

    @Override
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
