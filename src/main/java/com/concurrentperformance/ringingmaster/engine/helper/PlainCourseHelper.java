package com.concurrentperformance.ringingmaster.engine.helper;

import com.concurrentperformance.ringingmaster.engine.compiler.impl.CompilerFactory;
import com.concurrentperformance.ringingmaster.engine.method.Method;
import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import com.concurrentperformance.ringingmaster.engine.proof.Proof;
import com.concurrentperformance.ringingmaster.engine.proof.ProofTerminationReason;
import com.concurrentperformance.ringingmaster.engine.touch.Touch;
import com.concurrentperformance.ringingmaster.engine.touch.impl.TouchBuilder;

import static com.google.common.base.Preconditions.checkState;

/**
 * TODO comments???
 * User: Stephen
 */
public class PlainCourseHelper {

	public static Method buildPlainCourse(NotationBody notation, String logPreamble) {
		Touch plainCourseTouch = TouchBuilder.buildPlainCourseInstance(notation);
		Proof proof = CompilerFactory.getInstance(plainCourseTouch,  logPreamble).compile(false);
		checkState(ProofTerminationReason.SPECIFIED_ROW == proof.getTerminationReason(), "Plain course must terminate with [" + ProofTerminationReason.SPECIFIED_ROW + "] but actually terminated with [" + proof.getTerminationReason());
		return proof.getMethod();
	}

}
