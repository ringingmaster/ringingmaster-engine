package org.ringingmaster.engine.notation;

import com.google.common.collect.ImmutableList;

import javax.annotation.concurrent.Immutable;
import java.util.Set;

/**
 * Default implementation of the PlaceSet interface. Only to be constructed from
 * this package, using the NotationBuilder.
 *
 * @author Steve Lake
 */
@Immutable
class DefaultPlaceSet implements PlaceSet {


    private final ImmutableList<Place> sortedPlaces;

    DefaultPlaceSet(final Set<Place> elements) {
        // The sorted version is needed so toDisplayString() is correct
        sortedPlaces = ImmutableList.sortedCopyOf(elements);
    }

    @Override
    public int size() {
        return sortedPlaces.size();
    }

    @Override
    public Place get(final int index) {
        return sortedPlaces.get(index);
    }

    @Override
    public boolean makesPlace(Place place) {
        return sortedPlaces.contains(place);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((sortedPlaces == null) ? 0 : sortedPlaces.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DefaultPlaceSet other = (DefaultPlaceSet) obj;
        if (sortedPlaces == null) {
            if (other.sortedPlaces != null) {
                return false;
            }
        } else if (!sortedPlaces.equals(other.sortedPlaces)) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isAllChange() {
        final boolean allChange = (sortedPlaces.size() == 1) &&
                sortedPlaces.contains(Place.ALL_CHANGE);
        return allChange;
    }

    @Override
    public String toDisplayString() {
        final StringBuffer buf = new StringBuffer();
        for (final Place element : sortedPlaces) {
            buf.append(element.toDisplayString());
        }

        return buf.toString();
    }

    @Override
    public String toString() {
        return "DefaultPlaceSet [" + toDisplayString() + "]";
    }
}