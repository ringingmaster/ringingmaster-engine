package com.concurrentperformance.ringingmaster.engine.touch.proof;

import com.concurrentperformance.ringingmaster.engine.method.Method;
import com.concurrentperformance.ringingmaster.engine.touch.analysis.Analysis;
import com.concurrentperformance.ringingmaster.engine.touch.container.Touch;
import net.jcip.annotations.Immutable;

import java.util.Optional;

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

	ProofTerminationReason getTerminationReason();

	String getTerminateReasonDisplayString();

	Optional<Method> getCreatedMethod();

	Optional<Analysis> getAnalysis();

	long getProofTime();
}
