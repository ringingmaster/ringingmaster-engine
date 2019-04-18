package org.ringingmaster.engine.helper;

import org.ringingmaster.engine.compiler.CompileTerminationReason;
import org.ringingmaster.engine.compiler.Compiler;
import org.ringingmaster.engine.compiler.compiledtouch.CompiledTouch;
import org.ringingmaster.engine.method.Method;
import org.ringingmaster.engine.method.MethodBuilder;
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.parser.Parser;
import org.ringingmaster.engine.touch.ObservableTouch;
import org.ringingmaster.engine.touch.Touch;
import org.ringingmaster.engine.touch.checkingtype.CheckingType;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkState;
import static org.ringingmaster.engine.touch.ObservableTouch.TERMINATION_MAX_LEADS_MAX;
import static org.ringingmaster.engine.touch.ObservableTouch.TERMINATION_MAX_ROWS_MAX;

/**
 * TODO comments???
 * User: Stephen
 */
public class PlainCourseHelper {

	private static AtomicInteger counter = new AtomicInteger();

	public static CompiledTouch buildPlainCourse(NotationBody notation, String logPreamble /*TODO How to get this in the pipeline? */, boolean withAnalysis) {


		CompiledTouch compiledTouch = pipeline.apply(notation);

		Method createdMethod = compiledTouch.getMethod().get();

		checkState(createdMethod.getRowCount() > 0, "Plain course has no rows.");
		checkState(CompileTerminationReason.SPECIFIED_ROW == compiledTouch.getTerminationReason(),
				"Plain course must terminate with row [%s]" +
						" but actually terminated with [%s]",
				compiledTouch.getTouch().getTerminationChange().get().getDisplayString(true),
				compiledTouch.getTerminateReasonDisplayString());
		return compiledTouch;
	}

	/**
	 * Shortcut to build a touch for a single notation plain course.
	 *
	 * @param notationBody
	 * @return
	 */
	public static Function<NotationBody, Touch> buildPlainCourseInstance = notationBody -> {
		final ObservableTouch touch = new ObservableTouch();
		touch.setTitle("PLAINCOURSE_" + counter.getAndIncrement() + ":" + notationBody.getNameIncludingNumberOfBells());
		touch.setNumberOfBells(notationBody.getNumberOfWorkingBells());
		touch.addNotation(notationBody);
		touch.setCheckingType(CheckingType.LEAD_BASED);
		touch.setTerminationChange(MethodBuilder.buildRoundsRow(notationBody.getNumberOfWorkingBells()));
		touch.setTerminationMaxLeads(TERMINATION_MAX_LEADS_MAX);
		touch.setTerminationMaxRows(TERMINATION_MAX_ROWS_MAX);

		return touch.get();
	};

	private static Function<NotationBody, CompiledTouch> pipeline =
			buildPlainCourseInstance
					.andThen(new Parser())
					.andThen(new Compiler());
}
