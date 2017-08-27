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
public class DefinitionCell extends Cell {

    private final String shorthand;

    public DefinitionCell(String shorthand, List<Element> elements) {
        super(elements);
        this.shorthand = shorthand;
    }

    public String getShorthand() {
        return shorthand;
    }
}
