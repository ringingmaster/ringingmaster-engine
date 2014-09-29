package com.concurrentperformance.ringingmaster.engine.proof;

import net.jcip.annotations.Immutable;

import com.concurrentperformance.ringingmaster.engine.analysis.Analysis;
import com.concurrentperformance.ringingmaster.engine.method.Method;
import com.concurrentperformance.ringingmaster.engine.touch.Touch;

/**
 * TODO comments.
 *
 * @author stephen
 */
@Immutable
public interface Proof {

	/** Get the COPY of the {@link Touch} as it was when the
	 * proof was requested.
	 */
	Touch getTouch();

	/**
	 * Get the created method.
	 */
	Method getMethod();

	/**
	 * Get the reason for no more rows being created.
	 */
	ProofTerminationReason getTerminationReason();

	/**
	 * Get the analysis of the created touch.
	 */
	Analysis getAnalysis();
}
