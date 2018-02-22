package org.ringingmaster.engine.compilernew.coursebased;

import org.ringingmaster.engine.compilernew.internaldata.BuildCourseBasedPipelineData;
import org.ringingmaster.engine.compilernew.proof.Proof;
import org.ringingmaster.engine.compilernew.proof.impl.BuildProof;
import org.ringingmaster.engine.compilernew.validity.ValidTouchCheck;
import org.ringingmaster.engine.parser.parse.Parse;

import java.util.function.Function;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class CourseBasedPipeline implements Function<Parse, Proof> {

    private final BuildCourseBasedPipelineData buildCourseBasedPipelineData = new BuildCourseBasedPipelineData();
    private final ValidTouchCheck validTouchCheck = new ValidTouchCheck();
    private final BuildCoursePositionNames buildCoursePositionNames = new BuildCoursePositionNames();
    private final BuildProof buildProof = new BuildProof();

    @Override
    public Proof apply(Parse parse) {

        return buildCourseBasedPipelineData
                .andThen(validTouchCheck)
                .andThen(buildCoursePositionNames)
                
                .andThen(buildProof)
                .apply(parse);
    }
}
