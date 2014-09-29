package com.concurrentperformance.ringingmaster.engine.method.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.jcip.annotations.Immutable;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.concurrentperformance.ringingmaster.engine.NumberOfBells;
import com.concurrentperformance.ringingmaster.engine.method.Bell;
import com.concurrentperformance.ringingmaster.engine.method.MethodLead;
import com.concurrentperformance.ringingmaster.engine.method.MethodRow;
import com.concurrentperformance.ringingmaster.util.ConvertionUtil;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkElementIndex;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Default implementation of the MethodLead interface.
 * 
 * @author Stephen Lake
 */
@Immutable
public class DefaultMethodLead implements MethodLead {

	private final NumberOfBells numberOfBells;
	private final MethodRow[] rows;
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
	DefaultMethodLead(final NumberOfBells numberOfBells, final List<MethodRow> rows, final Collection<Integer> leadSeparatorPositions)  {
		this.numberOfBells = checkNotNull(numberOfBells, "numberOfBells must not be null");
		checkNotNull(rows, "rows must not be null");
		checkArgument((rows.size() > 0), "rows length must be greater than zero");
		this.rows = rows.toArray(new MethodRow[rows.size()]);
		placeSequences = calculatePlaceSequences(this.rows);
		checkNotNull(leadSeparatorPositions, "leadSeparatorPositions must not be null");
		this.leadSeparatorPositions = ConvertionUtil.integerCollectionToArray(leadSeparatorPositions);
	}

	/**
	 * Take each of the passed in rows, and calculate a sequence of places
	 * for the path of each bell.
	 * @param rows
	 * @return
	 */
	private ImmutableMap<Bell, List<Integer>> calculatePlaceSequences(final MethodRow[] rows) {
		checkNotNull(rows);
		checkElementIndex(0, rows.length);

		final ImmutableMap.Builder<Bell,List<Integer>> placeSequencesBuilder = ImmutableMap.<Bell, List<Integer>>builder();

		final MethodRow firstRow = rows[0];
		for (final Bell bell : firstRow) {
			final ImmutableList.Builder<Integer> sequencesBuilder = ImmutableList.<Integer>builder();

			for (final MethodRow row : rows) {
				final Integer place = row.getPlaceOfBell(bell);
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
	public MethodRow getRow(final int index) {
		checkElementIndex(index, rows.length);

		return rows[index];
	}

	@Override
	public MethodRow getLastRow() {
		// constructor throws exception if null or 0 length
		return rows[rows.length - 1];
	}

	@Override
	public Iterator<MethodRow> iterator() {
		return new Iterator<MethodRow>() {

			int rowIndex = 0;

			@Override
			public boolean hasNext() {
				return rowIndex < rows.length;
			}

			@Override
			public MethodRow next() {
				return getRow(rowIndex++);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException("DefaultMethodRow.iterator() does not support remove()");
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
	public Bell getStartNumber(final Bell bell) {
		checkNotNull(bell);

		Bell startBell = null;
		//find the first row
		if (rows.length > 0) {
			final MethodRow firstRow = rows[0];
			final Integer place = firstRow.getPlaceOfBell(bell);
			startBell = Bell.valueOf(place);
		}
		return startBell;
	}

	@Override
	public String getAllChangesAsText() {
		StringBuilder buff = new StringBuilder();
		for (MethodRow row : rows) {
			buff.append(row.getDisplayString()).append(System.lineSeparator());
		}
		return buff.toString();
	}

	@Override
	public String toString() {
		final StringBuilder buff = new StringBuilder();

		buff.append("DefaultMethodLead [\r\n");

		for (final MethodRow row : rows) {
			buff.append(row.getDisplayString());
			buff.append("\r\n");
		}
		buff.append("]");
		return buff.toString();
	}

}