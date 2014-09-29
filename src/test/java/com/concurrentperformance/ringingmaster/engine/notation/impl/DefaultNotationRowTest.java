package com.concurrentperformance.ringingmaster.engine.notation.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.concurrentperformance.ringingmaster.engine.notation.NotationPlace;
import com.concurrentperformance.ringingmaster.engine.notation.NotationRow;

public class DefaultNotationRowTest {


	@Test
	public void makesPlace() {

		final Set<NotationPlace> elements = new HashSet<NotationPlace>();
		elements.add(NotationPlace.PLACE_1);
		elements.add(NotationPlace.PLACE_8);
		final NotationRow notationRow = new DefaultNotationRow(elements);

		assertTrue("Should make place 1", notationRow.makesPlace(0));
		assertFalse("Should not make place 2", notationRow.makesPlace(1));
		assertFalse("Should not make place 3", notationRow.makesPlace(2));
		assertFalse("Should not make place 4", notationRow.makesPlace(3));
		assertFalse("Should not make place 5", notationRow.makesPlace(4));
		assertFalse("Should not make place 6", notationRow.makesPlace(5));
		assertFalse("Should not make place 7", notationRow.makesPlace(6));
		assertTrue("Should make place 8", notationRow.makesPlace(7));

	}
}
