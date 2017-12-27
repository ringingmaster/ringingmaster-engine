package org.ringingmaster.engine.compiler.impl;

import javax.annotation.concurrent.Immutable;

import org.ringingmaster.engine.touch.variance.Variance;

/**
 * TODO comments???
 * User: Stephen
 */
@Immutable
public class CourseBasedDecomposedCall extends DecomposedCall {

	private final String callingPositionName;

	public CourseBasedDecomposedCall(String callName, Variance variance, String callingPositionName) {
		super(callName, variance);
		this.callingPositionName = callingPositionName;
	}

	public String getCallingPositionName() {
		return callingPositionName;
	}

	@Override
	public String toString() {
		String varianceToString = getVariance().toString();
		return "{" + getCallName() + "@" + callingPositionName +
				((varianceToString.length() > 0)?(", " + varianceToString ):"") +
				'}';
	}
}
