package org.ringingmaster.engine.notation.impl;

import org.ringingmaster.engine.notation.NotationPlace;
import org.ringingmaster.engine.notation.NotationRow;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DefaultNotationRowTest {


	@Test
	public void makesPlace() {

		final Set<NotationPlace> elements = new HashSet<NotationPlace>();
		elements.add(NotationPlace.PLACE_1);
		elements.add(NotationPlace.PLACE_8);
		final NotationRow notationRow = new DefaultNotationRow(elements);

		assertTrue("Should make place 1", notationRow.makesPlace(NotationPlace.PLACE_1));
		assertFalse("Should not make place 2", notationRow.makesPlace(NotationPlace.PLACE_2));
		assertFalse("Should not make place 3", notationRow.makesPlace(NotationPlace.PLACE_3));
		assertFalse("Should not make place 4", notationRow.makesPlace(NotationPlace.PLACE_4));
		assertFalse("Should not make place 5", notationRow.makesPlace(NotationPlace.PLACE_5));
		assertFalse("Should not make place 6", notationRow.makesPlace(NotationPlace.PLACE_6));
		assertFalse("Should not make place 7", notationRow.makesPlace(NotationPlace.PLACE_7));
		assertTrue("Should make place 8", notationRow.makesPlace(NotationPlace.PLACE_8));

	}
}
