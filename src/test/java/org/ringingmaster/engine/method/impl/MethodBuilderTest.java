package org.ringingmaster.engine.method.impl;

import org.ringingmaster.engine.method.Bell;
import org.ringingmaster.engine.method.RowCourseType;
import org.ringingmaster.engine.method.Stroke;
import org.junit.Test;

import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.method.MethodRow;

import static org.ringingmaster.engine.NumberOfBells.BELLS_10;
import static org.ringingmaster.engine.NumberOfBells.BELLS_12;
import static org.ringingmaster.engine.NumberOfBells.BELLS_6;
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
		String rowAsString = row.getDisplayString(false);
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
		assertEquals("12345678", transformedRow.getDisplayString(false));
	}

	@Test
	public void transformingNonRoundsRowWithHigherNumberOfBellsExtendsOriginal() {
		final MethodRow changeOnSix = buildRow(BELLS_6, Bell.BELL_6, Bell.BELL_4, Bell.BELL_2, Bell.BELL_1, Bell.BELL_3, Bell.BELL_5);
		final MethodRow transformedRow = MethodBuilder.transformToNewNumberOfBells(changeOnSix, NumberOfBells.BELLS_8);
		assertEquals("64213578", transformedRow.getDisplayString(false));
	}

	@Test
	public void transformingRoundsRowWithLowerNumberOfBellsReducesOriginal() {
		final MethodRow roundsOnSix = MethodBuilder.buildRoundsRow(BELLS_10);
		final MethodRow transformedRow = MethodBuilder.transformToNewNumberOfBells(roundsOnSix, NumberOfBells.BELLS_6);
		assertEquals("123456", transformedRow.getDisplayString(false));
	}

	@Test
	public void transformingNonRoundsRowWithLowerNumberOfBellsExtendsOriginal() {
		final MethodRow changeOnSix = buildRow(BELLS_6, Bell.BELL_6, Bell.BELL_4, Bell.BELL_2, Bell.BELL_1, Bell.BELL_3, Bell.BELL_5);
		final MethodRow transformedRow = MethodBuilder.transformToNewNumberOfBells(changeOnSix, NumberOfBells.BELLS_4);
		assertEquals("4213", transformedRow.getDisplayString(false));
	}

	@Test
	public void canParseRoundsRow() {
		final String parseString = "1234567890ET";
		final MethodRow parsedRow = MethodBuilder.parse(BELLS_12, parseString);
		assertEquals(parseString, parsedRow.getDisplayString(false));
	}

	@Test(expected = IllegalStateException.class)
	public void parsingRowWithBellMnemonicGreaterThanNumberOfBellsThrows() {
		final String parseString = "123457";
		MethodBuilder.parse(BELLS_6, parseString);
	}

	@Test(expected = IllegalStateException.class)
	public void parsingRowWithUnknownBellMnemonicThrows() {
		final String parseString = "12345Z";
		MethodBuilder.parse(BELLS_6, parseString);
	}

	@Test(expected = IllegalStateException.class)
	public void parsingRowWithDuplicateBellMnemonicThrows() {
		final String parseString = "123451";
		MethodBuilder.parse(BELLS_6, parseString);
	}

	@Test(expected = IllegalStateException.class)
	public void parsingWrongNumberOfBellsThrows() {
		final String parseString = "123456";
		final MethodRow parsedRow = MethodBuilder.parse(BELLS_12, parseString);
		assertEquals(parseString, parsedRow.getDisplayString(false));
	}

	private MethodRow buildRow(final NumberOfBells numberOfBells, final Bell... bells) {
		return new DefaultMethodRow(numberOfBells, bells, 0, Stroke.HANDSTROKE, RowCourseType.POSITIVE);
	}
}