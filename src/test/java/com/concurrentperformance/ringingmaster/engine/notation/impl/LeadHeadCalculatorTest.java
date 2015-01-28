package com.concurrentperformance.ringingmaster.engine.notation.impl;


import com.concurrentperformance.ringingmaster.engine.NumberOfBells;
import com.concurrentperformance.ringingmaster.engine.method.MethodRow;
import com.concurrentperformance.ringingmaster.engine.method.impl.MethodBuilder;
import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import com.concurrentperformance.ringingmaster.generated.notation.persist.SerializableNotation;
import com.concurrentperformance.ringingmaster.generated.notation.persist.SerializableNotationList;
import com.google.common.base.Strings;
import com.ringingmaster.extraction.CentralCouncilMethodExtractor;
import com.ringingmaster.extraction.MethodExtractor;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;

public class LeadHeadCalculatorTest  {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private final MethodExtractor methodExtractor = new CentralCouncilMethodExtractor();

	@Test
	public void checkAllCCLibrary() {

		SerializableNotationList ccLib = methodExtractor.extractNotations();
		for (SerializableNotation serializableNotation : ccLib.getSerializableNotation()) {
			log.info(serializableNotation.toString());

			String name = serializableNotation.getName();
			int stage = serializableNotation.getStage();
			String notation = serializableNotation.getNotation();
			String leadEnd = serializableNotation.getLeadEnd();
			String leadHead = serializableNotation.getLeadHead();

			NotationBuilder notationBuilder = NotationBuilder.getInstance();
			notationBuilder.setNumberOfWorkingBells(NumberOfBells.valueOf(stage));
			if (Strings.isNullOrEmpty(leadEnd)) {
				notationBuilder.setUnfoldedNotationShorthand(notation);
			} else {
				notationBuilder.setFoldedPalindromeNotationShorthand(notation, leadEnd);
			}
			notationBuilder.setName(name);

			NotationBody notationBody = notationBuilder.build();

			assertEquals(notationBody.getNameIncludingNumberOfBells() + " Lead Head Code", leadHead, notationBody.getLeadHeadCode());

			log.warn(notationBody.getNameIncludingNumberOfBells() +  " " + notationBody.getLeadHeadCode() + " OK");
		}
	}

	@Test
	public void lookupLeadHeadFunctions() {
		MethodRow row = MethodBuilder.parse(NumberOfBells.BELLS_9, "124638597");
		assertEquals("124638597", row.getDisplayString(false));
		assertEquals("f", LeadHeadCalculator.lookupLeadHeadCode(row, LeadHeadCalculator.LeadHeadType.NEAR));
		assertEquals("m", LeadHeadCalculator.lookupLeadHeadCode(row, LeadHeadCalculator.LeadHeadType.FAR));
	}
}