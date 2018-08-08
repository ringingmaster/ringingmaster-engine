package org.ringingmaster.engine.compilernew.coursebased;

import org.ringingmaster.engine.compilernew.internaldata.CourseBasedCompilerPipelineData;
import org.ringingmaster.engine.compilernew.proof.Proof;
import org.ringingmaster.engine.compilernew.proof.impl.BuildProof;
import org.ringingmaster.engine.compilernew.validity.CommonValidTouchCheck;
import org.ringingmaster.engine.parser.parse.Parse;

import java.util.function.Function;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class CourseBasedPipeline implements Function<Parse, Proof> {

    private final BuildCourseBasedPipelineData buildCourseBasedPipelineData = new BuildCourseBasedPipelineData();
    private final CommonValidTouchCheck<CourseBasedCompilerPipelineData> commonValidTouchCheck = new CommonValidTouchCheck<>();
    private final BuildCallPositionNames buildCallPositionNames = new BuildCallPositionNames();
    private final BuildCallSequence buildCallSequence = new BuildCallSequence();

    private final BuildProof<CourseBasedCompilerPipelineData> buildProof = new BuildProof();

    @Override
    public Proof apply(Parse parse) {
        return this.buildCourseBasedPipelineData
                .andThen(commonValidTouchCheck)
                .andThen(buildCallPositionNames)
                .andThen(buildCallSequence)
                
                .andThen(buildProof)

                .apply(parse);
    }
}
