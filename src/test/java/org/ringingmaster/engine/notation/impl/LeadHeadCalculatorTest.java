package org.ringingmaster.engine.notation.impl;


import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.method.Row;
import org.ringingmaster.engine.method.MethodBuilder;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LeadHeadCalculatorTest  {


	@Test
	public void lookupLeadHeadFunctions() {
		Row row = MethodBuilder.parse(NumberOfBells.BELLS_9, "124638597");
		assertEquals("124638597", row.getDisplayString(false));
		assertEquals("f", LeadHeadCalculator.lookupLeadHeadCode(row, LeadHeadCalculator.LeadHeadType.NEAR));
		assertEquals("m", LeadHeadCalculator.lookupLeadHeadCode(row, LeadHeadCalculator.LeadHeadType.EXTREME));
	}

	@Test
	public void lookupRowFromLeadHeadCodeFunctions() {
		assertEquals("124638597", LeadHeadCalculator.lookupRowFromCode("f", NumberOfBells.BELLS_9));
		assertEquals("124638597", LeadHeadCalculator.lookupRowFromCode("m", NumberOfBells.BELLS_9));
	}

}