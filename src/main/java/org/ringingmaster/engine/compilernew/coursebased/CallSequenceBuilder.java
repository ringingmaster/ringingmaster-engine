package org.ringingmaster.engine.compilernew.coursebased;

import org.ringingmaster.engine.compiler.impl.CourseBasedCallDecomposer;

import java.util.function.Function;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class CallSequenceBuilder implements Function<CourseBasedCompilerPipelineData, CourseBasedCompilerPipelineData> {

    @Override
    public CourseBasedCompilerPipelineData apply(CourseBasedCompilerPipelineData data) {
        return new CourseBasedCallDecomposer(data.getParse().getTouch(), data.getLogPreamble()).createCallSequence();
    }
}
