package org.ringingmaster.engine.helper;

import org.ringingmaster.engine.method.Method;
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.touch.compiler.impl.CompilerFactory;
import org.ringingmaster.engine.touch.container.impl.TouchBuilder;
import org.ringingmaster.engine.touch.newcontainer.ObservableTouch;
import org.ringingmaster.engine.touch.proof.Proof;
import org.ringingmaster.engine.touch.proof.ProofTerminationReason;

import static com.google.common.base.Preconditions.checkState;

/**
 * TODO comments???
 * User: Stephen
 */
public class PlainCourseHelper {

	public static Proof buildPlainCourse(NotationBody notation, String logPreamble, boolean withAnalysis) {
		ObservableTouch plainCourseTouch = TouchBuilder.buildPlainCourseInstance(notation);
		Proof proof = CompilerFactory.getInstance(plainCourseTouch.get(),  logPreamble).compile(withAnalysis, () -> false);
		Method createdMethod = proof.getCreatedMethod().get();

		checkState(createdMethod.getRowCount() > 0, "Plain course has no rows.");
		checkState(ProofTerminationReason.SPECIFIED_ROW == proof.getTerminationReason(),
				"Plain course must terminate with row [%s]" +
						" but actually terminated with [%s]",
				plainCourseTouch.get().getTerminationChange().get().getDisplayString(true),
				proof.getTerminateReasonDisplayString());
		return proof;
	}

}
