package org.ringingmaster.engine.compiler.coursebased;

import com.google.common.collect.ImmutableList;
import org.ringingmaster.engine.compiler.denormaliser.CallDenormaliser;
import org.ringingmaster.engine.parser.assignparsetype.ParseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.concurrent.Immutable;
import java.util.Optional;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkPositionIndex;
import static com.google.common.base.Preconditions.checkState;

/**
 * TODO comments???
 *
 * @author stevelake
 */
@Immutable
public class CourseBasedBuildCallSequence implements Function<CourseBasedCompilerPipelineData, CourseBasedCompilerPipelineData> {
    private final Logger log = LoggerFactory.getLogger(CourseBasedBuildCallSequence.class);

    private final CallDenormaliser<CourseBasedDenormalisedCall, CourseBasedCompilerPipelineData> callDenormaliser = new CallDenormaliser<>() {

        @Override
        protected CourseBasedDenormalisedCall buildDecomposedCall(String callName, ParseType parseType, State state, CourseBasedCompilerPipelineData input) {

            ImmutableList<Optional<String>> callPositionLookupByColumn = input.getCallPositionLookupByColumn();

            checkPositionIndex(state.getColumnIndex(), callPositionLookupByColumn.size(), "column index out of bounds");
            Optional<String> callPositionName = callPositionLookupByColumn.get(state.getColumnIndex());
            checkState(callPositionName.isPresent(), "callPositionName is missing. Check that the parsing is correctly excluding columns with no valid call position");

            return new CourseBasedDenormalisedCall(callName, state.getCurrentVariance(), callPositionName.get());
        }
    };




    @Override
    public CourseBasedCompilerPipelineData apply(CourseBasedCompilerPipelineData input) {
        if (input.isTerminated()) {
            return input;
        }

        log.debug("{} > creating call sequence", input.getLogPreamble());

        final ImmutableList<CourseBasedDenormalisedCall> callSequence =
                callDenormaliser.createCallSequence(input.getParse(), input.getVarianceLookupByName(), input.getLogPreamble(), input);

        log.debug("{} < creating call sequence", input.getLogPreamble());

        return input.setCallSequence(callSequence);
    }
}
