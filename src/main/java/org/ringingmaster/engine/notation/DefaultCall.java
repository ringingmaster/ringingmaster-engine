package org.ringingmaster.engine.notation;

import org.ringingmaster.engine.NumberOfBells;

import javax.annotation.concurrent.Immutable;
import java.util.List;

/**
 * Representation of a single call.
 * User: Stephen
 */
@Immutable
class DefaultCall extends DefaultPlaceSetSequence implements Call {

    private final String nameShorthand;

    DefaultCall(String name,
                String nameShorthand,
                NumberOfBells numberOfWorkingBells,
                List<PlaceSet> normalisedNotationElements) {
        super(name, numberOfWorkingBells, normalisedNotationElements);
        this.nameShorthand = nameShorthand;
    }

    @Override
    public String getNameShorthand() {
        return nameShorthand;
    }

    @Override
    public String getNotationDisplayString(boolean concise) {
        return NotationBuilderHelper.getAsDisplayString(getNormalisedNotationElements(), concise);
    }

    @Override
    public String toDisplayString() {
        final StringBuilder buf = new StringBuilder();
        buf.append(getName());
        buf.append(" (").append(getNameShorthand()).append(") :");
        buf.append(getNotationDisplayString(false));
        return buf.toString();
    }

    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder();
        buf.append("[").append(getName());
        buf.append(", ").append(getNameShorthand());
        buf.append(", ").append(getNotationDisplayString(false));
        buf.append("]");
        return buf.toString();
    }
}
