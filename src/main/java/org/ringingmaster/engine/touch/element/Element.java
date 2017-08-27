package org.ringingmaster.engine.touch.element;

import net.jcip.annotations.Immutable;
import org.ringingmaster.engine.touch.variance.Variance;

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


    Element(char character, Optional<Variance> variance) {
        this.character = character;
        this.variance = variance;
    }

    public char getCharacter() {
        return character;
    }

    public Optional<Variance> getVariance() {
        return variance;
    }
}
