package org.ringingmaster.engine.composition.element;

import com.google.common.collect.ImmutableList;

import java.util.stream.Collectors;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class ElementBuilder {

    public static ImmutableList<Element> createElements(String characters) {
        return characters.chars()
                .mapToObj(value -> new Element((char) value))
                .collect(Collectors.collectingAndThen(Collectors.toList(), ImmutableList::copyOf));

    }
}
