package org.ringingmaster.engine.method;

import org.junit.Test;
import org.ringingmaster.engine.NumberOfBells;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.ringingmaster.engine.NumberOfBells.BELLS_10;
import static org.ringingmaster.engine.NumberOfBells.BELLS_12;
import static org.ringingmaster.engine.NumberOfBells.BELLS_6;

public class MethodBuilderTest {

	@Test
	public void canBuildRoundsRow() {
		Row roundsOnTwelve = MethodBuilder.buildRoundsRow(NumberOfBells.BELLS_12);
		assertNotNull("build rounds should allways return a valid object", roundsOnTwelve);
		checkRow("1234567890ET", roundsOnTwelve);
	}

	private void checkRow(String sequence, Row row) {
		String rowAsString = row.getDisplayString(false);
        assertEquals("row should equal sequence", sequence, rowAsString);
	}

	@Test
	public void transformingMethodRowWithSameNumberOfBellsReturnsOriginalEquivalent() {
		final Row roundsOnSix = MethodBuilder.buildRoundsRow(BELLS_6);
		final Row transformedRow = MethodBuilder.transformToNewNumberOfBells(roundsOnSix, NumberOfBells.BELLS_6);
		assertEquals(roundsOnSix, transformedRow);
	}

	@Test
	public void transformingRoundsRowWithHigherNumberOfBellsExtendsOriginal() {
		final Row roundsOnSix = MethodBuilder.buildRoundsRow(BELLS_6);
		final Row transformedRow = MethodBuilder.transformToNewNumberOfBells(roundsOnSix, NumberOfBells.BELLS_8);
		assertEquals("12345678", transformedRow.getDisplayString(false));
	}

	@Test
	public void transformingNonRoundsRowWithHigherNumberOfBellsExtendsOriginal() {
		final Row changeOnSix = buildRow(BELLS_6, Bell.BELL_6, Bell.BELL_4, Bell.BELL_2, Bell.BELL_1, Bell.BELL_3, Bell.BELL_5);
		final Row transformedRow = MethodBuilder.transformToNewNumberOfBells(changeOnSix, NumberOfBells.BELLS_8);
		assertEquals("64213578", transformedRow.getDisplayString(false));
	}

	@Test
	public void transformingRoundsRowWithLowerNumberOfBellsReducesOriginal() {
		final Row roundsOnSix = MethodBuilder.buildRoundsRow(BELLS_10);
		final Row transformedRow = MethodBuilder.transformToNewNumberOfBells(roundsOnSix, NumberOfBells.BELLS_6);
		assertEquals("123456", transformedRow.getDisplayString(false));
	}

	@Test
	public void transformingNonRoundsRowWithLowerNumberOfBellsExtendsOriginal() {
		final Row changeOnSix = buildRow(BELLS_6, Bell.BELL_6, Bell.BELL_4, Bell.BELL_2, Bell.BELL_1, Bell.BELL_3, Bell.BELL_5);
		final Row transformedRow = MethodBuilder.transformToNewNumberOfBells(changeOnSix, NumberOfBells.BELLS_4);
		assertEquals("4213", transformedRow.getDisplayString(false));
	}

	@Test
	public void canParseRoundsRow() {
		final String parseString = "1234567890ET";
		final Row parsedRow = MethodBuilder.parse(BELLS_12, parseString);
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
		final Row parsedRow = MethodBuilder.parse(BELLS_12, parseString);
		assertEquals(parseString, parsedRow.getDisplayString(false));
	}

	private Row buildRow(final NumberOfBells numberOfBells, final Bell... bells) {
		return new DefaultRow(numberOfBells, bells, 0, Stroke.HANDSTROKE, RowCourseType.POSITIVE);
	}
}