package org.ringingmaster.engine.compiler.coursebased;

import org.ringingmaster.engine.compiler.common.ValidCompositionCheck;
import org.ringingmaster.engine.compiler.compiledcomposition.BuildCompiledComposition;
import org.ringingmaster.engine.compiler.compiledcomposition.CompiledComposition;
import org.ringingmaster.engine.parser.parse.Parse;

import java.util.function.Function;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class CourseBasedCompilePipeline implements Function<Parse, CompiledComposition> {

    @Override
    public CompiledComposition apply(Parse parse) {
//TODO Add in early terminate nechanisam
        return pipeline.apply(parse);
    }


    private static final Function<Parse, CompiledComposition> pipeline =
        new BuildCourseBasedCompolePipelineData()
                .andThen(new ValidCompositionCheck<>())
                .andThen(new BuildCallPositionLookupByColumn())
                //TODO add back in when common between lead and course. .andThen(buildCallSequence)
                
                .andThen(new BuildCompiledComposition<>());
}
