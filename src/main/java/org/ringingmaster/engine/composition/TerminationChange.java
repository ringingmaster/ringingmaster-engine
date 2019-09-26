package org.ringingmaster.engine.composition;


import org.ringingmaster.engine.method.Row;

import java.util.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

public class TerminationChange {

    public enum Location {
        ANYWHERE,
        LEAD_END;
    }

    TerminationChange(Row change, Location location) {
        this.change = checkNotNull(change);
        this.location = checkNotNull(location);
    }

    private final Row change;
    private final Location location;

    public String getDisplayString() {
        return change.getDisplayString(true) +
                (location == Location.LEAD_END ? " (at Lead End)" : "");
    }

    public Row getChange() {
        return change;
    }

    public Location getLocation() {
        return location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TerminationChange that = (TerminationChange) o;
        return Objects.equals(change, that.change) &&
                location == that.location;
    }

    @Override
    public int hashCode() {
        return Objects.hash(change, location);
    }

    @Override
    public String toString() {
        return "TerminationChange{" +
                "change=" + change +
                ", location=" + location +
                '}';
    }
}

