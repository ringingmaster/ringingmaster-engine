package org.ringingmaster.engine.method.impl;

import org.junit.Assert;
import org.junit.Test;

import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.method.Bell;
import org.ringingmaster.engine.method.MethodRow;
import org.ringingmaster.engine.method.RowCourseType;
import org.ringingmaster.engine.method.Stroke;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DefaultMethodRowTest {

	@Test(expected=IndexOutOfBoundsException.class)
	public void accessingHigherBellCausesIndexOutOfBoundsException()  {
		MethodRow roundsOnEight = new MethodBuilder().buildRoundsRow(NumberOfBells.BELLS_8);
	
		roundsOnEight.getBellInPlace(8);
	}

	@Test
	public void accessingBoundaryBellDoesNotCauseException()  {
		MethodRow roundsOnEight = new MethodBuilder().buildRoundsRow(NumberOfBells.BELLS_8);
		Assert.assertEquals("should be able to access position 7", Bell.BELL_8, roundsOnEight.getBellInPlace(7));
	}

	@Test
	public void comparatorOfIdenticalRowsReturnsZero() {
		MethodRow row1 = buildRow(NumberOfBells.BELLS_3, Bell.BELL_1, Bell.BELL_2, Bell.BELL_3);
		MethodRow row2 = buildRow(NumberOfBells.BELLS_3, Bell.BELL_1, Bell.BELL_2, Bell.BELL_3);
		int compare = row1.compareTo(row2);
		assert(compare == 0);
	}

	@Test
	public void comparatorOfDifferentNumbersOfBellsReturnsBasedOnNumberOfBells() {
		MethodRow row1 = buildRow(NumberOfBells.BELLS_4, Bell.BELL_1, Bell.BELL_2, Bell.BELL_3, Bell.BELL_4);
		MethodRow row2 = buildRow(NumberOfBells.BELLS_3, Bell.BELL_1, Bell.BELL_2, Bell.BELL_3);
		int compare = row1.compareTo(row2);
		assert(compare > 0);
	}

	@Test
	public void comparatorSortsByFirstDifferingPlace() {
		MethodRow row1 = buildRow(NumberOfBells.BELLS_3, Bell.BELL_1, Bell.BELL_2, Bell.BELL_3);
		MethodRow row2= buildRow(NumberOfBells.BELLS_3, Bell.BELL_1, Bell.BELL_3, Bell.BELL_2);
		int compare = row1.compareTo(row2);
		assert(compare < 0);
	}

	@Test
	public void isRoundsReturnsTrueWhenRounds() {
		final MethodRow roundsOnSix = MethodBuilder.buildRoundsRow(NumberOfBells.BELLS_10);
		assertTrue(roundsOnSix.isRounds());
	}

	@Test
	public void isRoundsReturnsFalseForRoundsNearMiss() {
		final MethodRow roundsOnSix = buildRow(NumberOfBells.BELLS_4, Bell.BELL_1, Bell.BELL_2, Bell.BELL_4, Bell.BELL_3);
		assertFalse(roundsOnSix.isRounds());
	}

	private MethodRow buildRow(final NumberOfBells numberOfBells, final Bell... bells) {
		return new DefaultMethodRow(numberOfBells, bells, 0, Stroke.HANDSTROKE, RowCourseType.POSITIVE);
	}

}
