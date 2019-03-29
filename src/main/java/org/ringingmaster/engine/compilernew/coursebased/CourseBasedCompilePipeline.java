package org.ringingmaster.engine.compilernew.coursebased;

import org.ringingmaster.engine.compilernew.leadbased.BuildCallSequence;
import org.ringingmaster.engine.compilernew.proof.Proof;
import org.ringingmaster.engine.compilernew.proof.impl.BuildProof;
import org.ringingmaster.engine.compilernew.common.ValidTouchCheck;
import org.ringingmaster.engine.parser.parse.Parse;

import java.util.function.Function;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class CourseBasedCompilePipeline implements Function<Parse, Proof> {

    private final BuildCourseBasedCompolePipelineData buildCourseBasedCompolePipelineData = new BuildCourseBasedCompolePipelineData();
    private final ValidTouchCheck<CourseBasedCompilePipelineData> validTouchCheck = new ValidTouchCheck<>();
    private final BuildCallPositionNames buildCallPositionNames = new BuildCallPositionNames();
    private final BuildCallSequence buildCallSequence = new BuildCallSequence();

    private final BuildProof<CourseBasedCompilePipelineData> buildProof = new BuildProof();

    @Override
    public Proof apply(Parse parse) {
        return this.buildCourseBasedCompolePipelineData
                .andThen(validTouchCheck)
                .andThen(buildCallPositionNames)
                //TODO add back in when common between lead and course. .andThen(buildCallSequence)
                
                .andThen(buildProof)

                .apply(parse);
    }
}
