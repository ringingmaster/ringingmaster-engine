package org.ringingmaster.engine.compilernew.proof.impl;

import org.ringingmaster.engine.compilernew.coursebased.CourseBasedCompilerPipelineData;
import org.ringingmaster.engine.compilernew.proof.Proof;

import java.util.Optional;
import java.util.function.Function;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class BuildProof implements Function<CourseBasedCompilerPipelineData, Proof> {

    @Override
    public Proof apply(CourseBasedCompilerPipelineData data) {
        return new DefaultProof(data.getParse(),
                data.getTerminationReason().get(),
                data.getTerminateNotes(),
                Optional.empty(),
                Optional.empty(),
                1);
    }
}
