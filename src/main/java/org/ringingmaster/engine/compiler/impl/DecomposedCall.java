package org.ringingmaster.engine.compiler.impl;

import javax.annotation.concurrent.Immutable;

import org.ringingmaster.engine.touch.variance.Variance;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO comments???
 * User: Stephen
 */
@Immutable
public abstract class DecomposedCall {
	private final String callName;
	private final Variance variance;

	public DecomposedCall(String callName, Variance variance) {
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