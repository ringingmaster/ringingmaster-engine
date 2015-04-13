package com.concurrentperformance.ringingmaster.engine.notation.impl;


import com.concurrentperformance.ringingmaster.engine.NumberOfBells;
import com.concurrentperformance.ringingmaster.engine.method.MethodRow;
import com.concurrentperformance.ringingmaster.engine.method.impl.MethodBuilder;
import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import com.concurrentperformance.ringingmaster.generated.notation.persist.SerializableNotation;
import com.ringingmaster.extraction.CentralCouncilMethodExtractor;
import com.ringingmaster.extraction.MethodExtractor;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LeadHeadCalculatorTest  {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private final MethodExtractor methodExtractor = new CentralCouncilMethodExtractor();

	@Test
	public void checkAllCCLibrary() {
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
		NumberOfBells ccNumberOfBells = NumberOfBells.valueOf(serializableNotation.getStage());
		boolean ccIsFoldedPalindrome = serializableNotation.isFoldedPalindrome();
		String ccNotation = serializableNotation.getNotation();
		String ccNotation2 = serializableNotation.getNotation2();
		String ccLeadHead = serializableNotation.getLeadHead();

		NotationBuilder notationBuilder = NotationBuilder.getInstance();
		notationBuilder.setNumberOfWorkingBells(ccNumberOfBells);
		if (!ccIsFoldedPalindrome) {
			notationBuilder.setUnfoldedNotationShorthand(ccNotation);
		} else {
			notationBuilder.setFoldedPalindromeNotationShorthand(ccNotation, ccNotation2);
		}
		notationBuilder.setName(ccName);

		NotationBody notationBody = notationBuilder.build();

		// Uncomment section to log out the changes in the lead
//		Proof proof = PlainCourseHelper.buildPlainCourse(notationBody, "TEST", false);
//		MethodLead lead = proof.getCreatedMethod().getLead(0);
//		log.warn(lead.toString());

		// TODO assertEquals(notationBody.getNameIncludingNumberOfBells() + " Lead Head Code", ccLeadHead, notationBody.getLeadHeadCode());

		LeadHeadCalculator.LeadHeadCodeType leadHeadType = LeadHeadCalculator.getLeadHeadType(ccLeadHead, ccNumberOfBells);
		assertTrue(leadHeadType != LeadHeadCalculator.LeadHeadCodeType.INVALID_LEADHEAD);

		if (leadHeadType == LeadHeadCalculator.LeadHeadCodeType.VALID_LEADHEAD_ROW) {
			// These rows are where it is not a plain bob lead head, and therefore no code is in the cc library.
			String lookupRow = LeadHeadCalculator.lookupRow(notationBody.getLeadHeadCode(), notationBody.getNumberOfWorkingBells());
			if (!ccLeadHead.equals(lookupRow)) {
				log.warn("[{}] {} [{}](calculated) vs [{}](library) NOT OK", notationBody.getNumberOfWorkingBells().getBellCount(),
						notationBody.getNameIncludingNumberOfBells(), lookupRow, ccLeadHead);
				return true;
			}
			else {
				return false;
			}
		}


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