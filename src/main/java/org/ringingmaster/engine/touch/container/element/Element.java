package org.ringingmaster.engine.touch.container.element;

import net.jcip.annotations.Immutable;
import org.ringingmaster.engine.touch.container.variance.Variance;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO Comments
 *
 * @author Lake
 */
@Immutable
public class Element {

    private final String character;
    private final Optional<Variance> variance;


    public Element(char character) {
        this(character,Optional.empty());
    }

    public Element(char character, Optional<Variance> variance) {
        this.character = Character.toString(character);
        this.variance = checkNotNull(variance);
    }

    public String getCharacter() {
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
