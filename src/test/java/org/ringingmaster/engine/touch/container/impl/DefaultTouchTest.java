package org.ringingmaster.engine.touch.container.impl;

import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.method.impl.MethodBuilder;
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.notation.impl.NotationBuilder;
import org.ringingmaster.engine.touch.container.Touch;
import org.ringingmaster.engine.touch.container.TouchCheckingType;
import org.ringingmaster.engine.touch.container.TouchDefinition;
import org.ringingmaster.engine.touch.parser.impl.DefaultParser;
import com.google.common.collect.Iterators;
import org.junit.Assert;
import org.junit.Test;
import org.ringingmaster.engine.touch.newcontainer.variance.VarianceLogicType;
import org.ringingmaster.engine.touch.newcontainer.variance.impl.OddEvenVariance;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * User: Stephen
 */
public class DefaultTouchTest {

	public static final NotationBody METHOD_A_6_BELL = buildNotation(NumberOfBells.BELLS_6, "METHOD A", "12");
	public static final NotationBody METHOD_B_6_BELL = buildNotation(NumberOfBells.BELLS_6, "METHOD B", "14");
	public static final NotationBody METHOD_C_6_BELL = buildNotation(NumberOfBells.BELLS_6, "METHOD C", "16");
	public static final NotationBody METHOD_D_6_BELL = buildNotation(NumberOfBells.BELLS_6, "METHOD D", "34");

	public static final NotationBody METHOD_A_7_BELL = buildNotation(NumberOfBells.BELLS_7, "METHOD A", "1");

	public static final NotationBody METHOD_A_8_BELL = buildNotation(NumberOfBells.BELLS_8, "METHOD A", "12");
	public static final NotationBody METHOD_B_8_BELL = buildNotation(NumberOfBells.BELLS_8, "METHOD B", "14");
	public static final NotationBody METHOD_C_8_BELL = buildNotation(NumberOfBells.BELLS_8, "METHOD C", "16");
	public static final NotationBody METHOD_D_8_BELL = buildNotation(NumberOfBells.BELLS_8, "METHOD D", "18");

	@Test
	public void canAddAndRemoveNotations() {
		NotationBody mockNotation1 = mock(NotationBody.class);
		when(mockNotation1.getName()).thenReturn("Method 1");
		when(mockNotation1.getNumberOfWorkingBells()).thenReturn(NumberOfBells.BELLS_6);
		when(mockNotation1.getNotationDisplayString(anyBoolean())).thenReturn("12,34");
		DefaultTouch touch = new DefaultTouch();
		touch.addNotation(mockNotation1);

		List<NotationBody> retrievedNotations = touch.getAllNotations();
		assertEquals(1, retrievedNotations.size());
		assertEquals(mockNotation1, retrievedNotations.iterator().next());

		NotationBody mockNotation2 = mock(NotationBody.class);
		when(mockNotation2.getName()).thenReturn("Method 2");
		when(mockNotation2.getNumberOfWorkingBells()).thenReturn(NumberOfBells.BELLS_6);
		when(mockNotation2.getNotationDisplayString(anyBoolean())).thenReturn("12,56");
		touch.addNotation(mockNotation2);

		retrievedNotations = touch.getAllNotations();
		assertEquals(2, retrievedNotations.size());

		touch.removeNotation(mockNotation1);
		retrievedNotations = touch.getAllNotations();
		assertEquals(mockNotation2, retrievedNotations.iterator().next());
	}

	@Test(expected = IllegalArgumentException.class)
	public void addingDuplicateNotationNameThrows() {
		DefaultTouch touch = null;
		NotationBody mockNotation2 = null;
		try {
			NotationBody mockNotation1 = mock(NotationBody.class);
			when(mockNotation1.getName()).thenReturn("Duplicate Name");
			when(mockNotation1.getNumberOfWorkingBells()).thenReturn(NumberOfBells.BELLS_6);

			mockNotation2 = mock(NotationBody.class);
			when(mockNotation2.getName()).thenReturn("Duplicate Name");
			when(mockNotation2.getNumberOfWorkingBells()).thenReturn(NumberOfBells.BELLS_6);

			touch = new DefaultTouch();
			touch.addNotation(mockNotation1);
		}
		catch (Exception e) {
			fail();
		}
		touch.addNotation(mockNotation2);
	}

