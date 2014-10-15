package com.concurrentperformance.ringingmaster.engine.touch.impl;

import com.concurrentperformance.ringingmaster.engine.NumberOfBells;
import com.concurrentperformance.ringingmaster.engine.method.impl.MethodBuilder;
import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import com.concurrentperformance.ringingmaster.engine.notation.impl.NotationBuilder;
import com.concurrentperformance.ringingmaster.engine.parser.impl.DefaultParser;
import com.concurrentperformance.ringingmaster.engine.touch.Touch;
import com.concurrentperformance.ringingmaster.engine.touch.TouchDefinition;
import com.concurrentperformance.ringingmaster.engine.touch.TouchType;
import com.google.common.collect.Iterators;
import junit.framework.Assert;
import org.junit.Test;

import java.util.Set;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * User: Stephen
 */
public class DefaultTouchTest {

	@Test
	public void canAddAndRemoveNotations() {
		NotationBody mockNotation1 = mock(NotationBody.class);
		when(mockNotation1.getName()).thenReturn("Method 1");
		when(mockNotation1.getNumberOfWorkingBells()).thenReturn(NumberOfBells.BELLS_6);
		DefaultTouch touch = new DefaultTouch();
		touch.addNotation(mockNotation1);

		Set<NotationBody> retrievedNotations = touch.getAllNotations();
		assertEquals(1, retrievedNotations.size());
		assertEquals(mockNotation1, retrievedNotations.iterator().next());

		NotationBody mockNotation2 = mock(NotationBody.class);
		when(mockNotation2.getName()).thenReturn("Method 2");
		when(mockNotation2.getNumberOfWorkingBells()).thenReturn(NumberOfBells.BELLS_6);
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

		NotationBody mockNotation = when(mock(NotationBody.class).getName()).thenReturn("A").getMock();
		when(mockNotation.getNumberOfWorkingBells()).thenReturn(NumberOfBells.BELLS_6);
		touch.addNotation(mockNotation);

		assertEquals(mockNotation, touch.getActiveNotation());
		assertFalse(touch.isSpliced());
	}

	@Test
	public void removingActiveNotationChoosesNextNotationAlphabetically() {
		DefaultTouch touch = new DefaultTouch();
		touch.setSpliced(false);

		NotationBody mockNotationA = when(mock(NotationBody.class).getName()).thenReturn("A").getMock();
		when(mockNotationA.getNumberOfWorkingBells()).thenReturn(NumberOfBells.BELLS_6);
		NotationBody mockNotationB = when(mock(NotationBody.class).getName()).thenReturn("B").getMock();
		when(mockNotationB.getNumberOfWorkingBells()).thenReturn(NumberOfBells.BELLS_6);
		NotationBody mockNotationC = when(mock(NotationBody.class).getName()).thenReturn("C").getMock();
		when(mockNotationC.getNumberOfWorkingBells()).thenReturn(NumberOfBells.BELLS_6);
		NotationBody mockNotationD = when(mock(NotationBody.class).getName()).thenReturn("D").getMock();
		when(mockNotationD.getNumberOfWorkingBells()).thenReturn(NumberOfBells.BELLS_6);

		touch.addNotation(mockNotationA);
		touch.addNotation(mockNotationB);
		touch.addNotation(mockNotationC);
		touch.addNotation(mockNotationD);

		touch.setActiveNotation(mockNotationC);
		assertEquals(mockNotationC, touch.getActiveNotation());

		touch.removeNotation(mockNotationC);
		assertEquals(mockNotationD, touch.getActiveNotation());
		touch.removeNotation(mockNotationD);
		assertEquals(mockNotationA, touch.getActiveNotation());
		touch.removeNotation(mockNotationB);
		assertEquals(mockNotationA, touch.getActiveNotation());
		touch.removeNotation(mockNotationA);
		assertNull(touch.getActiveNotation());
	}

