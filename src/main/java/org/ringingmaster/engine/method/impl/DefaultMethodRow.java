package org.ringingmaster.engine.method.impl;

import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.method.Bell;
import org.ringingmaster.engine.method.MethodRow;
import org.ringingmaster.engine.method.RowCourseType;
import org.ringingmaster.engine.method.Stroke;
import org.ringingmaster.engine.notation.NotationPlace;
import net.jcip.annotations.Immutable;

import java.util.Arrays;
import java.util.Iterator;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkPositionIndex;

/**
 * Default implementation of the MethodRow interface.
 * 
 * @author Stephen Lake
 */
@Immutable
public class DefaultMethodRow implements MethodRow {

	
	private final NumberOfBells numberOfBells;
	private final Bell[] bells;
	private final int rowNumber;
	private final Stroke stroke;
	private final RowCourseType rowCourseType;
	/**
	 * 
	 * @param numberOfBells not null
	 * @param bells not null
	 * @param rowNumber greater than or equal to 0
	 */
	DefaultMethodRow(final NumberOfBells numberOfBells, final Bell[] bells, final int rowNumber, final Stroke stroke, RowCourseType rowCourseType) {
		this.numberOfBells = checkNotNull(numberOfBells, "numberOfBells can't be null");
		this.bells = checkNotNull(bells, "bells can't be null"); //TODO should this be copied, or constructed using a List<>() ?
		checkArgument(numberOfBells.getBellCount() == bells.length, "bells array needs to have [%s] elemnts, but has [%s]", numberOfBells.getBellCount(), bells.length);
		checkArgument(rowNumber >= 0, "rowNumber must be greater than or equal to 0. [%s]", rowNumber);
		this.rowNumber = rowNumber;
		this.stroke = checkNotNull(stroke, "stroke can't be null");
		this.rowCourseType = checkNotNull(rowCourseType, "rowCourseType can't be null");
	}

	@Override
	public NumberOfBells getNumberOfBells() {
		return numberOfBells;
	}

	@Override
	public Bell getBellInPlace(final int place) {
		checkPositionIndex(place, bells.length);

		return bells[place];
	}

	@Override
	public Bell getBellInPlace(final NotationPlace place) {
		return getBellInPlace(place.getZeroBasedPlace());
	}

	@Override
	public Integer getPlaceOfBell(final Bell bellToFindPlaceFor) {
		checkNotNull(bellToFindPlaceFor, "bellToFindPlaceFor must not be null");

		int place = 0;
		for(final Bell bell : bells) {
			if (bellToFindPlaceFor == bell) {
				return place;
			}
			place++;
		}
		return null;
	}

	@Override
	public Iterator<Bell> iterator() {

		return new Iterator<Bell>() {

			private int place = 0;

			@Override
			public boolean hasNext() {
				return place < bells.length;
			}

			@Override
			public Bell next() {
				return getBellInPlace(place++);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException("DefaultMethodRow.iterator() does not support remove()");
			}
		};
	}

	@Override
	public String getDisplayString(boolean useRoundsWord) {
		if (useRoundsWord) {
			if (isRounds()) {
				return ROUNDS_TOKEN;
			}
		}

		final StringBuilder buff = new StringBuilder(bells.length);
		for (final Bell bell: bells) {
			buff.append(bell.getMnemonic());
		}
		return buff.toString();
	}

	@Override
	public int getRowNumber() {
		return rowNumber;
	}

	@Override
	public Stroke getStroke() {
		return stroke;
	}

	@Override
	public MethodRow setStroke(Stroke stroke) {
		return new DefaultMethodRow(numberOfBells, bells, rowNumber, stroke, rowCourseType);
	}


	public RowCourseType getRowCourseType() {
		return rowCourseType;
	}

	@Override
	public boolean isRounds() {
		for (int i=0;i<numberOfBells.getBellCount();i++) {
			if (bells[i].getZeroBasedBell() != i) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() { // TODO as immutable, can improve speed by caching
		final int prime = 31;
		int result = 1;
		result = (prime * result) + Arrays.hashCode(bells);
		result = (prime * result)
				+ ((numberOfBells == null) ? 0 : numberOfBells.hashCode());
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
		final DefaultMethodRow other = (DefaultMethodRow) obj;
		if (numberOfBells != other.numberOfBells) {
			return false;
		}
		if (!Arrays.equals(bells, other.bells)) {
			return false;
		}
		return true;
	}

	@Override
	public int compareTo(MethodRow other) {
		int result = getNumberOfBells().compareTo(other.getNumberOfBells());
		if (result != 0) return result;

		for (int i=0;i<other.getNumberOfBells().getBellCount();i++) {
			result = getBellInPlace(i).compareTo(other.getBellInPlace(i));
			if (result != 0) return result;
		}
		return 0;
	}

	@Override
	public String toString() {
		return "[" + getDisplayString(false) + "(" + numberOfBells.getBellCount() + ")," + getRowNumber() + "]";
	}
}
