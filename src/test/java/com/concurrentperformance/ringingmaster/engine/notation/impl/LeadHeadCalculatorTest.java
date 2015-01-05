package com.concurrentperformance.ringingmaster.engine.notation.impl;


import com.concurrentperformance.ringingmaster.engine.NumberOfBells;
import com.concurrentperformance.ringingmaster.engine.helper.PlainCourseHelper;
import com.concurrentperformance.ringingmaster.engine.method.Method;
import com.concurrentperformance.ringingmaster.engine.method.MethodLead;
import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import com.concurrentperformance.ringingmaster.engine.notation.NotationPlace;
import com.concurrentperformance.ringingmaster.engine.notation.NotationRow;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class LeadHeadCalculatorTest  {


	@Test
	public void createLeadHead() {
		Method plainCourse = buildPlainCourse();
		MethodLead plainLead = plainCourse.getLead(0);
		String leadHeadCode = LeadHeadCalculator.calculateLeadHeadCode(plainLead);
		assertEquals("a", leadHeadCode);
	}

	private Method buildPlainCourse() {
		List<NotationRow> normalisedNotationElements = Lists.newArrayList(
				NotationRowHelper.buildNotationRow(NotationPlace.ALL_CHANGE),
				NotationRowHelper.buildNotationRow(NotationPlace.PLACE_1,NotationPlace.PLACE_2));

		NotationBody plainCourseNotation = new PlainCourseNotationBody("TEST", NumberOfBells.BELLS_8, normalisedNotationElements);
		return PlainCourseHelper.buildPlainCourse(plainCourseNotation, "[LeadHeadCalculatorTest] ", false).getCreatedMethod();
	}


}