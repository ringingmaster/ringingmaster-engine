package com.concurrentperformance.ringingmaster.engine.method.impl;

import com.concurrentperformance.ringingmaster.engine.method.Bell;
import com.concurrentperformance.ringingmaster.engine.method.RowCourseType;
import com.concurrentperformance.ringingmaster.engine.method.Stroke;
import org.junit.Test;

import com.concurrentperformance.ringingmaster.engine.NumberOfBells;
import com.concurrentperformance.ringingmaster.engine.method.MethodRow;

import static com.concurrentperformance.ringingmaster.engine.NumberOfBells.BELLS_10;
import static com.concurrentperformance.ringingmaster.engine.NumberOfBells.BELLS_6;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MethodBuilderTest {

	@Test
	public void canBuildRoundsRow() {
		MethodRow roundsOnTwelve = MethodBuilder.buildRoundsRow(NumberOfBells.BELLS_12);
		assertNotNull("build rounds should allways return a valid object", roundsOnTwelve);
		checkRow("1234567890ET", roundsOnTwelve);
	}

	private void checkRow(String sequence, MethodRow row) {
		String rowAsString = row.getDisplayString();
        assertEquals("row should equal sequence", sequence, rowAsString);
	}

	@Test
	public void transformingMethodRowWithSameNumberOfBellsReturnsOriginalEquivalent() {
		final MethodRow roundsOnSix = MethodBuilder.buildRoundsRow(BELLS_6);
		final MethodRow transformedRow = MethodBuilder.transformToNewNumberOfBells(roundsOnSix, NumberOfBells.BELLS_6);
		assertEquals(roundsOnSix, transformedRow);
	}

	@Test
	public void transformingRoundsRowWithHigherNumberOfBellsExtendsOriginal() {
		final MethodRow roundsOnSix = MethodBuilder.buildRoundsRow(BELLS_6);
		final MethodRow transformedRow = MethodBuilder.transformToNewNumberOfBells(roundsOnSix, NumberOfBells.BELLS_8);
		assertEquals("12345678", transformedRow.getDisplayString());
	}

	@Test
	public void transformingNonRoundsRowWithHigherNumberOfBellsExtendsOriginal() {
		final MethodRow changeOnSix = buildRow(BELLS_6, Bell.BELL_6, Bell.BELL_4, Bell.BELL_2, Bell.BELL_1, Bell.BELL_3, Bell.BELL_5);
		final MethodRow transformedRow = MethodBuilder.transformToNewNumberOfBells(changeOnSix, NumberOfBells.BELLS_8);
		assertEquals("64213578", transformedRow.getDisplayString());
	}

	@Test
	public void transformingRoundsRowWithLowerNumberOfBellsReducesOriginal() {
		final MethodRow roundsOnSix = MethodBuilder.buildRoundsRow(BELLS_10);
		final MethodRow transformedRow = MethodBuilder.transformToNewNumberOfBells(roundsOnSix, NumberOfBells.BELLS_6);
		assertEquals("123456", transformedRow.getDisplayString());
	}

	@Test
	public void transformingNonRoundsRowWithLowerNumberOfBellsExtendsOriginal() {
		final MethodRow changeOnSix = buildRow(BELLS_6, Bell.BELL_6, Bell.BELL_4, Bell.BELL_2, Bell.BELL_1, Bell.BELL_3, Bell.BELL_5);
		final MethodRow transformedRow = MethodBuilder.transformToNewNumberOfBells(changeOnSix, NumberOfBells.BELLS_4);
		assertEquals("4213", transformedRow.getDisplayString());
	}

	private MethodRow buildRow(final NumberOfBells numberOfBells, final Bell... bells) {
		return new DefaultMethodRow(numberOfBells, bells, 0, Stroke.HANDSTROKE, RowCourseType.POSITIVE);
	}
}