package org.ringingmaster.engine.notation.impl;

import org.junit.Before;
import org.junit.Test;

import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.notation.NotationCall;
import org.ringingmaster.engine.notation.NotationPlace;
import org.ringingmaster.engine.notation.NotationRow;

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
				.setNumberOfWorkingBells(NumberOfBells.BELLS_8)
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
				.setNumberOfWorkingBells(NumberOfBells.BELLS_8)
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
				.setNumberOfWorkingBells(NumberOfBells.BELLS_8)
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
				.setNumberOfWorkingBells(NumberOfBells.BELLS_8)
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
				.setNumberOfWorkingBells(NumberOfBells.BELLS_8)
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
				.setNumberOfWorkingBells(NumberOfBells.BELLS_8)
				.build();
		System.out.println(notationBody.toString());
	}

	@Test
	public void buildingCallAllowsCorrectRetrieval()  {
		NotationBody notationBody =
				fixture.addCall("Bob", "A", "12.-.34", true)
						.setUnfoldedNotationShorthand("-")
						.setNumberOfWorkingBells(NumberOfBells.BELLS_8)
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
				.addCall("B", "DUPLICATE", "56.34", false)
				.setNumberOfWorkingBells(NumberOfBells.BELLS_8)
		;
	}

	@Test(expected = IllegalArgumentException.class)
	public void buildingNotationWithDuplicateCallNotationThrows()  {
		fixture.addCall("A", "C", "12.34", true)
				.addCall("B", "D", "12.34", false)
				.setNumberOfWorkingBells(NumberOfBells.BELLS_8)
		;
	}

	@Test(expected = IllegalArgumentException.class)
	public void buildingNotationWithDuplicateCallShorthandAndNameThrows()  {
		fixture.addCall("DUPLICATE", "A", "12.34", true)
				.addCall("B", "DUPLICATE", "56.34", false)
				.setNumberOfWorkingBells(NumberOfBells.BELLS_8)
		;
	}

	@Test
	public void canSetAndRetrieveSpliceIdentifier()  {
		String spliceIdentifier = "SP";
		NotationBody notation = fixture.setSpliceIdentifier(spliceIdentifier)
				.setUnfoldedNotationShorthand("-")
				.setNumberOfWorkingBells(NumberOfBells.BELLS_8)
				.build();
		assertEquals(spliceIdentifier, notation.getSpliceIdentifier());
	}

	@Test
	public void setAndRetrieveCallInitiationRows()  {
		NotationBody notation = fixture.setUnfoldedNotationShorthand("12.34")
				.addCallInitiationRow(2)
				.setNumberOfWorkingBells(NumberOfBells.BELLS_8)
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
				.setNumberOfWorkingBells(NumberOfBells.BELLS_8)
				.build();

	}

	@Test
	public void addingNoLeadBasedCallingPositionAddsDefault()  {
		NotationBody notation = fixture.setUnfoldedNotationShorthand("12.34")
				.setNumberOfWorkingBells(NumberOfBells.BELLS_8)
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
				.setNumberOfWorkingBells(NumberOfBells.BELLS_8)
				.build();

		assertEquals("BBB", notation.getDefaultCall().getName());
	}

	@Test
	public void addingNoDefaultCallResultsInFirstAlphaCall() {
		NotationBody notation = fixture
				.addCall("BBB", "B", "56.34", false)
				.addCall("AAA", "A", "12.34", false)
				.setUnfoldedNotationShorthand("-")
				.setNumberOfWorkingBells(NumberOfBells.BELLS_8)
				.build();

		assertEquals("AAA", notation.getDefaultCall().getName());
	}

	@Test
	public void addingNoDefaultCallButHaveBobUsesDefaultBob() {
		NotationBody notation = fixture
				.addCall("Bob", "-", "56.34", false)
				.addCall("AAA", "A", "12.34", false)
				.setUnfoldedNotationShorthand("-")
				.setNumberOfWorkingBells(NumberOfBells.BELLS_8)
				.build();

		assertEquals("Bob", notation.getDefaultCall().getName());
	}

	@Test
	public void canBuildEmptyUnfolded() {
		NotationBody notation = fixture
				.setNumberOfWorkingBells(NumberOfBells.BELLS_6)
				.setUnfoldedNotationShorthand("")
				.setNumberOfWorkingBells(NumberOfBells.BELLS_8)
				.build();

		assertEquals(0, notation.getRowCount());
	}

	@Test
	public void canBuildEmptyFoldedPalindrome() {
		NotationBody notation = fixture
				.setNumberOfWorkingBells(NumberOfBells.BELLS_6)
				.setFoldedPalindromeNotationShorthand("", "")
				.build();

		assertEquals(",", notation.getNotationDisplayString(false)); //NOTE: The comma on the end of the 16 indicates folded notation without a lead end
		assertEquals(0, notation.getRowCount());
	}

	@Test
	public void addingNotationWithHigherPlacesExcludesPlaces() {
		NotationBody notation = fixture
				.setNumberOfWorkingBells(NumberOfBells.BELLS_6)
				.setUnfoldedNotationShorthand("18")
				.build();

		assertEquals("", notation.getNotationDisplayString(false));
		assertEquals(0, notation.getRowCount());
	}

	@Test
	public void addingNotationWithEmptyLeadEndBuildsCorrectRowCount() {
		NotationBody notation = fixture
				.setNumberOfWorkingBells(NumberOfBells.BELLS_6)
				.setFoldedPalindromeNotationShorthand("16", "")
				.build();

		assertEquals("16,", notation.getNotationDisplayString(false)); //NOTE: The comma on the end of the 16 indicates folded notation without a lead end
		assertEquals(1, notation.getRowCount());
	}

	@Test
	public void addingNotationWithHigherLeadEndExcludesPlaces() {
		NotationBody notation = fixture
				.setNumberOfWorkingBells(NumberOfBells.BELLS_6)
				.setFoldedPalindromeNotationShorthand("16","18")
				.build();

		assertEquals("16,", notation.getNotationDisplayString(false)); //NOTE: The comma on the end of the 16 indicates folded notation without a lead end
		assertEquals(1, notation.getRowCount());
	}

	@Test
	public void addingNotationMultiLeadEndBuildsMultiRowLeadEnd() {
		NotationBody notation = fixture
				.setNumberOfWorkingBells(NumberOfBells.BELLS_6)
				.setFoldedPalindromeNotationShorthand("16","x.14")
				.build();

		assertEquals("16,-.14", notation.getNotationDisplayString(false));
		assertEquals(4, notation.getRowCount());
	}

}
