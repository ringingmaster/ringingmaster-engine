package com.concurrentperformance.ringingmaster.engine.touch.compiler.impl;

import net.jcip.annotations.Immutable;

import com.concurrentperformance.ringingmaster.engine.touch.container.Variance;

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