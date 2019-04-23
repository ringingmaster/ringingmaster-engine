package org.ringingmaster.engine.notation;

import org.junit.Before;
import org.junit.Test;

import org.ringingmaster.engine.NumberOfBells;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class PlaceSetSequenceBuilderTest {

	private NotationBuilder fixture;

	@Before
	public void setup() {
		fixture = NotationBuilder.getInstance();

	}

	@Test
	public void allChangeNotAllowedForOddBellMethods()  {
		final Notation notation = fixture
				.setNumberOfWorkingBells(NumberOfBells.BELLS_11)
				.setUnfoldedNotationShorthand("1.x")
				.build();

		assertEquals("for '12.x' all change should not be allowed for odd bell methods, so only 12 should be returned", 1, notation.size());
		final PlaceSet expected = NotationRowHelper.buildNotationRow(Place.PLACE_1);
		assertEquals("'12.x' should have PLACE_1,PLACE_2 in 0th index, when on an odd mell method",expected, notation.get(0));

	}

	@Test
	public void canCreateFoldedPalindromeNotation()  {
		final Notation notation =
				fixture
				.setNumberOfWorkingBells(NumberOfBells.BELLS_7)
				.setFoldedPalindromeNotationShorthand("1.5.3", "7")
				.build();
		assertTrue(notation.isFoldedPalindrome());
		assertEquals("folded notation of '1.5' should return a 6 rows", 6, notation.size());
		PlaceSet expected = NotationRowHelper.buildNotationRow(Place.PLACE_1);
		assertEquals("folded notation of '1.5' should have PLACE_1 in 0th index",expected, notation.get(0));
		assertEquals("folded notation of '1.5' should have PLACE_1 in 4th index",expected, notation.get(4));
		expected = NotationRowHelper.buildNotationRow(Place.PLACE_5);
		assertEquals("folded notation of '1.5' should have PLACE_5 in 1st index",expected, notation.get(1));
		assertEquals("folded notation of '1.5' should have PLACE_5 in 3nd index",expected, notation.get(3));
		expected = NotationRowHelper.buildNotationRow(Place.PLACE_3);
		assertEquals("folded notation of '1.5' should have PLACE_3 in 2nd index",expected, notation.get(2));
		expected = NotationRowHelper.buildNotationRow(Place.PLACE_7);
		assertEquals("folded notation of '1.5' should have PLACE_7 in 5th index",expected, notation.get(5));
	}

	@Test
	public void canCreateNotationWithManyElements()  {
		final Notation notation =
				fixture
				.setUnfoldedNotationShorthand("X14")
				.setNumberOfWorkingBells(NumberOfBells.BELLS_8)
				.build();
		assertFalse(notation.isFoldedPalindrome());
		assertEquals("notation of 'X14' should return a 2 rows", 2, notation.size());
		PlaceSet expected = NotationRowHelper.buildNotationRow(Place.ALL_CHANGE);
		assertEquals("notation of 'X14' should have ALL_CHANGE in 0th index",expected, notation.get(0));
		expected = NotationRowHelper.buildNotationRow(Place.PLACE_1,
				Place.PLACE_4);
		assertEquals("notation of 'X14' should have PLACE_1, PLACE_4 in 1st index", expected, notation.get(1));
	}

	@Test
	public void canCreateNotationWithOneElement()  {
		final Notation notation = fixture
				.setUnfoldedNotationShorthand("X")
				.setNumberOfWorkingBells(NumberOfBells.BELLS_8)
				.build();
		assertFalse(notation.isFoldedPalindrome());
		assertEquals("notation of 'X' should return a single row", 1, notation.size());
		final PlaceSet expected = NotationRowHelper.buildNotationRow(Place.ALL_CHANGE);
		assertEquals(expected, notation.get(0));
	}

	@Test
	public void canSeparateAllChangeAndPlaces() {
		final Notation notation =
				fixture
				.setUnfoldedNotationShorthand("x18x")
				.setNumberOfWorkingBells(NumberOfBells.BELLS_8)
				.build();

		assertEquals("'x18x is correctly separated", 3, notation.size());
		PlaceSet expected = NotationRowHelper.buildNotationRow(Place.ALL_CHANGE);
		assertEquals("x18x is correctly separated",expected, notation.get(0));
		assertEquals("x18x is correctly separated",expected, notation.get(2));
		expected = NotationRowHelper.buildNotationRow(Place.PLACE_1, Place.PLACE_8);
		assertEquals("x18x is correctly separated",expected, notation.get(1));
	}

	@Test (expected = IllegalStateException.class)
	public void emptyNotationCantBeBuilt()  {
		final Notation notation = fixture.build();
		assertNotNull("Should return an empty notation", notation);
		assertEquals("notation should be empty", 0, notation.size());
	}


	@Test
	public void invalidNotationCharactersAreRemoved() {
		final Notation notation =
				fixture
				.setUnfoldedNotationShorthand("12.Zxy.w12f.g")
				.setNumberOfWorkingBells(NumberOfBells.BELLS_8)
				.build();

		assertEquals("invalidNotationCharacters in '12.Zxy.w12f.g' should return a 3 rows", 3, notation.size());
		PlaceSet expected = NotationRowHelper.buildNotationRow(Place.PLACE_1, Place.PLACE_2);
		assertEquals("invalidNotationCharacters in '12.Zxy.w12f.g' should have PLACE_1,PLACE_2 in 0th index",expected, notation.get(0));
		assertEquals("invalidNotationCharacters in '12.Zxy.w12f.g' should have PLACE_1,PLACE_2 in 2th index",expected, notation.get(2));
		expected = NotationRowHelper.buildNotationRow(Place.ALL_CHANGE);
		assertEquals("invalidNotationCharacters in '12.Zxy.w12f.g' should have ALL_CHANGE in 1st index",expected, notation.get(1));
	}

	@Test
	public void placesAreOrdered() {
		final Notation notation =
				fixture
				.setUnfoldedNotationShorthand("8714")
				.setNumberOfWorkingBells(NumberOfBells.BELLS_8)
				.build();

		final PlaceSet row = notation.get(0);
		assertEquals(Place.PLACE_1, row.get(0));
		assertEquals(Place.PLACE_4, row.get(1));
		assertEquals(Place.PLACE_7, row.get(2));
		assertEquals(Place.PLACE_8, row.get(3));
	}

	@Test
	public void placesEqualToNumberOfBellsIsAllowed()  {
		fixture.setNumberOfWorkingBells(NumberOfBells.BELLS_8);
		fixture.setUnfoldedNotationShorthand("18");
		final Notation notation = fixture.build();

		assertEquals("For '18' on a 8 bell method, one row should be returned", 1, notation.size());
		final PlaceSet expected = NotationRowHelper.buildNotationRow(Place.PLACE_1, Place.PLACE_8);
		assertEquals("For '18' on a 8 bell method, only the place 1 & 8  should be returned",expected, notation.get(0));
	}

	@Test
	public void placesHigherThanNumberOfBellsNotAllowed()  {
		final Notation notation =
				fixture
				.setNumberOfWorkingBells(NumberOfBells.BELLS_7)
				.setUnfoldedNotationShorthand("18")
				.build();

		assertEquals("For '18' on a 7 bell method, one row should be returned", 1, notation.size());
		final PlaceSet expected = NotationRowHelper.buildNotationRow(Place.PLACE_1);
		assertEquals("For '18' on a 7 bell method, only the place 1 should be returned", expected, notation.get(0));
	}

	@Test
	public void toStringDoesNotThrowException()  {
		final Notation notation = fixture
				.addCall("test", "r", "12.34", true)
				.setUnfoldedNotationShorthand("-")
				.setNumberOfWorkingBells(NumberOfBells.BELLS_8)
				.build();
		System.out.println(notation.toString());
	}

	@Test
	public void buildingCallAllowsCorrectRetrieval()  {
		Notation notation =
				fixture.addCall("Bob", "A", "12.-.34", true)
						.setUnfoldedNotationShorthand("-")
						.setNumberOfWorkingBells(NumberOfBells.BELLS_8)
						.build();

		Call call = notation.getCalls().iterator().next();

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
		Notation notation = fixture.setSpliceIdentifier(spliceIdentifier)
				.setUnfoldedNotationShorthand("-")
				.setNumberOfWorkingBells(NumberOfBells.BELLS_8)
				.build();
		assertEquals(spliceIdentifier, notation.getSpliceIdentifier());
	}

	@Test
	public void setAndRetrieveCallInitiationRows()  {
		Notation notation = fixture.setUnfoldedNotationShorthand("12.34")
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
		Notation notation = fixture.setUnfoldedNotationShorthand("12.34")
				.setNumberOfWorkingBells(NumberOfBells.BELLS_8)
				.build();
		assertEquals(1, notation.getCallInitiationRows().size());
		assertEquals(1, (int)notation.getCallInitiationRows().first());
	}

	@Test
	public void addingDefaultCallResultsInDefaultCall() {
		Notation notation = fixture
				.addCall("AAA", "A", "12.34", false)
				.addCall("BBB", "B", "56.34", true)
				.setUnfoldedNotationShorthand("-")
				.setNumberOfWorkingBells(NumberOfBells.BELLS_8)
				.build();

		assertEquals("BBB", notation.getDefaultCall().getName());
	}

	@Test
	public void addingNoDefaultCallResultsInFirstAlphaCall() {
		Notation notation = fixture
				.addCall("BBB", "B", "56.34", false)
				.addCall("AAA", "A", "12.34", false)
				.setUnfoldedNotationShorthand("-")
				.setNumberOfWorkingBells(NumberOfBells.BELLS_8)
				.build();

		assertEquals("AAA", notation.getDefaultCall().getName());
	}

	@Test
	public void addingNoDefaultCallButHaveBobUsesDefaultBob() {
		Notation notation = fixture
				.addCall("Bob", "-", "56.34", false)
				.addCall("AAA", "A", "12.34", false)
				.setUnfoldedNotationShorthand("-")
				.setNumberOfWorkingBells(NumberOfBells.BELLS_8)
				.build();

		assertEquals("Bob", notation.getDefaultCall().getName());
	}

	@Test
	public void canBuildEmptyUnfolded() {
		Notation notation = fixture
				.setNumberOfWorkingBells(NumberOfBells.BELLS_6)
				.setUnfoldedNotationShorthand("")
				.setNumberOfWorkingBells(NumberOfBells.BELLS_8)
				.build();

		assertEquals(0, notation.size());
	}

	@Test
	public void canBuildEmptyFoldedPalindrome() {
		Notation notation = fixture
				.setNumberOfWorkingBells(NumberOfBells.BELLS_6)
				.setFoldedPalindromeNotationShorthand("", "")
				.build();

		assertEquals(",", notation.getNotationDisplayString(false)); //NOTE: The comma on the end of the 16 indicates folded notation without a lead end
		assertEquals(0, notation.size());
	}

	@Test
	public void addingNotationWithHigherPlacesExcludesPlaces() {
		Notation notation = fixture
				.setNumberOfWorkingBells(NumberOfBells.BELLS_6)
				.setUnfoldedNotationShorthand("18")
				.build();

		assertEquals("", notation.getNotationDisplayString(false));
		assertEquals(0, notation.size());
	}

	@Test
	public void addingNotationWithEmptyLeadEndBuildsCorrectRowCount() {
		Notation notation = fixture
				.setNumberOfWorkingBells(NumberOfBells.BELLS_6)
				.setFoldedPalindromeNotationShorthand("16", "")
				.build();

		assertEquals("16,", notation.getNotationDisplayString(false)); //NOTE: The comma on the end of the 16 indicates folded notation without a lead end
		assertEquals(1, notation.size());
	}

	@Test
	public void addingNotationWithHigherLeadEndExcludesPlaces() {
		Notation notation = fixture
				.setNumberOfWorkingBells(NumberOfBells.BELLS_6)
				.setFoldedPalindromeNotationShorthand("16","18")
				.build();

		assertEquals("16,", notation.getNotationDisplayString(false)); //NOTE: The comma on the end of the 16 indicates folded notation without a lead end
		assertEquals(1, notation.size());
	}

	@Test
	public void addingNotationMultiLeadEndBuildsMultiRowLeadEnd() {
		Notation notation = fixture
				.setNumberOfWorkingBells(NumberOfBells.BELLS_6)
				.setFoldedPalindromeNotationShorthand("16","x.14")
				.build();

		assertEquals("16,-.14", notation.getNotationDisplayString(false));
		assertEquals(4, notation.size());
	}

}
