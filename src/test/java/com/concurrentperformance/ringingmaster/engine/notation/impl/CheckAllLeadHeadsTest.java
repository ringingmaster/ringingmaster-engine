package com.concurrentperformance.ringingmaster.engine.notation.impl;


import com.concurrentperformance.ringingmaster.engine.NumberOfBells;
import com.concurrentperformance.ringingmaster.engine.helper.PlainCourseHelper;
import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import com.concurrentperformance.ringingmaster.generated.notation.persist.SerializableNotation;
import com.ringingmaster.extraction.CentralCouncilMethodExtractor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class CheckAllLeadHeadsTest {

	private final Logger log = LoggerFactory.getLogger(this.getClass());


	@Parameterized.Parameters
	public static Collection<Object[]> checkAllCCLibrary() {
		return new CentralCouncilMethodExtractor()
				.extractNotationsToStream()
				.filter(serializableNotation -> serializableNotation.getStage() == 5)
				.filter(serializableNotation -> serializableNotation.getName().startsWith("Grandsire") ||
												serializableNotation.getName().contains("Breastosn"))
				.map(serializableNotation -> new Object[]{serializableNotation})
				.collect(Collectors.toList());
	}

	public CheckAllLeadHeadsTest(SerializableNotation serializableNotation) {
		this.serializableNotation = serializableNotation;
	}

	private final SerializableNotation serializableNotation;

	@Test
	public void checkLeadHeadCorrectness() {

		log.info(serializableNotation.toString());

		NotationBody notationBody = NotationBuilder.getInstance()
				.setFromSerializableNotation(serializableNotation)
				.build();

		NumberOfBells ccNumberOfBells = NumberOfBells.valueOf(serializableNotation.getStage());
		String ccLeadHead = serializableNotation.getLeadHead();


		// Uncomment section to log out the changes in the lead
		log.warn(notationBody.toString());
		log.warn(PlainCourseHelper.buildPlainCourse(notationBody, "TEST", false).getCreatedMethod().getLead(0).toString());

		// TODO assertEquals(notationBody.getNameIncludingNumberOfBells() + " Lead Head Code", ccLeadHead, notationBody.getLeadHeadCode());


		LeadHeadCalculator.LeadHeadCodeType leadHeadType = LeadHeadCalculator.getLeadHeadType(ccLeadHead, ccNumberOfBells);
		//TODO need to exclude non plain bob lead heads from calculating a code.
		// These rows are where it is not a plain bob lead head, and therefore no code is in the cc library.
		if (leadHeadType == LeadHeadCalculator.LeadHeadCodeType.VALID_LEADHEAD_ROW) {
			String lookupRow = LeadHeadCalculator.lookupRow(notationBody.getLeadHeadCode(), notationBody.getNumberOfWorkingBells());
			assertEquals("[" + notationBody.getNumberOfWorkingBells().getBellCount() + "] " + notationBody.getNameIncludingNumberOfBells() +
						" [" + lookupRow + "](calculated) vs [" + ccLeadHead + "](library) NOT OK",
					ccLeadHead, lookupRow);
		}
		else if (leadHeadType == LeadHeadCalculator.LeadHeadCodeType.VALID_LEADHEAD_CODE){
			assertEquals("[" + notationBody.getNumberOfWorkingBells().getBellCount() + "] " + notationBody.getNameIncludingNumberOfBells() +
							" [" + notationBody.getLeadHeadCode() + "](calculated) vs [" + ccLeadHead + "](library) NOT OK",
					ccLeadHead, notationBody.getLeadHeadCode());
		}
		else {
			fail();
		}
	}
}