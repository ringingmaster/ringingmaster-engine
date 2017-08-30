package org.ringingmaster.engine.touch.container.impl;

import org.junit.Assert;
import org.junit.Test;
import org.ringingmaster.engine.touch.container.Touch;
import org.ringingmaster.engine.touch.newcontainer.checkingtype.CheckingType;

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
		Assert.assertEquals(CheckingType.COURSE_BASED, touch.getCheckingType());
		touch.setTouchCheckingType(CheckingType.LEAD_BASED);
		Assert.assertEquals(CheckingType.LEAD_BASED, touch.getCheckingType());
	}

}
