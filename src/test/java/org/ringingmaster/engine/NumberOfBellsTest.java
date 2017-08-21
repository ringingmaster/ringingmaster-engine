package org.ringingmaster.engine;

import org.ringingmaster.engine.notation.NotationPlace;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NumberOfBellsTest {

	@Test
	public void iteratorFunctions() {
		int i=0;
		for(final NotationPlace notationPlace : NumberOfBells.BELLS_30) {
			assertEquals(notationPlace, NotationPlace.valueOf(i));
			i++;
		}
		assertEquals(i, 30);
	}

}