	@Test
	public void settingSplicedClearsActiveNotation() {
		DefaultTouch touch = new DefaultTouch();
		touch.setSpliced(false);

		NotationBody mockNotationA = when(mock(NotationBody.class).getName()).thenReturn("A").getMock();
		when(mockNotationA.getNumberOfWorkingBells()).thenReturn(NumberOfBells.BELLS_6);
		NotationBody mockNotationB = when(mock(NotationBody.class).getName()).thenReturn("B").getMock();
		when(mockNotationB.getNumberOfWorkingBells()).thenReturn(NumberOfBells.BELLS_6);

		touch.addNotation(mockNotationA);
		touch.addNotation(mockNotationB);

		assertNotNull(touch.getActiveNotation());
		touch.setSpliced(true);
		assertNull(touch.getActiveNotation());
	}

	@Test
	public void unsettingSplicedSetsFirstActiveNotation() {
		DefaultTouch touch = new DefaultTouch();

		NotationBody mockNotationA = when(mock(NotationBody.class).getName()).thenReturn("A").getMock();
		when(mockNotationA.getNumberOfWorkingBells()).thenReturn(NumberOfBells.BELLS_6);
		NotationBody mockNotationB = when(mock(NotationBody.class).getName()).thenReturn("B").getMock();
		when(mockNotationB.getNumberOfWorkingBells()).thenReturn(NumberOfBells.BELLS_6);
		touch.addNotation(mockNotationA);
		touch.addNotation(mockNotationB);

		touch.setSpliced(true);
		assertNull(touch.getActiveNotation());
		touch.setSpliced(false);
		assertEquals(mockNotationA, touch.getActiveNotation());
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

		assertEquals(1, touch.getCell_FOR_TEST_ONLY(0,0).getLength());
		touch.removeCharacter(0,0,0);
		assertEquals(0, touch.getCell_FOR_TEST_ONLY(0,0).getLength());
	}

	@Test
	public void canCorrectlyRetrieveCharacters() {
		DefaultTouch touch = new DefaultTouch();
		touch.insertCharacter(0, 0, 0, 'F');
		assertEquals('F', touch.getElement(0, 0, 0).getCharacter());
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
		assertEquals(TouchType.COURSE_BASED, touch.getTouchType());
		touch.setTouchType(TouchType.LEAD_BASED);
		assertEquals(TouchType.LEAD_BASED, touch.getTouchType());
	}

	@Test
	public void canAddAndRemoveDefinitions() {
		DefaultTouch touch = new DefaultTouch();
		TouchDefinition a = touch.addDefinition("a", "p-p");
		assertEquals("a", Iterators.getOnlyElement(touch.getDefinitions().iterator()).getName());
		touch.addDefinition("b", "-p-");
		assertEquals(2, touch.getDefinitions().size());
		touch.removeDefinition(a.getName());
		assertEquals("b", Iterators.getOnlyElement(touch.getDefinitions().iterator()).getName());
	}

	@Test(expected = IllegalArgumentException.class)
	public void addingDuplicateDefinitionThrows() {
		DefaultTouch touch = new DefaultTouch();
		touch.addDefinition("a", "p-p");
		touch.addDefinition("a", "sp-");
	}

	@Test
	public void cloneTest() throws CloneNotSupportedException {
		DefaultTouch touch = new DefaultTouch();
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
		touch.setTerminationSpecificRow(MethodBuilder.buildRoundsRow(NumberOfBells.BELLS_6));
		new DefaultParser().parseAndAnnotate(touch);

		Touch clone = touch.clone();
		String cloneToString = clone.toString();
		Assert.assertEquals(touch.toString(), cloneToString);

		/// check that changing the original does not change the clone.
		touch.resetParseData();
		assertEquals(cloneToString, clone.toString());
	}

	private NotationBody buildPlainBobMinor() {

		return NotationBuilder.getInstance()
				.setNumberOfWorkingBells(NumberOfBells.BELLS_6)
				.setName("Plain Bob")
				.setFoldedPalindromeNotationShorthand("x16x", "16")
				.addCall("Bob", "-", "14", true)
				.addCall("Single", "s", "1234", false)
				.setSpliceIdentifier("p")
				.build();
	}

}