package com.concurrentperformance.ringingmaster.engine.touch.proof;

import net.jcip.annotations.Immutable;

import com.concurrentperformance.ringingmaster.engine.touch.analysis.Analysis;
import com.concurrentperformance.ringingmaster.engine.method.Method;
import com.concurrentperformance.ringingmaster.engine.touch.container.Touch;

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

	String getTerminateReasonDisplayString();

	Analysis getAnalysis();

	long getProofTime();
}
