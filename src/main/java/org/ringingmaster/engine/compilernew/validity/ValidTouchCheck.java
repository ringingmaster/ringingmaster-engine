package org.ringingmaster.engine.compilernew.validity;

import org.ringingmaster.engine.compilernew.coursebased.CourseBasedCompilerPipelineData;
import org.ringingmaster.engine.compilernew.proof.ProofTerminationReason;
import org.ringingmaster.engine.touch.Touch;

import java.util.function.Function;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class ValidTouchCheck implements Function<CourseBasedCompilerPipelineData, CourseBasedCompilerPipelineData> {

    @Override
    public CourseBasedCompilerPipelineData apply(CourseBasedCompilerPipelineData data) {
        final Touch touch = data.getParse().getTouch();
        if (touch.isSpliced()) {
            if (touch.getAvailableNotations().size() == 0) {
                return data.terminate(ProofTerminationReason.INVALID_TOUCH, "Spliced touch has no valid methods");
            }
        }
        else { // Not Spliced
            if (touch.getNonSplicedActiveNotation() == null) {
                return data.terminate(ProofTerminationReason.INVALID_TOUCH, "No active method");
            }
        }

        return data;
    }
}
