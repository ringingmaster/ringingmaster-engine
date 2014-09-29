package com.concurrentperformance.ringingmaster.engine.method.impl;

import java.util.List;

import com.concurrentperformance.ringingmaster.engine.NumberOfBells;
import com.concurrentperformance.ringingmaster.engine.method.Bell;
import com.concurrentperformance.ringingmaster.engine.method.Method;
import com.concurrentperformance.ringingmaster.engine.method.MethodLead;
import com.concurrentperformance.ringingmaster.engine.method.MethodRow;
import com.concurrentperformance.ringingmaster.engine.method.RowCourseType;
import com.concurrentperformance.ringingmaster.engine.method.Stroke;
import com.concurrentperformance.ringingmaster.engine.notation.NotationRow;

import static com.google.common.base.Preconditions.checkNotNull;

public class MethodBuilder {

	public static Method buildMethod(final NumberOfBells numberOfBells, final List<MethodLead> leads)  {
		return new DefaultMethod(numberOfBells, leads);
	}

	public static MethodLead buildLead(NumberOfBells numberOfBells, List<MethodRow> rows, List<Integer> leadSeperatorPositions) {
		return new DefaultMethodLead(numberOfBells, rows, leadSeperatorPositions);
	}

	/**
	 * Build a rounds row for a given number of bells.
	 * @param numberOfBells not null
	 * @return
	 */
	public static MethodRow buildRoundsRow(final NumberOfBells numberOfBells) {
		checkNotNull(numberOfBells, "numberOfBells cant be null");

		final Bell[] bells = new Bell[numberOfBells.getBellCount()];
		for (int i=0;i<numberOfBells.getBellCount();i++) {
			final Bell bell = Bell.valueOf(i);
			bells[i] = bell;
		}
		final RowCourseType courseType = RowCourseType.calculateRowCourseType(bells);

		final MethodRow row = new DefaultMethodRow(numberOfBells, bells, 0, Stroke.HANDSTROKE, courseType);
		return row;
	}

	/**
	 * Build a new MethodRow where each pair of bells swaps. This is
	 * only applicable to even numbers of bells.
	 * //TODO when happens when we have an even number of bells with a cover?
	 *
	 * @param previousRow The previous row to construct the new row from.
	 * @return MethodRow not null
	 */
	public static MethodRow buildAllChangeRow(final MethodRow previousRow) {
		checkNotNull(previousRow, "previousRow cant be null");

		final Bell[] bells = new Bell[previousRow.getNumberOfBells().getBellCount()];
		for (int i=0;i<previousRow.getNumberOfBells().getBellCount();i+=2) {
			bells[i]   = previousRow.getBellInPlace(i+1);
			bells[i+1] = previousRow.getBellInPlace(i);
		}
		final int rowNumber = previousRow.getRowNumber();
		final Stroke stroke = Stroke.flipStroke(previousRow.getStroke());
		final RowCourseType courseType = RowCourseType.calculateRowCourseType(bells);

		final MethodRow row = new DefaultMethodRow(previousRow.getNumberOfBells(), bells, rowNumber + 1, stroke, courseType);
		return row;
	}

	/**
	 * Build a new MthdoRow where places are made. if a place is not made, then
	 * swap the bells.
	 * i.e.
	 * 12345678 if places 1 & 4 are made, then it becomes
	 * 13246587
	 *
	 * @param previousRow not null
	 * @param notationRow not null
	 * @return
	 */
	public static MethodRow buildRowWithPlaces(final MethodRow previousRow, final NotationRow notationRow) {
		checkNotNull(previousRow, "notationRow cant be null");
		checkNotNull(notationRow, "notationRow cant be null");

		final Bell[] bells = new Bell[previousRow.getNumberOfBells().getBellCount()];
		for (int place=0;place<previousRow.getNumberOfBells().getBellCount();place++) {
			if (notationRow.makesPlace(place)) {
				//Make the place
				bells[place] = previousRow.getBellInPlace(place);
			}
			else {
				// swap 2 bells
				bells[place]   = previousRow.getBellInPlace(place+1);
				bells[place+1] = previousRow.getBellInPlace(place);
				place++;
			}
		}
		final int rowNumber = previousRow.getRowNumber();
		final Stroke stroke = Stroke.flipStroke(previousRow.getStroke());
		final RowCourseType courseType = RowCourseType.calculateRowCourseType(bells);

		final MethodRow row = new DefaultMethodRow(previousRow.getNumberOfBells(), bells, rowNumber + 1, stroke, courseType);
		return row;
	}
}