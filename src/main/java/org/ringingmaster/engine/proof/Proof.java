package org.ringingmaster.engine.proof;

import org.ringingmaster.engine.method.Method;
import org.ringingmaster.engine.analysis.Analysis;
import org.ringingmaster.engine.touch.Touch;
import javax.annotation.concurrent.Immutable;

import java.util.Optional;

/**
 * TODO comments.
 *
 * @author stephen
 */
@Immutable
public interface Proof {

	/**
	 * An immutable {@link Touch} as it was when the
	 * proof was requested.
	 */
	Touch getTouch();

	ProofTerminationReason getTerminationReason();

	String getTerminateReasonDisplayString();

	Optional<Method> getCreatedMethod();

	Optional<Analysis> getAnalysis();

	long getProofTimeMs();
}
