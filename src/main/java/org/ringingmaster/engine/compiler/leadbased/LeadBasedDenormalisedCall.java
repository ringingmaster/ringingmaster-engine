package org.ringingmaster.engine.compiler.leadbased;

import org.ringingmaster.engine.compiler.denormaliser.DenormalisedCall;
import org.ringingmaster.engine.compiler.variance.Variance;

import javax.annotation.concurrent.Immutable;

/**
 * TODO comments???
 * User: Stephen
 */
@Immutable
class LeadBasedDenormalisedCall extends DenormalisedCall {

	private final boolean plainLead;
	private final boolean defaultCall;

	public LeadBasedDenormalisedCall(String callName, Variance variance, boolean plainLead, boolean defaultCall) {
		super(callName, variance);
		this.plainLead = plainLead;
		this.defaultCall = defaultCall;
	}

	public boolean isPlainLead() {
		return plainLead;
	}

	public boolean isDefaultCall() {
		return defaultCall;
	}

	@Override
	public String toString() {
		String varianceToString = getVariance().toString();
		return "{" +
				((plainLead)?("PLAIN_LEAD,"):"") +
				((defaultCall)?("DEFAULT_CALL,"):"") +
				getCallName()  +
				((varianceToString.length() > 0)?(", " + varianceToString ):"") +
				'}';
	}

}
