package org.ringingmaster.engine.method;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.notation.Place;
import org.ringingmaster.util.conversion.ConvertionUtil;

import javax.annotation.concurrent.Immutable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkElementIndex;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Default implementation of the Lead interface.
 *
 * @author Steve Lake
 */
@Immutable
class DefaultLead implements Lead {

    private final NumberOfBells numberOfBells;
    private final int partIndex; //TODO should we have a Part class in Method?
    private final Row[] rows;
    private final int[] leadSeparatorPositions;

    //derived
    private final ImmutableMap<Bell, List<Integer>> placeSequences;

    /**
     * Default constructor.
     *
     * @param numberOfBells
     * @param rows
     * @param leadSeparatorPositions
     */
    DefaultLead(final NumberOfBells numberOfBells, final int partIndex, final List<Row> rows, final Collection<Integer> leadSeparatorPositions) {
        this.numberOfBells = checkNotNull(numberOfBells, "numberOfBells must not be null");
        checkArgument(partIndex >= 0);
        this.partIndex = partIndex;
        checkNotNull(rows, "rows must not be null");
        checkArgument((rows.size() > 0), "rows length must be greater than zero");
        this.rows = rows.toArray(new Row[rows.size()]);
        placeSequences = calculatePlaceSequences(this.rows);
        checkNotNull(leadSeparatorPositions, "leadSeparatorPositions must not be null");
        this.leadSeparatorPositions = ConvertionUtil.integerCollectionToArray(leadSeparatorPositions);
    }

    /**
     * Take each of the passed in rows, and calculate a sequence of places
     * for the path of each bell.
     *
     * @param rows
     * @return
     */
    private ImmutableMap<Bell, List<Integer>> calculatePlaceSequences(final Row[] rows) {
        checkNotNull(rows);
        checkElementIndex(0, rows.length);

        final ImmutableMap.Builder<Bell, List<Integer>> placeSequencesBuilder = ImmutableMap.builder();

        final Row firstRow = rows[0];
        for (final Bell bell : firstRow) {
            final ImmutableList.Builder<Integer> sequencesBuilder = ImmutableList.builder();

            for (final Row row : rows) {
                final int place = row.getPlaceOfBell(bell);
                sequencesBuilder.add(place);
            }
            placeSequencesBuilder.put(bell, sequencesBuilder.build());
        }

        return placeSequencesBuilder.build();
    }

    @Override
    public NumberOfBells getNumberOfBells() {
        return numberOfBells;
    }

    @Override
    public int getRowCount() {
        return rows.length;
    }

    @Override
    public Row getRow(final int index) {
        checkElementIndex(index, rows.length);

        return rows[index];
    }

    @Override
    public Row getLastRow() {
        // constructor throws exception if null or 0 length
        return rows[rows.length - 1];
    }

    @Override
    public Row getFirstRow() {
        // constructor throws exception if null or 0 length
        return rows[0];
    }

    @Override
    public Iterator<Row> iterator() {
        return new Iterator<>() {

            int rowIndex = 0;

            @Override
            public boolean hasNext() {
                return rowIndex < rows.length;
            }

            @Override
            public Row next() {
                return getRow(rowIndex++);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("DefaultRow.iterator() does not support remove()");
            }
        };
    }

    @Override
    public List<Integer> getPlaceSequenceForBell(final Bell bell) {
        checkNotNull(bell);

        final List<Integer> sequences = placeSequences.get(bell);
        return sequences;
    }

    @Override
    public int[] getLeadSeparatorPositions() {
        return leadSeparatorPositions;
    }

    @Override
    public Place getStartPlace(final Bell bell) {
        checkNotNull(bell);

        Place startPlace = null;
        //find the first row
        if (rows.length > 0) {
            final Row firstRow = rows[0];
            final int place = firstRow.getPlaceOfBell(bell);
            startPlace = Place.valueOf(place);
        }
        return startPlace;
    }

    @Override
    public Set<Place> getHuntBellStartPlace() {

        Set<Place> huntBellPlaces = new HashSet<>();

        Row firstRow = getFirstRow();
        Row lastRow = getLastRow();

        for (Place place : numberOfBells) {
            if (firstRow.getBellInPlace(place) == lastRow.getBellInPlace(place)) {
                huntBellPlaces.add(place);
            }
        }

        return huntBellPlaces;
    }

    @Override
    public String getAllChangesAsText() {
        StringBuilder buff = new StringBuilder();
        for (Row row : rows) {
            buff.append(row.getDisplayString(false)).append(System.lineSeparator());
        }
        return buff.toString();
    }

    @Override
    public int getPartIndex() {
        return partIndex;
    }

    @Override
    public String toString() {
        final StringBuilder buff = new StringBuilder();

        buff.append("DefaultLead [\r\n");

        for (final Row row : rows) {
            buff.append(row.getDisplayString(false));
            buff.append("\r\n");
        }
        buff.append("]");
        return buff.toString();
    }

}