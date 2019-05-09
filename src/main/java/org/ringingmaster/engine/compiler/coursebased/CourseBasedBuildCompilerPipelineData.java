package org.ringingmaster.engine.compiler.coursebased;

import org.ringingmaster.engine.parser.parse.Parse;

import java.util.function.Function;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
public class CourseBasedBuildCompilerPipelineData implements Function<Parse, CourseBasedCompilerPipelineData> {

    @Override
    public CourseBasedCompilerPipelineData apply(Parse parse) {
        return new CourseBasedCompilerPipelineData(parse);
    }

}