	@Test
	public void addingFirstNotationToNonSplicedSetsDefaultNotation() {
		DefaultTouch touch = new DefaultTouch();
		touch.setSpliced(false);
		touch.addNotation(METHOD_A_8_BELL);

		Assert.assertEquals(METHOD_A_8_BELL, touch.getNonSplicedActiveNotation());
		assertFalse(touch.isSpliced());
	}

	@Test
	public void removingOnlyNotationRemovesActiveNotation() throws CloneNotSupportedException {
		DefaultTouch touch = new DefaultTouch();
		touch.setNumberOfBells(NumberOfBells.BELLS_8);

		touch.addNotation(METHOD_A_8_BELL);

		touch.removeNotation(METHOD_A_8_BELL);
		assertNull(touch.getNonSplicedActiveNotation());
	}

	@Test
	public void removingActiveNotationChoosesNextNotationAlphabetically() {
		DefaultTouch touch = new DefaultTouch();
		touch.setSpliced(false);

		touch.addNotation(METHOD_A_6_BELL);
		touch.addNotation(METHOD_B_6_BELL);
		touch.addNotation(METHOD_C_6_BELL);
		touch.addNotation(METHOD_D_6_BELL);

		touch.setNonSplicedActiveNotation(METHOD_C_6_BELL);
		Assert.assertEquals(METHOD_C_6_BELL, touch.getNonSplicedActiveNotation());

		touch.removeNotation(METHOD_C_6_BELL);
		Assert.assertEquals(METHOD_D_6_BELL, touch.getNonSplicedActiveNotation());
		touch.removeNotation(METHOD_D_6_BELL);
		Assert.assertEquals(METHOD_B_6_BELL, touch.getNonSplicedActiveNotation());
		touch.removeNotation(METHOD_A_6_BELL);
		Assert.assertEquals(METHOD_B_6_BELL, touch.getNonSplicedActiveNotation());
		touch.removeNotation(METHOD_B_6_BELL);
		assertNull(touch.getNonSplicedActiveNotation());
	}

	@Test
	public void removingActiveNotationOnlyChoosesValidNotations() {
		DefaultTouch touch = new DefaultTouch();
		touch.setNumberOfBells(NumberOfBells.BELLS_6);
		touch.setSpliced(false);

		touch.addNotation(METHOD_A_6_BELL);
		touch.addNotation(METHOD_B_8_BELL);//Invalid number of bells
		touch.addNotation(METHOD_C_6_BELL);
		touch.addNotation(METHOD_D_8_BELL);//Invalid number of bells

		touch.setNonSplicedActiveNotation(METHOD_C_6_BELL);
		Assert.assertEquals(METHOD_C_6_BELL, touch.getNonSplicedActiveNotation());

		touch.removeNotation(METHOD_C_6_BELL);
		Assert.assertEquals(METHOD_A_6_BELL, touch.getNonSplicedActiveNotation());
		touch.removeNotation(METHOD_A_6_BELL);
		assertNull(touch.getNonSplicedActiveNotation());
	}

//TODO can notation in touch be an unsorted set?

	@Test
	public void removingNotationSwitchesToLexicographicallyNextActiveNotationWithinClosestNumberOfBells() {
		DefaultTouch touch = new DefaultTouch();
		touch.setNumberOfBells(NumberOfBells.BELLS_8);

		touch.addNotation(METHOD_A_8_BELL);
		touch.addNotation(METHOD_B_8_BELL);
		touch.addNotation(METHOD_C_6_BELL);
		Assert.assertEquals(METHOD_A_8_BELL, touch.getNonSplicedActiveNotation());

		touch.setNonSplicedActiveNotation(METHOD_B_8_BELL);
		Assert.assertEquals(METHOD_B_8_BELL, touch.getNonSplicedActiveNotation());

		touch.removeNotation(METHOD_B_8_BELL);
		Assert.assertEquals(METHOD_A_8_BELL, touch.getNonSplicedActiveNotation());

		touch.removeNotation(METHOD_A_8_BELL);
		Assert.assertEquals(METHOD_C_6_BELL, touch.getNonSplicedActiveNotation());
	}

