package com.concurrentperformance.ringingmaster.engine.touch.compiler.impl;

import net.jcip.annotations.NotThreadSafe;

import com.concurrentperformance.ringingmaster.engine.touch.parser.ParseType;
import com.concurrentperformance.ringingmaster.engine.touch.container.Touch;
import com.concurrentperformance.ringingmaster.engine.touch.container.Variance;

/**
 * Take a touch, and decompose into a List of {@link LeadBasedDecomposedCall}'s
 * This removes all the multipliers and replaces them with actual calls.
 *
 * User: Stephen
 */
@NotThreadSafe
public class LeadBasedCallDecomposer extends SkeletalCallDecomposer<LeadBasedDecomposedCall> {

	public LeadBasedCallDecomposer(Touch touch, String logPreamble) {
		super(touch, logPreamble);
	}

	protected void preGenerate(Touch touch) {
		// Do nothing
	}

	protected LeadBasedDecomposedCall buildDecomposedCall(String callName, Variance variance, int columnIndex, ParseType parseType) {
		return new LeadBasedDecomposedCall(callName, variance, parseType);
	}


}