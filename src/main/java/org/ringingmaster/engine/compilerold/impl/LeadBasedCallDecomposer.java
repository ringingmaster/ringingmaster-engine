package org.ringingmaster.engine.compilerold.impl;

import org.ringingmaster.engine.compiler.variance.Variance;
import org.ringingmaster.engine.parser.assignparsetype.ParseType;
import org.ringingmaster.engine.composition.Composition;

import javax.annotation.concurrent.NotThreadSafe;

/**
 * Take a composition, and decompose into a List of {@link LeadBasedDecomposedCall}'s
 * This removes all the multipliers and replaces them with actual calls.
 *
 * User: Stephen
 */
@NotThreadSafe
public class LeadBasedCallDecomposer extends SkeletalCallDecomposer<LeadBasedDecomposedCall> {

	public LeadBasedCallDecomposer(Composition composition, String logPreamble) {
		super(composition, logPreamble);
	}

	protected void preGenerate(Composition composition) {
		// Do nothing
	}

	protected LeadBasedDecomposedCall buildDecomposedCall(String callName, Variance variance, int columnIndex, ParseType parseType) {
		return new LeadBasedDecomposedCall(callName, variance, parseType);
	}


}
