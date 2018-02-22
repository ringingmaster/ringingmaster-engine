package org.ringingmaster.engine.compilernew.coursebased;

import com.google.common.collect.ImmutableList;
import org.ringingmaster.engine.compiler.impl.CourseBasedDecomposedCall;
import org.ringingmaster.engine.compilernew.proof.ProofTerminationReason;
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
public class CourseBasedCompilerPipelineData {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final Parse parse;
    private final String logPreamble;
    private final Optional<ProofTerminationReason> terminationReason;
    private final Optional<String> terminateNotes;
    private final ImmutableList<Optional<String>> callPositionNames;
    private final ImmutableList<CourseBasedDecomposedCall> callSequence;


    public CourseBasedCompilerPipelineData(Parse parse) {
        this(parse, "",
                Optional.empty(), Optional.empty(),
                ImmutableList.of(),
                ImmutableList.of());
    }

    private CourseBasedCompilerPipelineData(Parse parse, String logPreamble,
                                            Optional<ProofTerminationReason> terminationReason, Optional<String> terminateNotes,
                                            ImmutableList<Optional<String>> callPositionNames,
                                            ImmutableList<CourseBasedDecomposedCall> callSequence) {
        this.parse = parse;
        this.logPreamble = logPreamble;
        this.terminationReason = terminationReason;
        this.terminateNotes = terminateNotes;
        this.callPositionNames = callPositionNames;
        this.callSequence = callSequence;
    }

    public Parse getParse() {
        return parse;
    }

    public String getLogPreamble() {
        return logPreamble;
    }

    public CourseBasedCompilerPipelineData terminate(final ProofTerminationReason terminationReason, String terminateNotes) {
        if (!isTerminated()) {
            log.debug("{}  - Terminate [{}] {}", logPreamble, terminateNotes, terminationReason);
            return new CourseBasedCompilerPipelineData(parse, logPreamble, Optional.of(terminationReason), Optional.of(terminateNotes), callPositionNames, callSequence);
        }
        else  {
            log.warn("Requesting second terminate [{}]{}", terminationReason, terminateNotes);
            return this;
        }
    }

    public Optional<ProofTerminationReason> getTerminationReason() {
        return terminationReason;
    }

    public Optional<String> getTerminateNotes() {
        return terminateNotes;
    }

    public boolean isTerminated() {
        return terminationReason.isPresent();
    }

    public CourseBasedCompilerPipelineData setCallSequence(ImmutableList<CourseBasedDecomposedCall> callSequence) {
        return new CourseBasedCompilerPipelineData(parse, logPreamble, terminationReason, terminateNotes, callPositionNames, callSequence);
    }

    public ImmutableList<CourseBasedDecomposedCall> getCallSequence() {
        return callSequence;
    }

    public CourseBasedCompilerPipelineData setCallPositionNames(ImmutableList<Optional<String>> callPositionNames) {
        return new CourseBasedCompilerPipelineData(parse, logPreamble, terminationReason, terminateNotes, callPositionNames, callSequence);
    }

    public ImmutableList<Optional<String>> getCallPositionNames() {
        return callPositionNames;
    }
}


