package org.ringingmaster.engine.compilernew.internaldata;

import org.ringingmaster.engine.compilernew.coursebased.CourseBasedCompilerPipelineData;
import org.ringingmaster.engine.parser.parse.Parse;

import java.util.function.Function;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class BuildCourseBasedPipelineData implements Function<Parse, CourseBasedCompilerPipelineData> {

    @Override
    public CourseBasedCompilerPipelineData apply(Parse parse) {
        return new CourseBasedCompilerPipelineData(parse);
    }

}
