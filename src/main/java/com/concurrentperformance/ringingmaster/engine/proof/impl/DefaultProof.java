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
	private final long proofTime;


	public DefaultProof(Touch touch, ProofTerminationReason terminationReason, Method createdMethod, Analysis analysis, long proofTime) {
		this.touch = checkNotNull(touch, "touch must not be null");
		this.terminationReason = checkNotNull(terminationReason, "terminationReason must not be null");
		this.createdMethod = createdMethod; // createdMethod can be null when termination reason is INVALID_TOUCH
		this.analysis = analysis; //analysis can be null when not requested
		this.proofTime = proofTime;
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
	public String getTerminateReasonDisplayString() {
		switch(getTerminationReason()) {

			case INVALID_TOUCH:
				return "";
			case ROW_COUNT:
				return "Row limit (" + getCreatedMethod().getRowCount() + ")";
			case LEAD_COUNT:
				return "Lead limit (" + getCreatedMethod().getLeadCount() + ")";
			case SPECIFIED_ROW:
				return "Change (" + getCreatedMethod().getLastRow().getDisplayString(true) + ")";
			case EMPTY_PARTS:
				return  "Aborted - Empty parts found";
			// TODO this is from C++
//			case TR_PARTS:
//				str.Format("Part limit (%d)", method->getPartCount());
//				addLine("Termination:", str, RGB(255, 120, 255));
//				break;
//
//			case TR_CIRCLE:
//				addLine("Termination:", "Aborted - Circular touch", RGB(255, 120, 120));
//				break;

			default:
				throw new RuntimeException("Please code for termination reason [" + getTerminationReason() + "]");
		}
	}


	@Override
	public Method getCreatedMethod() {
		return createdMethod;
	}

	@Override
	public Analysis getAnalysis() {
		return analysis;
	}

	@Override
	public long getProofTime() {
		return proofTime;
	}
}
