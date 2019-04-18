package org.ringingmaster.engine.composition.element;

import org.ringingmaster.engine.compiler.variance.Variance;

import javax.annotation.concurrent.Immutable;
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
    private final Optional<Variance> variance; //TODO we should be able to simply enter variances as text instead of special handling.


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
