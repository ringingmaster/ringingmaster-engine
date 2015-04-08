package com.concurrentperformance.ringingmaster.engine.notation.impl;


import com.concurrentperformance.ringingmaster.engine.NumberOfBells;
import com.concurrentperformance.ringingmaster.engine.helper.PlainCourseHelper;
import com.concurrentperformance.ringingmaster.engine.method.MethodLead;
import com.concurrentperformance.ringingmaster.engine.method.MethodRow;
import com.concurrentperformance.ringingmaster.engine.method.impl.MethodBuilder;
import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import com.concurrentperformance.ringingmaster.engine.touch.proof.Proof;
import com.concurrentperformance.ringingmaster.generated.notation.persist.SerializableNotation;
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

		int numberOK = 0;
		int numberNOTOK = 0;

		long count = methodExtractor
				.extractNotationsToStream()
//				.filter(serializableNotation -> serializableNotation.getName().startsWith("Cheeky Little Place"))
//				.filter(serializableNotation -> serializableNotation.getStage() == 5)
			    .filter(this::calculatedLeadHeadNotEqualsToSuppliedLH)
//				.peek(serializableNotation -> log.warn(serializableNotation.getName() + " " + serializableNotation.getStage()))
				.count();

		log.warn("[{}] methods calculated different to supplied LeadHead", count);

	}


	boolean calculatedLeadHeadNotEqualsToSuppliedLH(SerializableNotation serializableNotation) {

		log.info(serializableNotation.toString());

		String ccName = serializableNotation.getName();
		int ccStage = serializableNotation.getStage();
		boolean ccIsFoldedPalindrome = serializableNotation.isFoldedPalindrome();
		String ccNotation = serializableNotation.getNotation();
		String ccNotation2 = serializableNotation.getNotation2();
		String ccLeadHead = serializableNotation.getLeadHead();

		NotationBuilder notationBuilder = NotationBuilder.getInstance();
		notationBuilder.setNumberOfWorkingBells(NumberOfBells.valueOf(ccStage));
		if (!ccIsFoldedPalindrome) {
			notationBuilder.setUnfoldedNotationShorthand(ccNotation);
		} else {
			notationBuilder.setFoldedPalindromeNotationShorthand(ccNotation, ccNotation2);
		}
		notationBuilder.setName(ccName);

		NotationBody notationBody = notationBuilder.build();

		Proof proof = PlainCourseHelper.buildPlainCourse(notationBody, "TEST", false);
		MethodLead lead = proof.getCreatedMethod().getLead(0);
		//log.warn(lead.toString());

		//assertEquals(notationBody.getNameIncludingNumberOfBells() + " Lead Head Code", ccLeadHead, notationBody.getLeadHeadCode());

		if (!ccLeadHead.equals(notationBody.getLeadHeadCode())) {
			log.warn("[{}] {} [{}](calculated) vs [{}](library) NOT OK", notationBody.getNumberOfWorkingBells().getBellCount(),
					notationBody.getNameIncludingNumberOfBells(), notationBody.getLeadHeadCode(), ccLeadHead);
			return true;
		} else {
			return false;
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