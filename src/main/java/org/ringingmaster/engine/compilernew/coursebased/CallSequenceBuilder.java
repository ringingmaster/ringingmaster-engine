package org.ringingmaster.engine.compilernew.coursebased;

import com.google.common.collect.ImmutableList;
import org.ringingmaster.engine.compiler.impl.CourseBasedDecomposedCall;
import org.ringingmaster.engine.compilernew.call.CallDecomposer;

import java.util.function.Function;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class CallSequenceBuilder implements Function<CourseBasedCompilerPipelineData, CourseBasedCompilerPipelineData> {

    private final CallDecomposer callDecomposer = new CallDecomposer();

    @Override
    public CourseBasedCompilerPipelineData apply(CourseBasedCompilerPipelineData data) {
        final ImmutableList<CourseBasedDecomposedCall> callSequence = callDecomposer.createCallSequence(data.getParse(), data.getLogPreamble());
        ??
    }
}
