package org.ringingmaster.engine.compiler.impl;

import javax.annotation.concurrent.Immutable;

import org.ringingmaster.engine.parser.assignparsetype.ParseType;
import org.ringingmaster.engine.touch.variance.Variance;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO comments???
 * User: Stephen
 */
@Immutable
public class LeadBasedDecomposedCall extends DecomposedCall {

	private final ParseType parseType;

	public LeadBasedDecomposedCall(String callName, Variance variance, ParseType parseType) {
		super(callName, variance);
		this.parseType = checkNotNull(parseType, "parse type must not be null");
	}

	public ParseType getParseType() {
		return parseType;
	}

	@Override
	public String toString() {
		String varianceToString = getVariance().toString();
		return "{" + parseType + "," + getCallName()  +
				((varianceToString.length() > 0)?(", " + varianceToString ):"") +
				'}';
	}
}
