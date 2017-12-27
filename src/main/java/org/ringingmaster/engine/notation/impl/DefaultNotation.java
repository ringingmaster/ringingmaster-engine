package org.ringingmaster.engine.notation.impl;

import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.notation.Notation;
import org.ringingmaster.engine.notation.NotationRow;
import com.google.common.collect.ImmutableList;
import javax.annotation.concurrent.Immutable;

import java.util.Iterator;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO
 * User: Stephen
 */
@Immutable
public abstract class DefaultNotation implements Notation {

	private final NumberOfBells numberOfWorkingBells;
	private final String name;

	/** A List of NotationElements that has been unfolded, and had the le applied if applicable */
	private final ImmutableList<NotationRow> normalisedNotationElements;

	/**
	 * Construct a new notation using a simple list of sets of elements.
	 * Use the NotationBuilder to construct.
	 */
	DefaultNotation(final String name,
	                final NumberOfBells numberOfWorkingBells,
	                final List<NotationRow> normalisedNotationElements) {
		this.name = checkNotNull(name, "name must not be null");
		checkNotNull(name.length() > 0 , "name must contain some characters");
		this.numberOfWorkingBells = checkNotNull(numberOfWorkingBells, "numberOfWorkingBells must not be null");
		this.normalisedNotationElements = ImmutableList.<NotationRow>builder().addAll(checkNotNull(normalisedNotationElements)).build();
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
	public int getRowCount() {
		return normalisedNotationElements.size();
	}

	@Override
	public NotationRow getRow(final int index) throws ArrayIndexOutOfBoundsException {
		return normalisedNotationElements.get(index);
	}

	public List<NotationRow> getNormalisedNotationElements() {
		return normalisedNotationElements;
	}

	@Override
	public Iterator<NotationRow> iterator() {
		return new Iterator<NotationRow>() {

			private int index = 0;

			@Override
			public boolean hasNext() {
				return index < getRowCount();
			}

			@Override
			public NotationRow next() {
				return getRow(index++);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException("DefaultNotationBody.iterator() does not support remove()");
			}
		};
	}

}
