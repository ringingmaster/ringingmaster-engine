package org.ringingmaster.engine.touch.cell;

import com.google.common.collect.ImmutableList;
import javax.annotation.concurrent.Immutable;
import org.ringingmaster.engine.touch.element.Element;

import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO Comments
 *
 * @author Lake
 */
@Immutable
public class DefaultCell implements Cell {

    private final ImmutableList<Element> elements;
    private final String characters;

    public DefaultCell(List<Element> elements) {
        this.elements = ImmutableList.copyOf(elements);
        this.characters = elements.stream()
                .map(element -> element.getCharacter())
                .collect(Collectors.joining());
    }

    @Override
    public Element getElement(int index) {
        return elements.get(index);
    }

    @Override
    public int getElementSize() {
        return elements.size();
    }

    @Override
    public String getCharacters() {
        return characters;
    }

    @Override
    public String toString() {
        return "Cell{" +
                "elements=" + getCharacters() +
                '}';
    }
}