	@Test
	public void removingNotationSwitchesToClosestNumberOfBellsHighestFirst() {
		DefaultTouch touch = new DefaultTouch();
		touch.setNumberOfBells(NumberOfBells.BELLS_8);

		touch.addNotation(METHOD_A_6_BELL);
		touch.addNotation(METHOD_A_7_BELL);
		touch.addNotation(METHOD_A_8_BELL);
		Assert.assertEquals(METHOD_A_6_BELL, touch.getNonSplicedActiveNotation());

		touch.setNonSplicedActiveNotation(METHOD_A_7_BELL);
		Assert.assertEquals(METHOD_A_7_BELL, touch.getNonSplicedActiveNotation());

		touch.removeNotation(METHOD_A_7_BELL);
		Assert.assertEquals(METHOD_A_8_BELL, touch.getNonSplicedActiveNotation());

		touch.removeNotation(METHOD_A_8_BELL);
		Assert.assertEquals(METHOD_A_6_BELL, touch.getNonSplicedActiveNotation());
	}

	@Test
	public void settingSplicedClearsActiveNotation() {
		DefaultTouch touch = new DefaultTouch();
		touch.setSpliced(false);

		NotationBody mockNotationA = when(mock(NotationBody.class).getName()).thenReturn("A").getMock();
		when(mockNotationA.getNumberOfWorkingBells()).thenReturn(NumberOfBells.BELLS_6);
		when(mockNotationA.getNotationDisplayString(anyBoolean())).thenReturn("12,");
		NotationBody mockNotationB = when(mock(NotationBody.class).getName()).thenReturn("B").getMock();
		when(mockNotationB.getNumberOfWorkingBells()).thenReturn(NumberOfBells.BELLS_6);
		when(mockNotationB.getNotationDisplayString(anyBoolean())).thenReturn("34,");

		touch.addNotation(mockNotationA);
		touch.addNotation(mockNotationB);

		assertNotNull(touch.getNonSplicedActiveNotation());
		touch.setSpliced(true);
		assertNull(touch.getNonSplicedActiveNotation());
	}

	@Test
	public void unsettingSplicedSetsFirstActiveNotation() {
		DefaultTouch touch = new DefaultTouch();

		NotationBody mockNotationA = when(mock(NotationBody.class).getName()).thenReturn("A").getMock();
		when(mockNotationA.getNumberOfWorkingBells()).thenReturn(NumberOfBells.BELLS_6);
		when(mockNotationA.getNotationDisplayString(anyBoolean())).thenReturn("12,");
		NotationBody mockNotationB = when(mock(NotationBody.class).getName()).thenReturn("B").getMock();
		when(mockNotationB.getNumberOfWorkingBells()).thenReturn(NumberOfBells.BELLS_6);
		when(mockNotationB.getNotationDisplayString(anyBoolean())).thenReturn("34,");
		touch.addNotation(mockNotationA);
		touch.addNotation(mockNotationB);

		touch.setSpliced(true);
		assertNull(touch.getNonSplicedActiveNotation());
		touch.setSpliced(false);
		Assert.assertEquals(mockNotationA, touch.getNonSplicedActiveNotation());
	}

	@Test
	public void settingActiveNotationUnsetsSpliced() {
		DefaultTouch touch = new DefaultTouch();

		NotationBody mockNotationA = when(mock(NotationBody.class).getName()).thenReturn("A").getMock();
		when(mockNotationA.getNumberOfWorkingBells()).thenReturn(NumberOfBells.BELLS_6);
		NotationBody mockNotationB = when(mock(NotationBody.class).getName()).thenReturn("B").getMock();
		when(mockNotationB.getNumberOfWorkingBells()).thenReturn(NumberOfBells.BELLS_6);
		touch.addNotation(mockNotationA);

		touch.setSpliced(true);
		assertEquals(true, touch.isSpliced());

		touch.setNonSplicedActiveNotation(mockNotationA);
		assertEquals(false, touch.isSpliced());
	}

