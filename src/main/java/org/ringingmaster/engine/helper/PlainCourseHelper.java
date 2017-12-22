package org.ringingmaster.engine.helper;

import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.touch.proof.Proof;

/**
 * TODO comments???
 * User: Stephen
 */
public class PlainCourseHelper {

	public static Proof buildPlainCourse(NotationBody notation, String logPreamble, boolean withAnalysis) {
//TODO		ObservableTouch plainCourseTouch = TouchBuilder.buildPlainCourseInstance(notation);
//		Proof proof = CompilerFactory.getInstance(plainCourseTouch.get(),  logPreamble).compile(withAnalysis, () -> false);
//		Method createdMethod = proof.getCreatedMethod().get();
//
//		checkState(createdMethod.getRowCount() > 0, "Plain course has no rows.");
//		checkState(ProofTerminationReason.SPECIFIED_ROW == proof.getTerminationReason(),
//				"Plain course must terminate with row [%s]" +
//						" but actually terminated with [%s]",
//				plainCourseTouch.get().getTerminationChange().get().getDisplayString(true),
//				proof.getTerminateReasonDisplayString());
//		return proof;
		return null;
	}

}
