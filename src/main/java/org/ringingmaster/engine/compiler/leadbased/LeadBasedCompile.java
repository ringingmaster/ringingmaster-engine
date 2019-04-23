package org.ringingmaster.engine.compiler.leadbased;


import org.ringingmaster.engine.compiler.compile.Compile;
import org.ringingmaster.engine.composition.compositiontype.CompositionType;
import org.ringingmaster.engine.notation.Call;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.concurrent.ThreadSafe;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
@ThreadSafe
class LeadBasedCompile implements Function<LeadBasedCompilerPipelineData, LeadBasedCompilerPipelineData> {

    private final Logger log = LoggerFactory.getLogger(LeadBasedCompile.class);

    private final Compile<LeadBasedDenormalisedCall, LeadBasedCompilerPipelineData> compile = new Compile<>() {

        @Override
        protected boolean applyNextCall(State state) {
            if (state.getNextDenormalisedCall().isPlainLead()) {
                // No Call, but consume the call.
                log.debug("{}    Apply Plain lead", state.getLogPreamble());
            }
            else {
                Call call = state.getCallLookupByName().get(state.getNextDenormalisedCall().getCallName());
                log.debug("{}    Apply call [{}]", state.getLogPreamble(), call);
                state.getMaskedNotation().applyCall(call, state.getLogPreamble());
            }
            // We consumed the call
            return true;
        }
    };

    @Override
    public LeadBasedCompilerPipelineData apply(LeadBasedCompilerPipelineData input) {

        if (input.isTerminated()) {
            return input;
        }
        log.debug("{} > compile lead based composition", input.getLogPreamble());

        checkArgument(input.getParse().getComposition().getCompositionType() == CompositionType.LEAD_BASED, "Lead based compiler must use a LEAD_BASED composition. Is actually [" + input.getParse().getComposition().getCompositionType() + "]");

        Compile.Result compileResult = compile.compileComposition(
                input.getParse().getComposition(),
                input.getDenormalisedCallSequence(),
                input.getCallLookupByName(),
                input.getLogPreamble(),
                input);

        LeadBasedCompilerPipelineData result = input
                .terminate(compileResult.getTerminateReason(), compileResult.getTerminateNotes())
                .setMethod(compileResult.getMethod());

        log.debug("{} < compile lead based composition", input.getLogPreamble());

        return result;
    }


}
