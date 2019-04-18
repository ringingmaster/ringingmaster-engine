package org.ringingmaster.engine.compiler.coursebased;

import org.ringingmaster.engine.compiler.common.ValidCompositionCheck;
import org.ringingmaster.engine.compiler.compiledcomposition.CompiledComposition;
import org.ringingmaster.engine.compiler.leadbased.BuildCallSequence;
import org.ringingmaster.engine.compiler.compiledcomposition.BuildCompiledComposition;
import org.ringingmaster.engine.parser.parse.Parse;

import java.util.function.Function;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class CourseBasedCompilePipeline implements Function<Parse, CompiledComposition> {

    //TODO copy style of lead - with static pipeline

    private final BuildCourseBasedCompolePipelineData buildCourseBasedCompolePipelineData = new BuildCourseBasedCompolePipelineData();
    private final ValidCompositionCheck<CourseBasedCompilePipelineData> validCompositionCheck = new ValidCompositionCheck<>();
    private final BuildCallPositionNames buildCallPositionNames = new BuildCallPositionNames();
    private final BuildCallSequence buildCallSequence = new BuildCallSequence();

    private final BuildCompiledComposition<CourseBasedCompilePipelineData> buildCompiledComposition = new BuildCompiledComposition();

    @Override
    public CompiledComposition apply(Parse parse) {
        return this.buildCourseBasedCompolePipelineData
                .andThen(validCompositionCheck)
                .andThen(buildCallPositionNames)
                //TODO add back in when common between lead and course. .andThen(buildCallSequence)
                
                .andThen(buildCompiledComposition)

                .apply(parse);
    }
}
