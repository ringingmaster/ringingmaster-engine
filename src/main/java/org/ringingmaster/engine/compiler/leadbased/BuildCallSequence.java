package org.ringingmaster.engine.compiler.leadbased;

import com.google.common.collect.ImmutableList;
import org.ringingmaster.engine.compilerold.impl.LeadBasedDecomposedCall;
import org.ringingmaster.engine.compiler.common.CallDecomposer;

import java.util.function.Function;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class BuildCallSequence implements Function<LeadBasedCompilePipelineData, LeadBasedCompilePipelineData> {

    private final CallDecomposer callDecomposer = new CallDecomposer();

    @Override
    public LeadBasedCompilePipelineData apply(LeadBasedCompilePipelineData data) {
        if (data.isTerminated()) {
            return data;
        }

        final ImmutableList<LeadBasedDecomposedCall> callSequence = callDecomposer.createCallSequence(data.getParse(), data.getLogPreamble());
        return data.setCallSequence(callSequence);
    }
}
