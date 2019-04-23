package org.ringingmaster.engine.compiler.coursebased;


import org.ringingmaster.engine.compiler.compile.Compile;
import org.ringingmaster.engine.composition.compositiontype.CompositionType;
import org.ringingmaster.engine.method.Bell;
import org.ringingmaster.engine.notation.Call;
import org.ringingmaster.engine.notation.CallingPosition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.concurrent.ThreadSafe;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
@ThreadSafe
class CourseBasedCompile implements Function<CourseBasedCompilerPipelineData, CourseBasedCompilerPipelineData> {

    private final Logger log = LoggerFactory.getLogger(CourseBasedCompile.class);

    private final Compile<CourseBasedDenormalisedCall, CourseBasedCompilerPipelineData> compile = new Compile<>() {

        @Override
        protected boolean applyNextCall(State state) {

            // Find the method calling position.
            CourseBasedDenormalisedCall nextCall = state.getNextDenormalisedCall();
            String callingPositionName = nextCall.getCallingPositionName();

            CallingPosition methodCallingPosition = state.getMaskedNotation().findMethodBasedCallingPositionByName(callingPositionName);
            checkState(methodCallingPosition != null, "Can't find calling position [" + callingPositionName + "] " +
                    "in notation [" + state.getMaskedNotation().getName() + "]");

            // check we are at the correct call initiation row
            if (methodCallingPosition.getCallInitiationRow() != state.getMaskedNotation().getIteratorIndex()) {
                return false;
            }

            // Is our designated calling bell on the correct lead (of the tenor)
            Bell callFromBell = state.getComposition().getCallFromBell();
            int positionOfCallingBell = state.getCurrentRow().getPlaceOfBell(callFromBell);
            Integer place = state.getPassthrough().getTenorPlaceForCallingPositionLookup().get(methodCallingPosition);
            if (positionOfCallingBell != place) {
                return false;
            }

            // find and make the call
            Call call = state.getCallLookupByName().get(nextCall.getCallName());
            checkNotNull(call);
            state.getMaskedNotation().applyCall(call, state.getLogPreamble());
            return true;
        }

    };

    @Override
    public CourseBasedCompilerPipelineData apply(CourseBasedCompilerPipelineData input) {

        if (input.isTerminated()) {
            return input;
        }
        log.debug("{} > compile course based composition", input.getLogPreamble());

        checkArgument(input.getParse().getComposition().getCompositionType() == CompositionType.COURSE_BASED, "Course based compiler must use a LEAD_BASED composition. Is actually [" + input.getParse().getComposition().getCompositionType() + "]");

        Compile.Result compileResult = compile.compileComposition(
                input.getParse().getComposition(),
                input.getCallSequence(),
                input.getCallLookupByName(),
                input.getLogPreamble(),
                input);

        CourseBasedCompilerPipelineData result = input
                .terminate(compileResult.getTerminateReason(), compileResult.getTerminateNotes())
                .setMethod(compileResult.getMethod());

        log.debug("{} < compile course based composition", input.getLogPreamble());

        return result;
    }

}
