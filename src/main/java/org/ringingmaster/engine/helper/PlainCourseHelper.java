package org.ringingmaster.engine.helper;

import org.ringingmaster.engine.compiler.CompileTerminationReason;
import org.ringingmaster.engine.compiler.Compiler;
import org.ringingmaster.engine.compiler.compiledcomposition.CompiledComposition;
import org.ringingmaster.engine.composition.MutableComposition;
import org.ringingmaster.engine.composition.compositiontype.CompositionType;
import org.ringingmaster.engine.method.Method;
import org.ringingmaster.engine.method.MethodBuilder;
import org.ringingmaster.engine.notation.Notation;
import org.ringingmaster.engine.parser.Parser;
import org.ringingmaster.engine.composition.Composition;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkState;
import static org.ringingmaster.engine.composition.MutableComposition.TERMINATION_MAX_LEADS_MAX;
import static org.ringingmaster.engine.composition.MutableComposition.TERMINATION_MAX_ROWS_MAX;

/**
 * TODO comments???
 * User: Stephen
 */
public class PlainCourseHelper {

    private static AtomicInteger counter = new AtomicInteger();

    public static CompiledComposition buildPlainCourse(Notation notation, String logPreamble) {

        CompiledComposition compiledComposition = pipeline.apply(new PlainCourseNotation(notation, logPreamble));

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
    public static Function<Notation, Composition> buildPlainCourseComposition = notation -> {
        final MutableComposition composition = new MutableComposition();
        composition.setTitle(" " + notation.getNameIncludingNumberOfBells() + ":" + " PLAINCOURSE_" + counter.getAndIncrement());
        composition.setNumberOfBells(notation.getNumberOfWorkingBells());
        composition.addNotation(notation);
        composition.setCompositionType(CompositionType.LEAD_BASED);
        composition.setTerminationChange(MethodBuilder.buildRoundsRow(notation.getNumberOfWorkingBells()));
        composition.setTerminationMaxLeads(TERMINATION_MAX_LEADS_MAX);
        composition.setTerminationMaxRows(TERMINATION_MAX_ROWS_MAX);

        return composition.get();
    };

    private static Function<Notation, CompiledComposition> pipeline =
            buildPlainCourseComposition
                    .andThen(new Parser())
                    .andThen(new Compiler());
}
