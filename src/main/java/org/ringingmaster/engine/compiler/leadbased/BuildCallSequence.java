package org.ringingmaster.engine.compiler.leadbased;

import com.google.common.collect.ImmutableList;
import org.ringingmaster.engine.compiler.common.CallDenormaliser;
import org.ringingmaster.engine.parser.assignparsetype.ParseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

import static org.ringingmaster.engine.parser.assignparsetype.ParseType.PLAIN_LEAD;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class BuildCallSequence implements Function<LeadBasedCompilePipelineData, LeadBasedCompilePipelineData> {
    private final Logger log = LoggerFactory.getLogger(BuildCallSequence.class);

    private final CallDenormaliser<LeadBasedDenormalisedCall> callDenormaliser = new CallDenormaliser<>() {

        protected LeadBasedDenormalisedCall buildDecomposedCall(String callName, ParseType parseType, State state) {
            return new LeadBasedDenormalisedCall(callName, state.getCurrentVariance(), parseType.equals(PLAIN_LEAD));
        }
    };

    @Override
    public LeadBasedCompilePipelineData apply(LeadBasedCompilePipelineData input) {
        if (input.isTerminated()) {
            return input;
        }

        log.debug("{} > creating call sequence", input.getLogPreamble());

        //TODO why do we not just havce the call sequence code in this class?
        final ImmutableList<LeadBasedDenormalisedCall> callSequence =
                callDenormaliser.createCallSequence(input.getParse(), input.getVarianceLookupByName(), input.getLogPreamble());

        log.debug("{} < creating call sequence", input.getLogPreamble());

        return input.setCallSequence(callSequence);
    }
}
