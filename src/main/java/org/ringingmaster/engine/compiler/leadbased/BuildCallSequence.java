package org.ringingmaster.engine.compiler.leadbased;

import com.google.common.collect.ImmutableList;
import org.ringingmaster.engine.compiler.common.CallDecomposer;
import org.ringingmaster.engine.compilerold.impl.LeadBasedDecomposedCall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class BuildCallSequence implements Function<LeadBasedCompilePipelineData, LeadBasedCompilePipelineData> {
    private final Logger log = LoggerFactory.getLogger(BuildCallSequence.class);

    private final CallDecomposer callDecomposer = new CallDecomposer();

    @Override
    public LeadBasedCompilePipelineData apply(LeadBasedCompilePipelineData input) {
        if (input.isTerminated()) {
            return input;
        }

        log.debug("{} > creating call sequence", input.getLogPreamble());

        //TODO why do we not just havce the call sequence code in this class?
        final ImmutableList<LeadBasedDecomposedCall> callSequence =
                callDecomposer.createCallSequence(input.getParse(), input.getVarianceLookupByName(), input.getLogPreamble());

        log.debug("{} < creating call sequence", input.getLogPreamble());

        return input.setCallSequence(callSequence);
    }
}
