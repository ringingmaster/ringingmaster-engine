package org.ringingmaster.engine.notation;

import org.junit.Test;

import org.ringingmaster.engine.NumberOfBells;

import static org.junit.Assert.assertEquals;

public class DefaultNotatoionTest {

	@Test(expected=IndexOutOfBoundsException.class)
	public void accessingHigherRowCausesIndexOutOfBoundsException()  {
		final Notation notation = buildSymmetricNotation("1.5.34", "78");
		notation.get(6);
	}

	/**
	 * As convenience, we use the notation builder to do the building
	 */
	private Notation buildAsymNotation(final String notationShorthand) {
		final NotationBuilder notationBuilder = NotationBuilder.getInstance();
		final Notation notation = notationBuilder
				.setNumberOfWorkingBells(NumberOfBells.BELLS_8)
				.setUnfoldedNotationShorthand(notationShorthand)
				.build();
		return notation;
	}

	@Test
	public void buildConsiseNotationDisplayString() {
		final Notation notation = buildAsymNotation("-.12x14.16");
		assertEquals("-12-14.16", notation.getNotationDisplayString(true));
	}

	@Test
	public void buildCorrectRowDisplayString() {
		final Notation notation = buildAsymNotation("-7821");
		assertEquals("-", notation.get(0).toDisplayString());
		assertEquals("Build correct row display string", "1278", notation.get(1).toDisplayString());
	}

	@Test
	public void buildNonConsiseNotationDisplayString() {
		final Notation notation = buildAsymNotation("x.12-14.16");
		assertEquals("-.12.-.14.16", notation.getNotationDisplayString(false));
	}

	/**
	 * As convenience, we use the notation builder to do the building symmetric
	 */
	private Notation buildSymmetricNotation(final String notationShorthand, final String leadEndShorthand) {
		final NotationBuilder builder = NotationBuilder.getInstance();
		final Notation notation = builder
				.setNumberOfWorkingBells(NumberOfBells.BELLS_8)
				.setFoldedPalindromeNotationShorthand(notationShorthand, leadEndShorthand)
				.build();
		return notation;
	}

	@Test
	public void canIterateFoldedPalindromeNotation()  {
		final Notation notation = buildSymmetricNotation("12.56.34", "78");

		notation.getNotationDisplayString(true);

		int i=0;
		for (final PlaceSet row : notation) {
			if ((i == 0) || (i == 4)) {
				final PlaceSet expected = NotationRowHelper.buildNotationRow(Place.PLACE_1, Place.PLACE_2);
				assertEquals("folded notation of '12.56.34 le78' should have PLACE_1, PLACE_2 in " + i + "th index",expected, row);
			}
			if ((i == 1) || (i == 3)) {
				final PlaceSet expected = NotationRowHelper.buildNotationRow(Place.PLACE_5, Place.PLACE_6);
				assertEquals("folded notation of '12.56.34 le78' should have PLACE_5,PLACE_6 in " + i + "th index",expected, row);
			}
			if (i == 2) {
				final PlaceSet expected = NotationRowHelper.buildNotationRow(Place.PLACE_3, Place.PLACE_4);
				assertEquals("folded notation of '12.56.34 le78' should have PLACE_3 in " + i + "th index",expected, row);
			}
			if (i == 5) {
				final PlaceSet expected = NotationRowHelper.buildNotationRow(Place.PLACE_7, Place.PLACE_8);
				assertEquals("folded notation of '12.56.34 le78' should have PLACE_7,PLACE_8  in " + i + "th index",expected, row);
			}
			i++;
		}

		assertEquals(6, i);
	}

	@Test
	public void canIterateNotation()  {
		final Notation notation = buildAsymNotation("12.56");

		notation.getNotationDisplayString(true);

		int i=0;
		for (final PlaceSet row : notation) {
			if (i == 0 ) {
				final PlaceSet expected = NotationRowHelper.buildNotationRow(Place.PLACE_1, Place.PLACE_2);
				assertEquals("folded notation of '12.56.34 le78' should have PLACE_1, PLACE_2 in " + i + "th index",expected, row);
			}
			if (i == 1) {
				final PlaceSet expected = NotationRowHelper.buildNotationRow(Place.PLACE_5, Place.PLACE_6);
				assertEquals("folded notation of '12.56.34 le78' should have PLACE_5,PLACE_6 in " + i + "th index",expected, row);
			}
			i++;
		}

		assertEquals(2, i);
	}

	@Test
	public void leadEndStringFormattedCorrectly() {
		final Notation notation = buildSymmetricNotation("x.12.34", "56.78");
		assertEquals("-.12.34,56.78", notation.getNotationDisplayString(false));
	}

	@Test
	public void nameIsCorrect() {
		Notation notation = NotationBuilder.getInstance()
				.setNumberOfWorkingBells(NumberOfBells.BELLS_8)
				.setUnfoldedNotationShorthand("-")
				.setName("Test")
				.build();
		assertEquals("Test", notation.getName());
		assertEquals("Test Major", notation.getNameIncludingNumberOfBells());
	}

	@Test
	public void callNameIsCorrect() {
		Notation notation = NotationBuilder.getInstance()
				.setNumberOfWorkingBells(NumberOfBells.BELLS_8)
				.setUnfoldedNotationShorthand("-")
				.addCall("Test", "T", "12", true)
				.build();
		assertEquals("Test", notation.getCalls().iterator().next().getName());
	}

	@Test
	public void callNameShorthandIsCorrect() {
		Notation notation = NotationBuilder.getInstance()
				.setNumberOfWorkingBells(NumberOfBells.BELLS_8)
				.setUnfoldedNotationShorthand("-")
				.addCall("Test", "T", "12", true)
				.build();
		assertEquals("T", notation.getCalls().iterator().next().getNameShorthand());
	}

}
