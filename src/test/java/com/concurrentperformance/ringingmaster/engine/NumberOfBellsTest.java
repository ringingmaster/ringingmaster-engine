package com.concurrentperformance.ringingmaster.engine;

import com.concurrentperformance.ringingmaster.engine.notation.NotationPlace;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NumberOfBellsTest {

	@Test
	public void iteratorFunctions() {
		int i=0;
		for(final NotationPlace notationPlace : NumberOfBells.BELLS_12) {
			assertEquals(notationPlace, NotationPlace.valueOf(i));
			i++;
		}
		assertEquals(i, 12);
	}

}
