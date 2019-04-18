package org.ringingmaster.engine.method;

import org.junit.Assert;
import org.junit.Test;
import org.ringingmaster.engine.NumberOfBells;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DefaultRowTest {

	@Test(expected=IndexOutOfBoundsException.class)
	public void accessingHigherBellCausesIndexOutOfBoundsException()  {
		Row roundsOnEight = new MethodBuilder().buildRoundsRow(NumberOfBells.BELLS_8);
	
		roundsOnEight.getBellInPlace(8);
	}

	@Test
	public void accessingBoundaryBellDoesNotCauseException()  {
		Row roundsOnEight = new MethodBuilder().buildRoundsRow(NumberOfBells.BELLS_8);
		Assert.assertEquals("should be able to access position 7", Bell.BELL_8, roundsOnEight.getBellInPlace(7));
	}

	@Test
	public void comparatorOfIdenticalRowsReturnsZero() {
		Row row1 = buildRow(NumberOfBells.BELLS_3, Bell.BELL_1, Bell.BELL_2, Bell.BELL_3);
		Row row2 = buildRow(NumberOfBells.BELLS_3, Bell.BELL_1, Bell.BELL_2, Bell.BELL_3);
		int compare = row1.compareTo(row2);
		assert(compare == 0);
	}

	@Test
	public void comparatorOfDifferentNumbersOfBellsReturnsBasedOnNumberOfBells() {
		Row row1 = buildRow(NumberOfBells.BELLS_4, Bell.BELL_1, Bell.BELL_2, Bell.BELL_3, Bell.BELL_4);
		Row row2 = buildRow(NumberOfBells.BELLS_3, Bell.BELL_1, Bell.BELL_2, Bell.BELL_3);
		int compare = row1.compareTo(row2);
		assert(compare > 0);
	}

	@Test
	public void comparatorSortsByFirstDifferingPlace() {
		Row row1 = buildRow(NumberOfBells.BELLS_3, Bell.BELL_1, Bell.BELL_2, Bell.BELL_3);
		Row row2= buildRow(NumberOfBells.BELLS_3, Bell.BELL_1, Bell.BELL_3, Bell.BELL_2);
		int compare = row1.compareTo(row2);
		assert(compare < 0);
	}

	@Test
	public void isRoundsReturnsTrueWhenRounds() {
		final Row roundsOnSix = MethodBuilder.buildRoundsRow(NumberOfBells.BELLS_10);
		assertTrue(roundsOnSix.isRounds());
	}

	@Test
	public void isRoundsReturnsFalseForRoundsNearMiss() {
		final Row roundsOnSix = buildRow(NumberOfBells.BELLS_4, Bell.BELL_1, Bell.BELL_2, Bell.BELL_4, Bell.BELL_3);
		assertFalse(roundsOnSix.isRounds());
	}

	private Row buildRow(final NumberOfBells numberOfBells, final Bell... bells) {
		return new DefaultRow(numberOfBells, bells, 0, Stroke.HANDSTROKE, RowCourseType.POSITIVE);
	}

}
