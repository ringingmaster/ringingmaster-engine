package org.ringingmaster.engine.touch.container.impl;

import org.junit.Assert;
import org.junit.Test;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.method.impl.MethodBuilder;
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.notation.impl.NotationBuilder;
import org.ringingmaster.engine.touch.container.Touch;
import org.ringingmaster.engine.touch.container.TouchCheckingType;
import org.ringingmaster.engine.touch.newcontainer.variance.VarianceLogicType;
import org.ringingmaster.engine.touch.newcontainer.variance.impl.OddEvenVariance;
import org.ringingmaster.engine.touch.parser.impl.DefaultParser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * User: Stephen
 */
public class DefaultTouchTest {
	

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


}
