package org.ringingmaster.engine.compiler.coursebased;

import org.ringingmaster.engine.parser.parse.Parse;

import java.util.function.Function;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class BuildCourseBasedCompolePipelineData implements Function<Parse, CourseBasedCompilePipelineData> {

    @Override
    public CourseBasedCompilePipelineData apply(Parse parse) {
        return new CourseBasedCompilePipelineData(parse);
    }

}
