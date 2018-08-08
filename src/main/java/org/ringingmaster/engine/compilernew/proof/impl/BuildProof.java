package org.ringingmaster.engine.compilernew.proof.impl;

import org.ringingmaster.engine.compilernew.internaldata.CommonCompilerPipelineData;
import org.ringingmaster.engine.compilernew.proof.Proof;

import java.util.Optional;
import java.util.function.Function;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class BuildProof<T extends CommonCompilerPipelineData<T>> implements Function<T, Proof> {

    @Override
    public Proof apply(T data) {
        return new DefaultProof(data.getParse(),
                data.getTerminationReason().get(),
                data.getTerminateNotes(),
                Optional.empty(),
                Optional.empty(),
                1);
    }
}
