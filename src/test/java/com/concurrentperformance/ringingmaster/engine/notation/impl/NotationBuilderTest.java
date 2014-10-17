package com.concurrentperformance.ringingmaster.engine.notation.impl;

import org.junit.Before;
import org.junit.Test;

import com.concurrentperformance.ringingmaster.engine.NumberOfBells;
import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import com.concurrentperformance.ringingmaster.engine.notation.NotationCall;
import com.concurrentperformance.ringingmaster.engine.notation.NotationPlace;
import com.concurrentperformance.ringingmaster.engine.notation.NotationRow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class NotationBuilderTest {

	private NotationBuilder fixture;

	@Before
	public void setup() {
		fixture = NotationBuilder.getInstance();

	}

	@Test
	public void allChangeNotAllowedForOddBellMethods()  {
		final NotationBody notationBody = fixture
				.setNumberOfWorkingBells(NumberOfBells.BELLS_11)
				.setUnfoldedNotationShorthand("1.x")
				.build();

		assertEquals("for '12.x' all change should not be allowed for odd bell methods, so only 12 should be returned", 1, notationBody.getRowCount());
		final NotationRow expected = NotationRowHelper.buildNotationRow(NotationPlace.PLACE_1);
		assertEquals("'12.x' should have PLACE_1,PLACE_2 in 0th index, when on an odd mell method",expected, notationBody.getRow(0));

	}

	@Test
	public void canCreateFoldedPalindromeNotation()  {
		final NotationBody notationBody =
				fixture
				.setNumberOfWorkingBells(NumberOfBells.BELLS_7)
				.setFoldedPalindromeNotationShorthand("1.5.3", "7")
				.build();
		assertTrue(notationBody.isFoldedPalindrome());
		assertEquals("folded notationBody of '1.5' should return a 6 rows", 6, notationBody.getRowCount());
		NotationRow expected = NotationRowHelper.buildNotationRow(NotationPlace.PLACE_1);
		assertEquals("folded notationBody of '1.5' should have PLACE_1 in 0th index",expected, notationBody.getRow(0));
		assertEquals("folded notationBody of '1.5' should have PLACE_1 in 4th index",expected, notationBody.getRow(4));
		expected = NotationRowHelper.buildNotationRow(NotationPlace.PLACE_5);
		assertEquals("folded notationBody of '1.5' should have PLACE_5 in 1st index",expected, notationBody.getRow(1));
		assertEquals("folded notationBody of '1.5' should have PLACE_5 in 3nd index",expected, notationBody.getRow(3));
		expected = NotationRowHelper.buildNotationRow(NotationPlace.PLACE_3);
		assertEquals("folded notationBody of '1.5' should have PLACE_3 in 2nd index",expected, notationBody.getRow(2));
		expected = NotationRowHelper.buildNotationRow(NotationPlace.PLACE_7);
		assertEquals("folded notationBody of '1.5' should have PLACE_7 in 5th index",expected, notationBody.getRow(5));
	}

	@Test
	public void canCreateNotationWithManyElements()  {
		final NotationBody notationBody =
				fixture
				.setUnfoldedNotationShorthand("X14")
				.build();
		assertFalse(notationBody.isFoldedPalindrome());
		assertEquals("notationBody of 'X14' should return a 2 rows", 2, notationBody.getRowCount());
		NotationRow expected = NotationRowHelper.buildNotationRow(NotationPlace.ALL_CHANGE);
		assertEquals("notationBody of 'X14' should have ALL_CHANGE in 0th index",expected, notationBody.getRow(0));
		expected = NotationRowHelper.buildNotationRow(NotationPlace.PLACE_1,
				NotationPlace.PLACE_4);
		assertEquals("notationBody of 'X14' should have PLACE_1, PLACE_4 in 1st index", expected, notationBody.getRow(1));
	}

	@Test
	public void canCreateNotationWithOneElement()  {
		final NotationBody notationBody = fixture
				.setUnfoldedNotationShorthand("X")
				.build();
		assertFalse(notationBody.isFoldedPalindrome());
		assertEquals("notationBody of 'X' should return a single row", 1, notationBody.getRowCount());
		final NotationRow expected = NotationRowHelper.buildNotationRow(NotationPlace.ALL_CHANGE);
		assertEquals(expected, notationBody.getRow(0));
	}

	@Test
	public void canSeparateAllChangeAndPlaces() {
		final NotationBody notationBody =
				fixture
				.setUnfoldedNotationShorthand("x18x")
				.build();

		assertEquals("'x18x is correctly separated", 3, notationBody.getRowCount());
		NotationRow expected = NotationRowHelper.buildNotationRow(NotationPlace.ALL_CHANGE);
		assertEquals("x18x is correctly separated",expected, notationBody.getRow(0));
		assertEquals("x18x is correctly separated",expected, notationBody.getRow(2));
		expected = NotationRowHelper.buildNotationRow(NotationPlace.PLACE_1, NotationPlace.PLACE_8);
		assertEquals("x18x is correctly separated",expected, notationBody.getRow(1));
	}

	@Test (expected = IllegalStateException.class)
	public void emptyNotationCantBeBuilt()  {
		final NotationBody notationBody = fixture.build();
		assertNotNull("Should return an empty notationBody", notationBody);
		assertEquals("notationBody should be empty", 0, notationBody.getRowCount());
	}


	@Test
	public void invalidNotationCharactersAreRemoved() {
		final NotationBody notationBody =
				fixture
				.setUnfoldedNotationShorthand("12.Zxy.w12f.g")
				.build();

		assertEquals("invalidNotationCharacters in '12.Zxy.w12f.g' should return a 3 rows", 3, notationBody.getRowCount());
		NotationRow expected = NotationRowHelper.buildNotationRow(NotationPlace.PLACE_1, NotationPlace.PLACE_2);
		assertEquals("invalidNotationCharacters in '12.Zxy.w12f.g' should have PLACE_1,PLACE_2 in 0th index",expected, notationBody.getRow(0));
		assertEquals("invalidNotationCharacters in '12.Zxy.w12f.g' should have PLACE_1,PLACE_2 in 2th index",expected, notationBody.getRow(2));
		expected = NotationRowHelper.buildNotationRow(NotationPlace.ALL_CHANGE);
		assertEquals("invalidNotationCharacters in '12.Zxy.w12f.g' should have ALL_CHANGE in 1st index",expected, notationBody.getRow(1));
	}

	@Test
	public void placesAreOrdered() {
		final NotationBody notationBody =
				fixture
				.setUnfoldedNotationShorthand("8714")
				.build();

		final NotationRow row = notationBody.getRow(0);
		assertEquals(NotationPlace.PLACE_1, row.getElement(0));
		assertEquals(NotationPlace.PLACE_4, row.getElement(1));
		assertEquals(NotationPlace.PLACE_7, row.getElement(2));
		assertEquals(NotationPlace.PLACE_8, row.getElement(3));
	}

	@Test
	public void placesEqualToNumberOfBellsIsAllowed()  {
		fixture.setNumberOfWorkingBells(NumberOfBells.BELLS_8);
		fixture.setUnfoldedNotationShorthand("18");
		final NotationBody notationBody = fixture.build();

		assertEquals("For '18' on a 8 bell method, one row should be returned", 1, notationBody.getRowCount());
		final NotationRow expected = NotationRowHelper.buildNotationRow(NotationPlace.PLACE_1, NotationPlace.PLACE_8);
		assertEquals("For '18' on a 8 bell method, only the place 1 & 8  should be returned",expected, notationBody.getRow(0));
	}

	@Test
	public void placesHigherThanNumberOfBellsNotAllowed()  {
		final NotationBody notationBody =
				fixture
				.setNumberOfWorkingBells(NumberOfBells.BELLS_7)
				.setUnfoldedNotationShorthand("18")
				.build();

		assertEquals("For '18' on a 7 bell method, one row should be returned", 1, notationBody.getRowCount());
		final NotationRow expected = NotationRowHelper.buildNotationRow(NotationPlace.PLACE_1);
		assertEquals("For '18' on a 7 bell method, only the place 1 should be returned", expected, notationBody.getRow(0));
	}

	@Test
	public void toStringDoesNotThrowException()  {
		final NotationBody notationBody = fixture
				.addCall("test", "r", "12.34", true)
				.setUnfoldedNotationShorthand("-")
				.build();
		System.out.println(notationBody.toString());
	}

	@Test
	public void buildingCallAllowsCorrectRetrieval()  {
		NotationBody notationBody =
				fixture.addCall("Bob", "A", "12.-.34", true)
						.setUnfoldedNotationShorthand("-")
						.build();

		NotationCall call = notationBody.getCalls().iterator().next();

		assertEquals("Bob", call.getName());
		assertEquals("A", call.getNameShorthand());
		assertEquals("12-34", call.getNotationDisplayString(true));
	}

	@Test(expected = IllegalArgumentException.class)
	public void buildingNotationWithDuplicateCallNamesThrows()  {
		fixture.addCall("DUPLICATE", "A", "12.34", true)
			   .addCall("DUPLICATE", "B", "56.34", false);
	}

	@Test(expected = IllegalArgumentException.class)
	public void buildingNotationWithDuplicateCallShortcutNamesThrows()  {
		fixture.addCall("A", "DUPLICATE", "12.34", true)
				.addCall("B", "DUPLICATE", "56.34", false);
	}

	@Test(expected = IllegalArgumentException.class)
	public void buildingNotationWithDuplicateCallNotationThrows()  {
		fixture.addCall("A", "C", "12.34", true)
				.addCall("B", "D", "12.34", false);
	}

	@Test(expected = IllegalArgumentException.class)
	public void buildingNotationWithDuplicateCallShorthandAndNameThrows()  {
		fixture.addCall("DUPLICATE", "A", "12.34", true)
				.addCall("B", "DUPLICATE", "56.34", false);
	}

	@Test
	public void canSetAndRetrieveSpliceIdentifier()  {
		String spliceIdentifier = "SP";
		NotationBody notation = fixture.setSpliceIdentifier(spliceIdentifier)
				.setUnfoldedNotationShorthand("-")
				.build();
		assertEquals(spliceIdentifier, notation.getSpliceIdentifier());
	}

	@Test
	public void setAndRetrieveCallInitiationRows()  {
		NotationBody notation = fixture.setUnfoldedNotationShorthand("12.34")
				.addCallInitiationRow(2)
				.build();

		assertEquals(2, notation.getCallInitiationRows().iterator().next().intValue());
	}

	@Test(expected = IllegalArgumentException.class)
	public void addingNegativeCallInitiationRowThrows()  {
		fixture.addCallInitiationRow(-1);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void addingCallInitiationRowGreaterThanPlainLeadLengthThrows()  {
		fixture .setUnfoldedNotationShorthand("12.34")
				.addCallInitiationRow(3)
				.build();

	}

	@Test
	public void addingNoLeadBasedCallingPositionAddsDefault()  {
		NotationBody notation = fixture.setUnfoldedNotationShorthand("12.34")
				.build();
		assertEquals(1, notation.getCallInitiationRows().size());
		assertEquals(1, (int)notation.getCallInitiationRows().first());
	}

	@Test
	public void addingDefaultCallResultsInDefaultCall() {
		NotationBody notation = fixture
				.addCall("AAA", "A", "12.34", false)
				.addCall("BBB", "B", "56.34", true)
				.setUnfoldedNotationShorthand("-")
				.build();

		assertEquals("BBB", notation.getDefaultCall().getName());
	}

	@Test
	public void addingNoDefaultCallResultsInFirstAlphaCall() {
		NotationBody notation = fixture
				.addCall("BBB", "B", "56.34", false)
				.addCall("AAA", "A", "12.34", false)
				.setUnfoldedNotationShorthand("-")
				.build();

		assertEquals("AAA", notation.getDefaultCall().getName());
	}

	@Test
	public void addingNoDefaultCallButHaveBobUsesDefaultBob() {
		NotationBody notation = fixture
				.addCall("Bob", "-", "56.34", false)
				.addCall("AAA", "A", "12.34", false)
				.setUnfoldedNotationShorthand("-")
				.build();

		assertEquals("Bob", notation.getDefaultCall().getName());
	}

	@Test(expected = IllegalStateException.class)
	public void addingNotationWithHigherPlacesThrows() {
		fixture	.setNumberOfWorkingBells(NumberOfBells.BELLS_6)
				.setUnfoldedNotationShorthand("18")
				.build();
	}

	@Test(expected = IllegalStateException.class)
	public void addingNotationWithHigherLeadEndThrows() {
		fixture	.setNumberOfWorkingBells(NumberOfBells.BELLS_6)
				.setUnfoldedNotationShorthand("16")
				.setUnfoldedNotationShorthand("18")
				.build();
	}
}
