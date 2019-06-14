package org.ringingmaster.engine.notation;

import org.ringingmaster.engine.NumberOfBells;

import javax.annotation.concurrent.Immutable;
import java.util.List;
import java.util.Objects;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DefaultCall)) return false;
        DefaultCall placeSets = (DefaultCall) o;
        return Objects.equals(getNameShorthand(), placeSets.getNameShorthand()) &&
                Objects.equals(getName(), placeSets.getName()) &&
                getNumberOfWorkingBells() == placeSets.getNumberOfWorkingBells() &&
                Objects.equals(getNormalisedNotationElements(), placeSets.getNormalisedNotationElements());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNameShorthand(), getName(), getNumberOfWorkingBells(), getNormalisedNotationElements());
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
        return "{" +
                getName() + ',' +
                nameShorthand + ',' +
                getNotationDisplayString(false) +
                '}';
    }
}
