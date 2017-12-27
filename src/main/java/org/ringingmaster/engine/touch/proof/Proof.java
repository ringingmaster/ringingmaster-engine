package org.ringingmaster.engine.touch.proof;

import org.ringingmaster.engine.method.Method;
import org.ringingmaster.engine.touch.analysis.Analysis;
import org.ringingmaster.engine.touch.container.Touch;
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
