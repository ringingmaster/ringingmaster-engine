package org.ringingmaster.engine.touch.newcontainer.element;

import net.jcip.annotations.Immutable;
import org.ringingmaster.engine.touch.newcontainer.variance.Variance;

import java.util.Optional;

/**
 * TODO Comments
 *
 * @author Lake
 */
@Immutable
public class Element {

    private final char character;
    private final Optional<Variance> variance;


    public Element(char character) {
        this.character = character;
        this.variance = Optional.empty();
    }

    public Element(char character, Optional<Variance> variance) {
        this.character = character;
        this.variance = variance;
    }

    public char getCharacter() {
        return character;
    }

    public Optional<Variance> getVariance() {
        return variance;
    }

    @Override
    public String toString() {
        return "Element{" +
                "character=" + character +
                ", variance=" + variance +
                '}';
    }
}
