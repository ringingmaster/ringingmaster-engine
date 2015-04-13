package com.concurrentperformance.ringingmaster;

import com.concurrentperformance.ringingmaster.engine.NumberOfBells;
import com.concurrentperformance.ringingmaster.engine.helper.PlainCourseHelper;
import com.concurrentperformance.ringingmaster.engine.method.MethodLead;
import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import com.concurrentperformance.ringingmaster.engine.notation.impl.LeadHeadCalculator;
import com.concurrentperformance.ringingmaster.engine.notation.impl.NotationBuilder;
import com.concurrentperformance.ringingmaster.engine.touch.proof.Proof;
import com.ringingmaster.extraction.CentralCouncilMethodExtractor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * TODO Comments
 *
 * @author Lake
 */
@RunWith(Parameterized.class)
public class MethodCorrectnessTest   {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

//	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][]{
				{"Grandsire", 5, true, "3", "1.5.1.5.1", 10, "a"},
		});
	}

	@Parameters
	public static Collection<Object[]> checkAllCCLibrary() {

		return new CentralCouncilMethodExtractor()
				.extractNotationsToStream()
				.map(serializableNotation -> new Object[]{serializableNotation.getName(), serializableNotation.getStage(),
															serializableNotation.isFoldedPalindrome(), serializableNotation.getNotation(), serializableNotation.getNotation2(),
															serializableNotation.getLeadLength(), serializableNotation.getLeadHead()})
				.collect(Collectors.toList());
	}

	public MethodCorrectnessTest(String name, int stage, boolean isFoldedPalindrome,
	                             String notation, String notation2, int leadLength, String leadHeadCode) {
		this.name = name;
		this.numberOfBells = NumberOfBells.valueOf(stage);
		this.isFoldedPalindrome = isFoldedPalindrome;
		this.notation = notation;
		this.notation2 = notation2;
		this.leadLength = leadLength;
		this.leadHead = LeadHeadCalculator.lookupRow(leadHeadCode, numberOfBells);
		assertTrue( " Method: " + name + " " + numberOfBells + " - " + leadHead + " " + leadHeadCode, (LeadHeadCalculator.getLeadHeadType(leadHead, numberOfBells) == LeadHeadCalculator.LeadHeadCodeType.VALID_LEADHEAD_ROW));
	}

	final String name;
	final NumberOfBells numberOfBells;
	final boolean isFoldedPalindrome;
	final String notation;
	final String notation2;
	final int leadLength;
	final String leadHead;


	@Test
	public void checkCalculatedMethodLength() {

		NotationBuilder notationBuilder = NotationBuilder.getInstance();
		notationBuilder.setNumberOfWorkingBells(numberOfBells);
		if (!isFoldedPalindrome) {
			notationBuilder.setUnfoldedNotationShorthand(notation);
		} else {
			notationBuilder.setFoldedPalindromeNotationShorthand(notation, notation2);
		}
		notationBuilder.setName(name);

		NotationBody notationBody = notationBuilder.build();

		Proof proof = PlainCourseHelper.buildPlainCourse(notationBody, name, false);
		MethodLead lead = proof.getCreatedMethod().getLead(0);

	//	log.info(lead.toString());

		assertEquals(leadLength, lead.getRowCount() - 1);
		assertEquals(leadHead, lead.getLastRow().getDisplayString(false));


	}
}
