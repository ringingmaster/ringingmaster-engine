package org.ringingmaster.engine.touch.newcontainer.cell;

import com.google.common.collect.ImmutableList;
import net.jcip.annotations.Immutable;
import org.ringingmaster.engine.touch.newcontainer.element.Element;

import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO Comments
 *
 * @author Lake
 */
@Immutable
public class Cell {

    private final ImmutableList<Element> elements;

    public Cell(List<Element> elements) {
        this.elements = ImmutableList.copyOf(elements);
    }

    public Element getElement(int index) {
        return elements.get(index);
    }

    public int size() {
        return elements.size();
    }

    public String getCharacters() {
        return elements.stream()
                .map(element -> Character.toString(element.getCharacter()))
                .collect(Collectors.joining());
    }

    @Override
    public String toString() {
        return "Cell{" +
                "elements=" + getCharacters() +
                '}';
    }
}
