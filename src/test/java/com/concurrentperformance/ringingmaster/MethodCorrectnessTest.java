package com.concurrentperformance.ringingmaster;

import com.concurrentperformance.ringingmaster.engine.NumberOfBells;
import com.concurrentperformance.ringingmaster.engine.helper.PlainCourseHelper;
import com.concurrentperformance.ringingmaster.engine.method.MethodLead;
import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import com.concurrentperformance.ringingmaster.engine.notation.impl.LeadHeadCalculator;
import com.concurrentperformance.ringingmaster.engine.notation.impl.NotationBuilder;
import com.concurrentperformance.ringingmaster.engine.touch.proof.Proof;
import com.concurrentperformance.ringingmaster.generated.persist.Notation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Collection;

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

//TODO		return new CentralCouncilXmlLibraryNotationExtractor()
//				.extractNotationLibraryToStream()
//				.map(serializableNotation -> new Object[]{serializableNotation})
//				.collect(Collectors.toList());

		return null;
	}

	public MethodCorrectnessTest(Notation serializableNotation) {
		this.notation = serializableNotation;
		this.leadHead = LeadHeadCalculator.lookupRowFromCode(serializableNotation.getLeadHead(), NumberOfBells.valueOf(serializableNotation.getNumberOfBells()));
	}

	private final Notation notation;
	private final String leadHead;

	@Test
	public void checkCalculatedMethodLength() {

		NotationBody notationBody = NotationBuilder.getInstance()
				.setFromSerializableNotation(notation)
				.build();

		Proof proof = PlainCourseHelper.buildPlainCourse(notationBody, "", false);
		MethodLead lead = proof.getCreatedMethod().get().getLead(0);
	//	log.info(lead.toString());

		assertEquals(notation.getLeadLength(), lead.getRowCount() - 1);
		assertEquals(leadHead, lead.getLastRow().getDisplayString(false));


	}
}
