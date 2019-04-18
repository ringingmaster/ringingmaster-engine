package org.ringingmaster.engine.compiler.coursebased;

import org.ringingmaster.engine.compiler.common.ValidTouchCheck;
import org.ringingmaster.engine.compiler.leadbased.BuildCallSequence;
import org.ringingmaster.engine.compiler.compiledtouch.BuildCompiledTouch;
import org.ringingmaster.engine.compiler.compiledtouch.CompiledTouch;
import org.ringingmaster.engine.parser.parse.Parse;

import java.util.function.Function;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class CourseBasedCompilePipeline implements Function<Parse, CompiledTouch> {

    //TODO copy style of lead - with static pipeline

    private final BuildCourseBasedCompolePipelineData buildCourseBasedCompolePipelineData = new BuildCourseBasedCompolePipelineData();
    private final ValidTouchCheck<CourseBasedCompilePipelineData> validTouchCheck = new ValidTouchCheck<>();
    private final BuildCallPositionNames buildCallPositionNames = new BuildCallPositionNames();
    private final BuildCallSequence buildCallSequence = new BuildCallSequence();

    private final BuildCompiledTouch<CourseBasedCompilePipelineData> buildCompiledTouch = new BuildCompiledTouch();

    @Override
    public CompiledTouch apply(Parse parse) {
        return this.buildCourseBasedCompolePipelineData
                .andThen(validTouchCheck)
                .andThen(buildCallPositionNames)
                //TODO add back in when common between lead and course. .andThen(buildCallSequence)
                
                .andThen(buildCompiledTouch)

                .apply(parse);
    }
}
