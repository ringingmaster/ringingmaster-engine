package org.ringingmaster.engine.method.impl;

import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.method.Bell;
import org.ringingmaster.engine.method.Method;
import org.ringingmaster.engine.method.MethodLead;
import org.ringingmaster.engine.method.MethodRow;
import org.ringingmaster.engine.method.RowCourseType;
import org.ringingmaster.engine.method.Stroke;
import org.ringingmaster.engine.notation.NotationPlace;
import org.ringingmaster.engine.notation.NotationRow;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

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
		checkNotNull(numberOfBells, "numberOfBells can't be null");

		final Bell[] bells = new Bell[numberOfBells.toInt()];
		for (int i = 0; i<numberOfBells.toInt(); i++) {
			final Bell bell = Bell.valueOf(i);
			bells[i] = bell;
		}
		final RowCourseType courseType = RowCourseType.calculateRowCourseType(bells);

		final MethodRow row = new DefaultMethodRow(numberOfBells, bells, 0, Stroke.HANDSTROKE, courseType);
		return row;
	}

	public static MethodRow parse(final NumberOfBells numberOfBells, final String parseString) {
		return  parse(numberOfBells, parseString, 0, Stroke.HANDSTROKE);
	}

	public static MethodRow parse(final NumberOfBells numberOfBells, final String parseString, int rowNumber, Stroke stroke) {
		checkNotNull(numberOfBells, "numberOfBells can't be null");
		checkNotNull(parseString, "parse string can't be null");
		checkState(numberOfBells.toInt() == parseString.length(), "You must enter a [%s] character sequence", numberOfBells.toInt());

		final Bell[] bells = new Bell[numberOfBells.toInt()];
		final Set<Bell> duplicateCheck = new HashSet<>();

		for (int i = 0; i<numberOfBells.toInt(); i++) {
			final String character = String.valueOf(parseString.charAt(i));
			Bell bell = Bell.valueOfMnemonic(character);
			checkState(bell != null, "Character [%s] is invalid.", character);
			checkState(bell.getZeroBasedBell() < numberOfBells.toInt(), "Character [%s] is invalid for [%s] bell row.", character, numberOfBells.toInt());
			checkState(duplicateCheck.add(bell), "Character [%s] appears more than once.", character);
			bells[i] = bell;
		}

		return new DefaultMethodRow(numberOfBells, bells, rowNumber, stroke, RowCourseType.calculateRowCourseType(bells));
	}


	public static MethodRow transformToNewNumberOfBells(MethodRow original, NumberOfBells newNumberOfBells) {
		checkNotNull(original);
		checkNotNull(newNumberOfBells);

		if (original.getNumberOfBells().toInt() < newNumberOfBells.toInt()) {
			Bell[] bells = new Bell[newNumberOfBells.toInt()];
			for (int i = 0; i < original.getNumberOfBells().toInt(); i++) {
				bells[i] = original.getBellInPlace(i);
			}
			;
			for (int i = original.getNumberOfBells().toInt(); i < newNumberOfBells.toInt(); i++) {
				bells[i] = Bell.valueOf(i);
			}
			return new DefaultMethodRow(newNumberOfBells, bells, original.getRowNumber(), original.getStroke(), original.getRowCourseType());
		}
		else if (original.getNumberOfBells().toInt() > newNumberOfBells.toInt()) {
			Bell[] bells = new Bell[newNumberOfBells.toInt()];
			int bellsPlaceIndex = 0;
			for (int i = 0; i < original.getNumberOfBells().toInt(); i++) {
				final Bell bellInPlace = original.getBellInPlace(i);
				if (bellInPlace.getZeroBasedBell() < newNumberOfBells.toInt()) {
					bells[bellsPlaceIndex] = original.getBellInPlace(i);
					bellsPlaceIndex++;
				}
			}
			;
			return new DefaultMethodRow(newNumberOfBells, bells, original.getRowNumber(), original.getStroke(), original.getRowCourseType());
		}
		else {
			return original;
		}
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

		final Bell[] bells = new Bell[previousRow.getNumberOfBells().toInt()];
		for (int i = 0; i<previousRow.getNumberOfBells().toInt(); i+=2) {
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

		final Bell[] bells = new Bell[previousRow.getNumberOfBells().toInt()];
		for (int zeroBasedPlace = 0; zeroBasedPlace<previousRow.getNumberOfBells().toInt(); zeroBasedPlace++) {
			NotationPlace notationPlace = NotationPlace.valueOf(zeroBasedPlace);
			if (notationRow.makesPlace(notationPlace)) {
				//Make the place
				bells[zeroBasedPlace] = previousRow.getBellInPlace(zeroBasedPlace);
			}
			else {
				// swap 2 bells
				bells[zeroBasedPlace]   = previousRow.getBellInPlace(zeroBasedPlace+1);
				bells[zeroBasedPlace+1] = previousRow.getBellInPlace(zeroBasedPlace);
				zeroBasedPlace++;
			}
		}
		final int rowNumber = previousRow.getRowNumber();
		final Stroke stroke = Stroke.flipStroke(previousRow.getStroke());
		final RowCourseType courseType = RowCourseType.calculateRowCourseType(bells);

		final MethodRow row = new DefaultMethodRow(previousRow.getNumberOfBells(), bells, rowNumber + 1, stroke, courseType);
		return row;
	}
}