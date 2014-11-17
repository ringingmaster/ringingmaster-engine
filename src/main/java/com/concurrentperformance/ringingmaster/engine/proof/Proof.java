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

	/**
	 * A **COPY** of the {@link Touch} as it was when the
	 * proof was requested.
	 */
	Touch getTouch();

	Method getCreatedMethod();

	ProofTerminationReason getTerminationReason();

	Analysis getAnalysis();

	long getProofTime();
}