	@Test
	public void widthAndHeightModified() {
		Touch touch = new DefaultTouch();
		assertEquals(1, touch.getColumnCount());
		assertEquals(1, touch.getRowCount());

		touch.setColumnCount(2);
		touch.setRowCount(3);

		assertEquals(2, touch.getColumnCount());
		assertEquals(3, touch.getRowCount());
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void cantInsertColumnOutsideExistingSize() {
		DefaultTouch touch = null;
		try {
			touch = new DefaultTouch();
		}
		catch(Exception e) {
			fail();
		}
		touch.insertCharacter(1, 0, 0, 'F');
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void cantInsertRowOutsideExistingSize() {
		DefaultTouch touch = null;
		try {
			touch = new DefaultTouch();
		}
		catch(Exception e) {
			fail();
		}
		touch.insertCharacter(0, 1, 0, 'F');
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void cantInsertCharacterOutsideExistingSize() {
		DefaultTouch touch = null;
		try {
			touch = new DefaultTouch();
		}
		catch(Exception e) {
			fail();
		}
		touch.insertCharacter(0, 0, 1, 'F');
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void removingNonExistantCharacterThrows() {

		DefaultTouch touch = null;
		try {
			touch = new DefaultTouch();

		}
		catch(Exception e) {
			fail();
		}

		touch.removeCharacter(0,0,0);
	}

	@Test
	public void canRemoveCharacter() {

		DefaultTouch touch = null;
		try {
			touch = new DefaultTouch();
			touch.insertCharacter(0, 0, 0, 'F');
		}
		catch(Exception e) {
			fail();
		}

		Assert.assertEquals(1, touch.getCell_FOR_TEST_ONLY(0,0).getLength());
		touch.removeCharacter(0,0,0);
		Assert.assertEquals(0, touch.getCell_FOR_TEST_ONLY(0,0).getLength());
	}

	@Test
	public void canCorrectlyRetrieveCharacters() {
		DefaultTouch touch = new DefaultTouch();
		touch.insertCharacter(0, 0, 0, 'F');
		org.junit.Assert.assertEquals('F', touch.getElement(0, 0, 0).getCharacter());
	}

	@Test
	public void canCorrectlyRetrieveMoreCharacters() {
		DefaultTouch touch = new DefaultTouch();
		touch.insertCharacter(0, 0, 0, 'F');
		touch.insertCharacter(0, 0, 1, 'G');
		touch.insertCharacter(0, 0, 1, 'H');
		assertEquals('F', touch.getElement(0, 0, 0).getCharacter());
		assertEquals('H', touch.getElement(0, 0, 1).getCharacter());
		assertEquals('G', touch.getElement(0, 0, 2).getCharacter());
	}
	
	@Test
	public void canSetCallingStyle() {
		DefaultTouch touch = new DefaultTouch();
		Assert.assertEquals(TouchCheckingType.COURSE_BASED, touch.getTouchCheckingType());
		touch.setTouchCheckingType(TouchCheckingType.LEAD_BASED);
		Assert.assertEquals(TouchCheckingType.LEAD_BASED, touch.getTouchCheckingType());
	}

	@Test
	public void canAddAndRemoveDefinitions() {
		DefaultTouch touch = new DefaultTouch();
		TouchDefinition a = touch.addDefinition("a", "p-p");
		Assert.assertEquals("a", Iterators.getOnlyElement(touch.getDefinitions().iterator()).getShorthand());
		touch.addDefinition("b", "-p-");
		assertEquals(2, touch.getDefinitions().size());
		touch.removeDefinition(a.getShorthand());
		Assert.assertEquals("b", Iterators.getOnlyElement(touch.getDefinitions().iterator()).getShorthand());
	}

	@Test(expected = IllegalArgumentException.class)
	public void addingDuplicateDefinitionThrows() {
		DefaultTouch touch = new DefaultTouch();
		touch.addDefinition("a", "p-p");
		touch.addDefinition("a", "sp-");
	}

	@Test
	public void addingNotationsWithDifferentNumberOfBellsFiltersInappropriateNotations() {
		DefaultTouch touch = new DefaultTouch();
		touch.setSpliced(false);
		touch.addNotation(buildPlainBobMinor());
		touch.addNotation(buildLittleBobMinor());
		touch.addNotation(buildPlainBobMMajor());

		assertEquals(3, touch.getAllNotations().size());
		assertEquals(1, touch.getNotationsInUse().size());

		touch.setSpliced(true);
		assertEquals(3, touch.getAllNotations().size());
		assertEquals(2, touch.getNotationsInUse().size());
	}

	@Test
	public void settingNumberOfBellsResetsActiveNotation() {
		DefaultTouch touch = new DefaultTouch();
		touch.setSpliced(false);
		touch.setNumberOfBells(NumberOfBells.BELLS_8);
		touch.addNotation(buildPlainBobMMajor());
		touch.addNotation(buildPlainBobMinor());

		Assert.assertEquals("Plain Bob Major", touch.getNonSplicedActiveNotation().getNameIncludingNumberOfBells());

		touch.setNumberOfBells(NumberOfBells.BELLS_6);
		Assert.assertEquals("Plain Bob Minor", touch.getNonSplicedActiveNotation().getNameIncludingNumberOfBells());
	}

	@Test(expected = IllegalStateException.class)
	public void cantSetInitialRowOfWrongNumber() {
		DefaultTouch touch = new DefaultTouch();
		touch.setNumberOfBells(NumberOfBells.BELLS_6);
		touch.setStartChange(MethodBuilder.buildRoundsRow(NumberOfBells.BELLS_7));
	}

	@Test(expected = NullPointerException.class)
	public void cantSetNullInitialRow() {
		DefaultTouch touch = new DefaultTouch();
		touch.setStartChange(null);
	}

	@Test
	public void cloneTest() throws CloneNotSupportedException {
		DefaultTouch touch = new DefaultTouch();
		touch.setNumberOfBells(NumberOfBells.BELLS_8);
		touch.setColumnCount(2);
		touch.setRowCount(3);
		touch.setTitle("title");
		touch.setAuthor("author");
		touch.addNotation(buildPlainBobMinor());
		touch.addCharacters(0, 0, "-[s-");
		touch.getCell_FOR_TEST_ONLY(0, 0).getElement(1).setVariance(new OddEvenVariance(VarianceLogicType.INCLUDE, OddEvenVariance.OddEvenVarianceType.ODD));
		touch.addCharacters(1, 0, "p");
		touch.addCharacters(1, 2, "--");
		touch.addDefinition("a", "2p-");
		touch.setPlainLeadToken("PL");
		touch.setSpliced(true);
		touch.setTerminationMaxLeads(10);
		touch.setTerminationMaxRows(100);
		touch.setTerminationChange(MethodBuilder.buildRoundsRow(NumberOfBells.BELLS_8));
		new DefaultParser().parseAndAnnotate(touch);

		Touch clone = touch.clone();
		String cloneToString = clone.toString();
		assertEquals(touch.toString(), cloneToString);

		/// check that changing the original does not change the clone.
		touch.resetParseData();
		assertEquals(cloneToString, clone.toString());
	}

	private static NotationBody buildNotation(NumberOfBells bells, String name, String notation1) {
		return NotationBuilder.getInstance()
						.setNumberOfWorkingBells(bells)
						.setName(name)
						.setUnfoldedNotationShorthand(notation1)
						.build();
	}

	private NotationBody buildPlainBobMinor() {

		return NotationBuilder.getInstance()
				.setNumberOfWorkingBells(NumberOfBells.BELLS_6)
				.setName("Plain Bob")
				.setFoldedPalindromeNotationShorthand("x16x16x", "16")
				.addCall("Bob", "-", "14", true)
				.addCall("Single", "s", "1234", false)
				.setSpliceIdentifier("p")
				.build();
	}

	private NotationBody buildLittleBobMinor() {

		return NotationBuilder.getInstance()
				.setNumberOfWorkingBells(NumberOfBells.BELLS_6)
				.setName("Little Bob")
				.setFoldedPalindromeNotationShorthand("x14", "12")
				.addCall("Bob", "-", "14", true)
				.addCall("Single", "s", "1234", false)
				.setSpliceIdentifier("l")
				.build();
	}

	private NotationBody buildPlainBobMMajor() {

		return NotationBuilder.getInstance()
				.setNumberOfWorkingBells(NumberOfBells.BELLS_8)
				.setName("Plain Bob")
				.setFoldedPalindromeNotationShorthand("x18x18x18x", "18")
				.addCall("Bob", "-", "14", true)
				.setSpliceIdentifier("pbm")
				.build();
	}

}
