package org.ringingmaster.engine.compilerold.impl;

import org.ringingmaster.engine.compiler.leadbased.LeadBasedDenormalisedCall;
import org.ringingmaster.engine.compiler.variance.Variance;
import org.ringingmaster.engine.parser.assignparsetype.ParseType;
import org.ringingmaster.engine.composition.Composition;

import javax.annotation.concurrent.NotThreadSafe;

/**
 * Take a composition, and decompose into a List of {@link LeadBasedDenormalisedCall}'s
 * This removes all the multipliers and replaces them with actual calls.
 *
 * User: Stephen
 */
@NotThreadSafe
public class LeadBasedCallDecomposer extends SkeletalCallDecomposer {

	public LeadBasedCallDecomposer(Composition composition, String logPreamble) {
		super(composition, logPreamble);
	}

	protected void preGenerate(Composition composition) {
		// Do nothing
	}

	protected LeadBasedDenormalisedCall buildDecomposedCall(String callName, Variance variance, int columnIndex, ParseType parseType) {
		return null;//LeadBasedDenormalisedCall(callName, variance, parseType);
	}


}
