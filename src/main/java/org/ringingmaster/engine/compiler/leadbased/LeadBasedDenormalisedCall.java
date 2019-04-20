package org.ringingmaster.engine.compiler.leadbased;

import org.ringingmaster.engine.compiler.common.DenormalisedCall;
import org.ringingmaster.engine.compiler.variance.Variance;

import javax.annotation.concurrent.Immutable;

/**
 * TODO comments???
 * User: Stephen
 */
@Immutable
public class LeadBasedDenormalisedCall extends DenormalisedCall {

	private final boolean plainLead;

	public LeadBasedDenormalisedCall(String callName, Variance variance, boolean plainLead) {
		super(callName, variance);
		this.plainLead = plainLead;
	}

	public boolean isPlainLead() {
		return plainLead;
	}

	@Override
	public String toString() {
		String varianceToString = getVariance().toString();
		return "{" +
				((plainLead)?("plainLead,"):"") +
				getCallName()  +
				((varianceToString.length() > 0)?(", " + varianceToString ):"") +
				'}';
	}

}
