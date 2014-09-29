package com.concurrentperformance.ringingmaster.engine.proof.impl;

import net.jcip.annotations.Immutable;

import com.concurrentperformance.ringingmaster.engine.analysis.Analysis;
import com.concurrentperformance.ringingmaster.engine.method.Method;
import com.concurrentperformance.ringingmaster.engine.proof.Proof;
import com.concurrentperformance.ringingmaster.engine.proof.ProofTerminationReason;
import com.concurrentperformance.ringingmaster.engine.touch.Touch;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO comments???
 * User: Stephen
 */
@Immutable
public class DefaultProof implements Proof {

	private final Touch touch;
	private final Method method;
	private final ProofTerminationReason terminationReason;
	private final Analysis analysis;


	public DefaultProof(Touch touch, Method method, ProofTerminationReason terminationReason, Analysis analysis) {
		this.touch = checkNotNull(touch, "touch must not be null");
		this.method = checkNotNull(method, "method must not be null");
		this.terminationReason = checkNotNull(terminationReason, "terminationReason must not be null");
		this.analysis = analysis; //analysis can be null when not requested
	}

	@Override
	public Touch getTouch() {
		return touch;
	}

	@Override
	public Method getMethod() {
		return method;
	}

	@Override
	public ProofTerminationReason getTerminationReason() {
		return terminationReason;
	}

	@Override
	public Analysis getAnalysis() {
		return analysis;
	}
}
