package com.concurrentperformance.ringingmaster.engine.helper;

import com.concurrentperformance.ringingmaster.engine.touch.compiler.impl.CompilerFactory;
import com.concurrentperformance.ringingmaster.engine.method.Method;
import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import com.concurrentperformance.ringingmaster.engine.touch.proof.Proof;
import com.concurrentperformance.ringingmaster.engine.touch.proof.ProofTerminationReason;
import com.concurrentperformance.ringingmaster.engine.touch.container.Touch;
import com.concurrentperformance.ringingmaster.engine.touch.container.impl.TouchBuilder;

import static com.google.common.base.Preconditions.checkState;

/**
 * TODO comments???
 * User: Stephen
 */
public class PlainCourseHelper {

	public static Proof buildPlainCourse(NotationBody notation, String logPreamble, boolean withAnalysis) {
		Touch plainCourseTouch = TouchBuilder.buildPlainCourseInstance(notation);
		Proof proof = CompilerFactory.getInstance(plainCourseTouch,  logPreamble).compile(withAnalysis);
		Method createdMethod = proof.getCreatedMethod();

		checkState(createdMethod.getRowCount() > 0, "Plain course has no rows.");
		checkState(ProofTerminationReason.SPECIFIED_ROW == proof.getTerminationReason(),
				"Plain course must terminate with row [%s]" +
						" but actually terminated with [%s]",
				plainCourseTouch.getTerminationSpecificRow().get().getDisplayString(true),
				proof.getTerminateReasonDisplayString());
		return proof;
	}

}
