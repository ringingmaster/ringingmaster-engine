package org.ringingmaster.engine.notation;

import com.google.common.collect.ImmutableList;
import org.ringingmaster.engine.NumberOfBells;

import javax.annotation.concurrent.Immutable;
import java.util.Iterator;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO
 * User: Stephen
 */
@Immutable
abstract class DefaultPlaceSetSequence implements PlaceSetSequence {

    private final String name;
    private final NumberOfBells numberOfWorkingBells;

    /**
     * A List of PlaceSets. NOTE Where this is used in an implementation that folds, this is the full unfolded version
     */
    private final ImmutableList<PlaceSet> normalisedNotationElements;

    /**
     * Construct a new notation using a simple list of sets of elements.
     * Use the NotationBuilder to construct.
     */
    DefaultPlaceSetSequence(final String name,
                            final NumberOfBells numberOfWorkingBells,
                            final List<PlaceSet> normalisedNotationElements) {
        this.name = checkNotNull(name, "name must not be null");
        checkNotNull(name.length() > 0, "name must contain some characters");
        this.numberOfWorkingBells = checkNotNull(numberOfWorkingBells, "numberOfWorkingBells must not be null");
        this.normalisedNotationElements = ImmutableList.<PlaceSet>builder().addAll(checkNotNull(normalisedNotationElements)).build();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public NumberOfBells getNumberOfWorkingBells() {
        return numberOfWorkingBells;
    }

    @Override
    public String getNameIncludingNumberOfBells() {
        return getName() + " " + getNumberOfWorkingBells().getName(); //TODO pre-calculate
    }

    @Override
    public int size() {
        return normalisedNotationElements.size();
    }

    @Override
    public PlaceSet get(final int index) throws ArrayIndexOutOfBoundsException {
        return normalisedNotationElements.get(index);
    }

    public List<PlaceSet> getNormalisedNotationElements() {
        return normalisedNotationElements;
    }

    @Override
    public Iterator<PlaceSet> iterator() {
        return normalisedNotationElements.iterator();
    }

}
