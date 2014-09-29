package com.concurrentperformance.ringingmaster.engine;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.concurrentperformance.ringingmaster.engine.method.Bell;

public class NumberOfBellsTest {

	@Test
	public void iterstorFunctions() {
		int i=0;
		for(final Bell bell : NumberOfBells.BELLS_12) {
			assertEquals(i,bell.getZeroBasedBell());
			i++;
		}
		assertEquals(i, 12);
	}

}
