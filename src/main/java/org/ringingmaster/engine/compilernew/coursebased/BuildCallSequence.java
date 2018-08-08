package org.ringingmaster.engine.compilernew.coursebased;

import com.google.common.collect.ImmutableList;
import org.ringingmaster.engine.compiler.impl.CourseBasedDecomposedCall;
import org.ringingmaster.engine.compilernew.call.CallDecomposer;
import org.ringingmaster.engine.compilernew.internaldata.CourseBasedCompilerPipelineData;

import java.util.function.Function;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class BuildCallSequence implements Function<CourseBasedCompilerPipelineData, CourseBasedCompilerPipelineData> {

    private final CallDecomposer callDecomposer = new CallDecomposer();

    @Override
    public CourseBasedCompilerPipelineData apply(CourseBasedCompilerPipelineData data) {
        final ImmutableList<CourseBasedDecomposedCall> callSequence = callDecomposer.createCallSequence(data.getParse(), data.getCallPositionNames(), data.getLogPreamble());
        return data.setCallSequence(callSequence);
    }
}
