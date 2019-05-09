package org.ringingmaster.engine.compiler.leadbased;

import com.google.common.collect.ImmutableList;
import org.ringingmaster.engine.compiler.denormaliser.CallDenormaliser;
import org.ringingmaster.engine.parser.assignparsetype.ParseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

import static org.ringingmaster.engine.parser.assignparsetype.ParseType.DEFAULT_CALL_MULTIPLIER;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.PLAIN_LEAD;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
public class LeadBasedBuildCallSequence implements Function<LeadBasedCompilerPipelineData, LeadBasedCompilerPipelineData> {
    private final Logger log = LoggerFactory.getLogger(LeadBasedBuildCallSequence.class);

    private final CallDenormaliser<LeadBasedDenormalisedCall, LeadBasedCompilerPipelineData> callDenormaliser = new CallDenormaliser<>() {

        @Override
        protected LeadBasedDenormalisedCall buildDecomposedCall(String callName, ParseType parseType, State state, LeadBasedCompilerPipelineData input) {
            return new LeadBasedDenormalisedCall(callName, state.getCurrentVariance(), parseType.equals(PLAIN_LEAD), parseType.equals(DEFAULT_CALL_MULTIPLIER));
        }
    };

    @Override
    public LeadBasedCompilerPipelineData apply(LeadBasedCompilerPipelineData input) {
        if (input.isTerminated()) {
            return input;
        }

        log.debug("{} > creating call sequence", input.getLogPreamble());

        //TODO why do we not just havce the call sequence code in this class?
        final ImmutableList<LeadBasedDenormalisedCall> callSequence =
                callDenormaliser.createCallSequence(input.getParse(), input.getVarianceLookupByName(), input.getLogPreamble(), input);

        log.debug("{} < creating call sequence", input.getLogPreamble());

        return input.setDenormalisedCallSequence(callSequence);
    }
}
