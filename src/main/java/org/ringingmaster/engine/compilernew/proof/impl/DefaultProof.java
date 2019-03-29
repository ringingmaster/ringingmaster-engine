package org.ringingmaster.engine.compilernew.proof.impl;

import org.ringingmaster.engine.analysis.Analysis;
import org.ringingmaster.engine.compilernew.CompileTerminationReason;
import org.ringingmaster.engine.compilernew.proof.Proof;
import org.ringingmaster.engine.method.Method;
import org.ringingmaster.engine.parser.parse.Parse;

import javax.annotation.concurrent.Immutable;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO comments???
 * User: Stephen
 */
@Immutable
class DefaultProof implements Proof {

	private final Parse parse;
	private final CompileTerminationReason terminationReason;
	private final Optional<String> terminateNotes;
	private final Optional<Method> createdMethod;
	private final Optional<Analysis> analysis;
	private final long proofTimeMs;


	DefaultProof(Parse parse, CompileTerminationReason terminationReason, Optional<String> terminateNotes,
						Optional<Method> createdMethod, Optional<Analysis> analysis, long proofTimeMs) {
		this.parse = checkNotNull(parse, "parse must not be null");
		this.terminationReason = checkNotNull(terminationReason, "terminationReason must not be null");
		this.terminateNotes = checkNotNull(terminateNotes, "terminateNotes must not be null");
		this.createdMethod = checkNotNull(createdMethod); // createdMethod can be absent when termination reason is INVALID_TOUCH
		this.analysis = checkNotNull(analysis); //analysis can be absent when not requested
		this.proofTimeMs = proofTimeMs;
	}

	@Override
	public Parse getParse() {
		return parse;
	}

	@Override
	public CompileTerminationReason getTerminationReason() {
		return terminationReason;
	}

	@Override
	public String getTerminateReasonDisplayString() {
		switch(getTerminationReason()) {

			case INVALID_TOUCH:
				return (terminateNotes.isPresent())?terminateNotes.get():"";
			case ROW_COUNT:
				return "Row limit (" + getMethod().get().getRowCount() + ")";
			case LEAD_COUNT:
				return "Lead limit (" + getMethod().get().getLeadCount() + ")";
			case SPECIFIED_ROW:
				return "Change (" + getMethod().get().getLastRow().getDisplayString(true) + ")";
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
	public Optional<Method> getMethod() {
		return createdMethod;
	}

	@Override
	public Optional<Analysis> getAnalysis() {
		return analysis;
	}

	@Override
	public long getProofTimeMs() {
		return proofTimeMs;
	}
}
