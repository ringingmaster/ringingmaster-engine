package org.ringingmaster.engine.helper;

import org.ringingmaster.engine.compiler.CompileTerminationReason;
import org.ringingmaster.engine.compiler.Compiler;
import org.ringingmaster.engine.compiler.compiledcomposition.CompiledComposition;
import org.ringingmaster.engine.composition.ObservableComposition;
import org.ringingmaster.engine.method.Method;
import org.ringingmaster.engine.method.MethodBuilder;
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.parser.Parser;
import org.ringingmaster.engine.composition.Composition;
import org.ringingmaster.engine.composition.checkingtype.CheckingType;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkState;
import static org.ringingmaster.engine.composition.ObservableComposition.TERMINATION_MAX_LEADS_MAX;
import static org.ringingmaster.engine.composition.ObservableComposition.TERMINATION_MAX_ROWS_MAX;

/**
 * TODO comments???
 * User: Stephen
 */
public class PlainCourseHelper {

	private static AtomicInteger counter = new AtomicInteger();

	public static CompiledComposition buildPlainCourse(NotationBody notation, String logPreamble /*TODO How to get this in the pipeline? */, boolean withAnalysis) {


		CompiledComposition compiledComposition = pipeline.apply(notation);

		Method createdMethod = compiledComposition.getMethod().get();

		checkState(createdMethod.getRowCount() > 0, "Plain course has no rows.");
		checkState(CompileTerminationReason.SPECIFIED_ROW == compiledComposition.getTerminationReason(),
				"Plain course must terminate with row [%s]" +
						" but actually terminated with [%s]",
				compiledComposition.getComposition().getTerminationChange().get().getDisplayString(true),
				compiledComposition.getTerminateReasonDisplayString());
		return compiledComposition;
	}

	/**
	 * Shortcut to build a composition for a single notation plain course.
	 *
	 * @param notationBody
	 * @return
	 */
	public static Function<NotationBody, Composition> buildPlainCourseInstance = notationBody -> {
		final ObservableComposition composition = new ObservableComposition();
		composition.setTitle("PLAINCOURSE_" + counter.getAndIncrement() + ":" + notationBody.getNameIncludingNumberOfBells());
		composition.setNumberOfBells(notationBody.getNumberOfWorkingBells());
		composition.addNotation(notationBody);
		composition.setCheckingType(CheckingType.LEAD_BASED);
		composition.setTerminationChange(MethodBuilder.buildRoundsRow(notationBody.getNumberOfWorkingBells()));
		composition.setTerminationMaxLeads(TERMINATION_MAX_LEADS_MAX);
		composition.setTerminationMaxRows(TERMINATION_MAX_ROWS_MAX);

		return composition.get();
	};

	private static Function<NotationBody, CompiledComposition> pipeline =
			buildPlainCourseInstance
					.andThen(new Parser())
					.andThen(new Compiler());
}
