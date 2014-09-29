package com.concurrentperformance.ringingmaster.engine.method.impl;

import org.junit.Test;

import com.concurrentperformance.ringingmaster.engine.NumberOfBells;
import com.concurrentperformance.ringingmaster.engine.method.MethodRow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MethodBuilderTest {

	@Test
	public void canBuildRoundsRow() {
		MethodRow roundsOnEight = MethodBuilder.buildRoundsRow(NumberOfBells.BELLS_12);
		assertNotNull("build rounds should allways return a valid object", roundsOnEight);
		checkRow("1234567890ET", roundsOnEight);
	}


	private void checkRow(String sequence, MethodRow row) {
		String rowAsString = row.getDisplayString();
        assertEquals("row should equal sequence", sequence, rowAsString);
	}

}