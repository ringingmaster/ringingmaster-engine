package com.concurrentperformance.ringingmaster;

import com.concurrentperformance.ringingmaster.engine.NumberOfBells;
import com.concurrentperformance.ringingmaster.engine.helper.PlainCourseHelper;
import com.concurrentperformance.ringingmaster.engine.method.MethodLead;
import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import com.concurrentperformance.ringingmaster.engine.notation.impl.LeadHeadCalculator;
import com.concurrentperformance.ringingmaster.engine.notation.impl.NotationBuilder;
import com.concurrentperformance.ringingmaster.engine.touch.proof.Proof;
import com.concurrentperformance.ringingmaster.generated.notation.persist.SerializableNotation;
import com.ringingmaster.extraction.CentralCouncilMethodExtractor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Collection;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

/**
 * TODO Comments
 *
 * @author Lake
 */
@RunWith(Parameterized.class)
public class MethodCorrectnessTest   {


	@Parameters
	public static Collection<Object[]> checkAllCCLibrary() {

		return new CentralCouncilMethodExtractor()
				.extractNotationsToStream()
				.map(serializableNotation -> new Object[]{serializableNotation})
				.collect(Collectors.toList());
	}

	public MethodCorrectnessTest(SerializableNotation serializableNotation) {
		this.serializableNotation = serializableNotation;
		this.leadHead = LeadHeadCalculator.lookupRowFromCode(serializableNotation.getLeadHead(), NumberOfBells.valueOf(serializableNotation.getNumberOfBells()));
	}

	private final SerializableNotation serializableNotation;
	private final String leadHead;

	@Test
	public void checkCalculatedMethodLength() {

		NotationBody notationBody = NotationBuilder.getInstance()
				.setFromSerializableNotation(serializableNotation)
				.build();

		Proof proof = PlainCourseHelper.buildPlainCourse(notationBody, "", false);
		MethodLead lead = proof.getCreatedMethod().getLead(0);
	//	log.info(lead.toString());

		assertEquals(serializableNotation.getLeadLength(), lead.getRowCount() - 1);
		assertEquals(leadHead, lead.getLastRow().getDisplayString(false));


	}
}
