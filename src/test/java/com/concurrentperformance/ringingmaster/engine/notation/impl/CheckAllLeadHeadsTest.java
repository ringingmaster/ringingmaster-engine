package com.concurrentperformance.ringingmaster.engine.notation.impl;


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

@RunWith(Parameterized.class)
public class CheckAllLeadHeadsTest {

	private final Logger log = LoggerFactory.getLogger(this.getClass());


	@Parameterized.Parameters
	public static Collection<Object[]> checkAllCCLibrary() {
		return new CentralCouncilMethodExtractor()
				.extractNotationsToStream()
//				.filter(serializableNotation -> serializableNotation.getNumberOfBells() == 8)
//				.filter(serializableNotation -> serializableNotation.getName().contains("Chipstead Slow Course"))
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

		String ccLeadHead = serializableNotation.getLeadHead();
		String calculatedLeadHead = notationBody.getLeadHeadCode();

		// Uncomment section to log out the changes in the lead
//		log.warn(notationBody.toString());
//		log.warn(PlainCourseHelper.buildPlainCourse(notationBody, "TEST", false).getCreatedMethod().getLead(0).toString());

		assertEquals("[" + notationBody.getNumberOfWorkingBells().getBellCount() + "] " + notationBody.getNameIncludingNumberOfBells() +
						"[" + ccLeadHead + "](library) vs [" + calculatedLeadHead + "](calculated) NOT OK: " + notationBody.toString(),
				ccLeadHead, calculatedLeadHead);

	}
}