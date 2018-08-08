package org.ringingmaster.engine.helper;

import org.ringingmaster.engine.compilernew.Compiler;
import org.ringingmaster.engine.compilernew.proof.Proof;
import org.ringingmaster.engine.compilernew.proof.ProofTerminationReason;
import org.ringingmaster.engine.method.Method;
import org.ringingmaster.engine.method.impl.MethodBuilder;
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.parser.Parser;
import org.ringingmaster.engine.touch.ObservableTouch;
import org.ringingmaster.engine.touch.Touch;
import org.ringingmaster.engine.touch.checkingtype.CheckingType;

import java.util.function.Function;

import static com.google.common.base.Preconditions.checkState;
import static org.ringingmaster.engine.touch.ObservableTouch.TERMINATION_MAX_LEADS_MAX;
import static org.ringingmaster.engine.touch.ObservableTouch.TERMINATION_MAX_ROWS_MAX;

/**
 * TODO comments???
 * User: Stephen
 */
public class PlainCourseHelper {

	private static Parser parser = new Parser();
	private static Compiler compiler = new Compiler();

	public static Proof buildPlainCourse(NotationBody notation, String logPreamble, boolean withAnalysis) {

		Proof proof = buildPlainCourseInstance
				.andThen(parser)
				.andThen(compiler)
				.apply(notation);

		Method createdMethod = proof.getCreatedMethod().get();

		checkState(createdMethod.getRowCount() > 0, "Plain course has no rows.");
		checkState(ProofTerminationReason.SPECIFIED_ROW == proof.getTerminationReason(),
				"Plain course must terminate with row [%s]" +
						" but actually terminated with [%s]",
				proof.getParse().getTouch().getTerminationChange().get().getDisplayString(true),
				proof.getTerminateReasonDisplayString());
		return proof;
	}

	/**
	 * Shortcut to build a touch for a single notation plain course.
	 *
	 * @param notationBody
	 * @return
	 */
	private static Function<NotationBody, Touch> buildPlainCourseInstance = notationBody -> {
		final ObservableTouch touch = new ObservableTouch();
		touch.setTitle("Plain Course of " + notationBody.getNameIncludingNumberOfBells());
		touch.setNumberOfBells(notationBody.getNumberOfWorkingBells());
		touch.addNotation(notationBody);
		touch.setTouchCheckingType(CheckingType.LEAD_BASED);
		touch.setTerminationChange(MethodBuilder.buildRoundsRow(notationBody.getNumberOfWorkingBells()));
		touch.setTerminationMaxLeads(TERMINATION_MAX_LEADS_MAX);
		touch.setTerminationMaxRows(TERMINATION_MAX_ROWS_MAX);

		return touch.get();
	};

}
