package org.ringingmaster.engine.compilernew.proof.impl;

import org.ringingmaster.engine.compilernew.common.CompilePipelineData;
import org.ringingmaster.engine.compilernew.proof.Proof;

import java.util.Optional;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkState;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class BuildProof<T extends CompilePipelineData<T>> implements Function<T, Proof> {

    @Override
    public Proof apply(T data) {

        checkState(data.isTerminated(), "Cannot build proof for unterminated touch [%s]", data);

        return new DefaultProof(data.getParse(),
                data.getTerminationReason().get(),
                data.getTerminateNotes(),
                data.getCreatedMethod(),
                Optional.empty(),
                1);
    }
}
