package com.concurrentperformance.ringingmaster.engine.notation.impl;

import com.concurrentperformance.ringingmaster.engine.method.MethodLead;
import com.concurrentperformance.ringingmaster.engine.method.MethodRow;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class LeadHeadCalculator {

	public static String calculateLeadHeadCode(MethodLead plainLead) {
		MethodRow lastRow = plainLead.getLastRow();
		return lastRow.getDisplayString(false);
	}

}
