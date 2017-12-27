package org.ringingmaster.engine.helper;

import org.ringingmaster.engine.method.Method;
import org.ringingmaster.engine.method.impl.MethodBuilder;
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.touch.compiler.impl.CompilerFactory;
import org.ringingmaster.engine.touch.newcontainer.ObservableTouch;
import org.ringingmaster.engine.touch.newcontainer.checkingtype.CheckingType;
import org.ringingmaster.engine.touch.proof.Proof;
import org.ringingmaster.engine.touch.proof.ProofTerminationReason;

import static com.google.common.base.Preconditions.checkState;
import static org.ringingmaster.engine.touch.newcontainer.ObservableTouch.TERMINATION_MAX_LEADS_MAX;
import static org.ringingmaster.engine.touch.newcontainer.ObservableTouch.TERMINATION_MAX_ROWS_MAX;

/**
 * TODO comments???
 * User: Stephen
 */
public class PlainCourseHelper {

	public static Proof buildPlainCourse(NotationBody notation, String logPreamble, boolean withAnalysis) {
		ObservableTouch plainCourseTouch = buildPlainCourseInstance(notation);
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

	/**
	 * Shortcut to build a touch for a single notation plain course.
	 *
	 * @param notationBody
	 * @return
	 */
	public static ObservableTouch buildPlainCourseInstance(NotationBody notationBody) {
		final ObservableTouch touch = new ObservableTouch();
		touch.setTitle("Plain Course of " + notationBody.getNameIncludingNumberOfBells());
		touch.setNumberOfBells(notationBody.getNumberOfWorkingBells());
//TODO		touch.setColumnCount(1);
//		touch.setRowCount(1);
		touch.addNotation(notationBody);
		touch.setTouchCheckingType(CheckingType.LEAD_BASED);
		touch.setTerminationChange(MethodBuilder.buildRoundsRow(notationBody.getNumberOfWorkingBells()));
		touch.setTerminationMaxLeads(TERMINATION_MAX_LEADS_MAX);
		touch.setTerminationMaxRows(TERMINATION_MAX_ROWS_MAX);

		return touch;
	}

}
