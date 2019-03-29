package org.ringingmaster.engine.compilernew.internaldata;

import com.google.common.collect.ImmutableList;
import org.ringingmaster.engine.compiler.impl.CourseBasedDecomposedCall;
import org.ringingmaster.engine.compilernew.proof.ProofTerminationReason;
import org.ringingmaster.engine.method.Method;
import org.ringingmaster.engine.parser.parse.Parse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.concurrent.Immutable;
import java.util.Optional;

/**
 * TODO comments???
 *
 * @author stevelake
 */
@Immutable
public class CourseBasedCompilerPipelineData extends CompilerPipelineData<CourseBasedCompilerPipelineData> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final ImmutableList<Optional<String>> callPositionNames;
    private final ImmutableList<CourseBasedDecomposedCall> callSequence;


    public CourseBasedCompilerPipelineData(Parse parse) {
        this(parse, "",
                Optional.empty(),
                Optional.empty(), Optional.empty(),
                ImmutableList.of(),
                ImmutableList.of());
    }

    private CourseBasedCompilerPipelineData(Parse parse, String logPreamble,
                                            Optional<Method> method,
                                            Optional<ProofTerminationReason> terminationReason, Optional<String> terminateNotes,
                                            ImmutableList<Optional<String>> callPositionNames,
                                            ImmutableList<CourseBasedDecomposedCall> callSequence) {
        super(parse, logPreamble, method, terminationReason, terminateNotes);
        this.callPositionNames = callPositionNames;
        this.callSequence = callSequence;
    }

    @Override
    protected CourseBasedCompilerPipelineData build(Parse parse, String logPreamble,
                                                  Optional<Method> method,
                                                  Optional<ProofTerminationReason> terminationReason, Optional<String> terminateNotes) {
        return new CourseBasedCompilerPipelineData(parse, logPreamble,
                method,
                terminationReason, terminateNotes,
                callPositionNames, callSequence);
    }

    public CourseBasedCompilerPipelineData setCallSequence(ImmutableList<CourseBasedDecomposedCall> callSequence) {
        return new CourseBasedCompilerPipelineData(getParse(), getLogPreamble(), getCreatedMethod(),
                getTerminationReason(), getTerminateNotes(),
                getCallPositionNames(), callSequence);
    }

    public ImmutableList<CourseBasedDecomposedCall> getCallSequence() {
        return callSequence;
    }

    public CourseBasedCompilerPipelineData setCallPositionNames(ImmutableList<Optional<String>> callPositionNames) {
        return new CourseBasedCompilerPipelineData(getParse(), getLogPreamble(), getCreatedMethod(),
                getTerminationReason(), getTerminateNotes(), callPositionNames, getCallSequence());
    }

    public ImmutableList<Optional<String>> getCallPositionNames() {
        return callPositionNames;
    }
}


