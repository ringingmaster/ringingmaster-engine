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
public class DefaultDefinitionCell extends DefaultCell implements DefinitionCell {

    private final String shorthand;

    public DefaultDefinitionCell(String shorthand, List<Element> elements) {
        super(elements);
        this.shorthand = shorthand;
    }

    @Override
    public String getShorthand() {
        return shorthand;
    }

    @Override
    public String toString() {
        return "DefinitionCell{" +
                "shorthand='" + shorthand + '\'' + ", " +
                "elements=" + getCharacters() +
                '}';
    }
}
