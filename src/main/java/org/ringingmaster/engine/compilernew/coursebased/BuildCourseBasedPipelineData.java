package org.ringingmaster.engine.compilernew.coursebased;

import org.ringingmaster.engine.compilernew.internaldata.CourseBasedCompilerPipelineData;
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
