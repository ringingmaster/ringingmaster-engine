package org.ringingmaster.engine.compiler.denormaliser;

import org.ringingmaster.engine.compiler.variance.Variance;

import javax.annotation.concurrent.Immutable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO comments???
 * User: Stephen
 */
@Immutable
public abstract class DenormalisedCall {
	private final String callName;
	private final Variance variance;

	public DenormalisedCall(String callName, Variance variance) {
		this.callName = checkNotNull(callName, "callName must not be null");
		this.variance = checkNotNull(variance, "variance must not be null");
	}

	public String getCallName() {
		return callName;
	}

	public Variance getVariance() {
		return variance;
	}
}