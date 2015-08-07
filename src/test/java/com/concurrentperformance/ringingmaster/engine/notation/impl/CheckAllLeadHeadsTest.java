package com.concurrentperformance.ringingmaster.engine.notation.impl;


import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import com.concurrentperformance.ringingmaster.generated.persist.Notation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class CheckAllLeadHeadsTest {

	private final Logger log = LoggerFactory.getLogger(this.getClass());


	@Parameterized.Parameters
	public static Collection<Object[]> checkAllCCLibrary() {
//TODO		return new CentralCouncilXmlLibraryNotationExtractor()
//				.extractNotationLibraryToStream()
//				.map(notation -> new Object[]{notation})
//				.collect(Collectors.toList());
		return null;
	}

	public CheckAllLeadHeadsTest(Notation notation) {
		this.notation = notation;
	}

	private final Notation notation;

	@Test
	public void checkLeadHeadCorrectness() {

		log.info(notation.toString());

		NotationBody notationBody = NotationBuilder.getInstance()
				.setFromSerializableNotation(notation)
				.build();

		String ccLeadHead = notation.getLeadHead();
		String calculatedLeadHead = notationBody.getLeadHeadCode();

		// Uncomment section to log out the changes in the lead
//		log.warn(notationBody.toString());
//		log.warn(PlainCourseHelper.buildPlainCourse(notationBody, "TEST", false).getCreatedMethod().getLead(0).toString());

		assertEquals("[" + notationBody.getNumberOfWorkingBells().getBellCount() + "] " + notationBody.getNameIncludingNumberOfBells() +
						"[" + ccLeadHead + "](library) vs [" + calculatedLeadHead + "](calculated) NOT OK: " + notationBody.toString(),
				ccLeadHead, calculatedLeadHead);

	}
}