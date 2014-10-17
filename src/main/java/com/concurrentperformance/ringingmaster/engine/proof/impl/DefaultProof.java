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
	private final ProofTerminationReason terminationReason;
	private final Method createdMethod;
	private final Analysis analysis;


	public DefaultProof(Touch touch, ProofTerminationReason terminationReason, Method createdMethod, Analysis analysis) {
		this.touch = checkNotNull(touch, "touch must not be null");
		this.terminationReason = checkNotNull(terminationReason, "terminationReason must not be null");
		this.createdMethod = createdMethod; // createdMethod can be null when termination reason is INVALID_TOUCH
		this.analysis = analysis; //analysis can be null when not requested
	}

	@Override
	public Touch getTouch() {
		return touch;
	}

	@Override
	public ProofTerminationReason getTerminationReason() {
		return terminationReason;
	}

	@Override
	public Method getCreatedMethod() {
		return createdMethod;
	}

	@Override
	public Analysis getAnalysis() {
		return analysis;
	}
}
