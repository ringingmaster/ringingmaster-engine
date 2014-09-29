package com.concurrentperformance.ringingmaster.engine.notation.impl;

import org.junit.Test;

import com.concurrentperformance.ringingmaster.engine.NumberOfBells;
import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import com.concurrentperformance.ringingmaster.engine.notation.NotationPlace;
import com.concurrentperformance.ringingmaster.engine.notation.NotationRow;

import static org.junit.Assert.assertEquals;

public class DefaultNotatoionTest {

	@Test(expected=IndexOutOfBoundsException.class)
	public void accessingHigherRowCausesIndexOutOfBoundsException()  {
		final NotationBody notationBody = buildSymmetricNotation("1.5.34", "78");
		notationBody.getRow(6);
	}

	/**
	 * As convenience, we use the notation builder to do the building
	 */
	private NotationBody buildAsymNotation(final String notationShorthand) {
		final NotationBuilder notationBuilder = NotationBuilder.getInstance();
		final NotationBody notationBody = notationBuilder
				.setNumberOfWorkingBells(NumberOfBells.BELLS_8)
				.setUnfoldedNotationShorthand(notationShorthand)
				.build();
		return notationBody;
	}

	@Test
	public void buildConsiseNotationDisplayString() {
		final NotationBody notationBody = buildAsymNotation("-.12x14.16");
		assertEquals("-12-14.16", notationBody.getNotationDisplayString(true));
	}

	@Test
	public void buildCorrectRowDisplayString() {
		final NotationBody notationBody = buildAsymNotation("-7821");
		assertEquals("-", notationBody.getRow(0).toDisplayString());
		assertEquals("Build correct row display string", "1278", notationBody.getRow(1).toDisplayString());
	}

	@Test
	public void buildNonConsiseNotationDisplayString() {
		final NotationBody notationBody = buildAsymNotation("x.12-14.16");
		assertEquals("-.12.-.14.16", notationBody.getNotationDisplayString(false));
	}

	/**
	 * As convenience, we use the notation builder to do the building symmetric
	 */
	private NotationBody buildSymmetricNotation(final String notationShorthand, final String leadEndShorthand) {
		final NotationBuilder builder = NotationBuilder.getInstance();
		final NotationBody notationBody = builder
				.setNumberOfWorkingBells(NumberOfBells.BELLS_8)
				.setFoldedPalindromeNotationShorthand(notationShorthand, leadEndShorthand)
				.build();
		return notationBody;
	}

	@Test
	public void canIterateFoldedPalindromeNotation()  {
		final NotationBody notationBody = buildSymmetricNotation("12.56.34", "78");

		notationBody.getNotationDisplayString(true);

		int i=0;
		for (final NotationRow row : notationBody) {
			if ((i == 0) || (i == 4)) {
				final NotationRow expected = NotationRowHelper.buildNotationRow(NotationPlace.PLACE_1, NotationPlace.PLACE_2);
				assertEquals("folded notationBody of '12.56.34 le78' should have PLACE_1, PLACE_2 in " + i + "th index",expected, row);
			}
			if ((i == 1) || (i == 3)) {
				final NotationRow expected = NotationRowHelper.buildNotationRow(NotationPlace.PLACE_5, NotationPlace.PLACE_6);
				assertEquals("folded notationBody of '12.56.34 le78' should have PLACE_5,PLACE_6 in " + i + "th index",expected, row);
			}
			if (i == 2) {
				final NotationRow expected = NotationRowHelper.buildNotationRow(NotationPlace.PLACE_3, NotationPlace.PLACE_4);
				assertEquals("folded notationBody of '12.56.34 le78' should have PLACE_3 in " + i + "th index",expected, row);
			}
			if (i == 5) {
				final NotationRow expected = NotationRowHelper.buildNotationRow(NotationPlace.PLACE_7, NotationPlace.PLACE_8);
				assertEquals("folded notationBody of '12.56.34 le78' should have PLACE_7,PLACE_8  in " + i + "th index",expected, row);
			}
			i++;
		}

		assertEquals(6, i);
	}

	@Test
	public void canIterateNotation()  {
		final NotationBody notationBody = buildAsymNotation("12.56");

		notationBody.getNotationDisplayString(true);

		int i=0;
		for (final NotationRow row : notationBody) {
			if (i == 0 ) {
				final NotationRow expected = NotationRowHelper.buildNotationRow(NotationPlace.PLACE_1, NotationPlace.PLACE_2);
				assertEquals("folded notationBody of '12.56.34 le78' should have PLACE_1, PLACE_2 in " + i + "th index",expected, row);
			}
			if (i == 1) {
				final NotationRow expected = NotationRowHelper.buildNotationRow(NotationPlace.PLACE_5, NotationPlace.PLACE_6);
				assertEquals("folded notationBody of '12.56.34 le78' should have PLACE_5,PLACE_6 in " + i + "th index",expected, row);
			}
			i++;
		}

		assertEquals(2, i);
	}

	@Test
	public void leadEndStringFormattedCorrectly() {
		final NotationBody notationBody = buildSymmetricNotation("x.12.34", "56.78");
		assertEquals("-.12.34,56.78", notationBody.getNotationDisplayString(false));
	}

	@Test
	public void nameIsCorrect() {
		NotationBody notation = NotationBuilder.getInstance()
				.setNumberOfWorkingBells(NumberOfBells.BELLS_8)
				.setUnfoldedNotationShorthand("-")
				.setName("Test")
				.build();
		assertEquals("Test", notation.getName());
		assertEquals("Test Major", notation.getNameIncludingNumberOfBells());
	}

	@Test
	public void callNameIsCorrect() {
		NotationBody notation = NotationBuilder.getInstance()
				.setNumberOfWorkingBells(NumberOfBells.BELLS_8)
				.setUnfoldedNotationShorthand("-")
				.addCall("Test", "T", "12", true)
				.build();
		assertEquals("Test", notation.getCalls().iterator().next().getName());
	}

	@Test
	public void callNameShorthandIsCorrect() {
		NotationBody notation = NotationBuilder.getInstance()
				.setNumberOfWorkingBells(NumberOfBells.BELLS_8)
				.setUnfoldedNotationShorthand("-")
				.addCall("Test", "T", "12", true)
				.build();
		assertEquals("T", notation.getCalls().iterator().next().getNameShorthand());
	}

}
