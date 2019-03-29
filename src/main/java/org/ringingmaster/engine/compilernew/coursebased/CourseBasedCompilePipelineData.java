package org.ringingmaster.engine.compilernew.coursebased;

import com.google.common.collect.ImmutableList;
import org.ringingmaster.engine.compiler.impl.CourseBasedDecomposedCall;
import org.ringingmaster.engine.compilernew.CompileTerminationReason;
import org.ringingmaster.engine.compilernew.common.CompilePipelineData;
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
public class CourseBasedCompilePipelineData extends CompilePipelineData<CourseBasedCompilePipelineData> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final ImmutableList<Optional<String>> callPositionNames;
    private final ImmutableList<CourseBasedDecomposedCall> callSequence;


    public CourseBasedCompilePipelineData(Parse parse) {
        this(parse, "[" + parse.getUnderlyingTouch().getTitle() + "]", Optional.empty(),
                Optional.empty(), Optional.empty(),
                ImmutableList.of(),
                ImmutableList.of());
    }

    private CourseBasedCompilePipelineData(Parse parse, String logPreamble,
                                           Optional<Method> method,
                                           Optional<CompileTerminationReason> terminationReason, Optional<String> terminateNotes,
                                           ImmutableList<Optional<String>> callPositionNames,
                                           ImmutableList<CourseBasedDecomposedCall> callSequence) {
        super(parse, logPreamble, method, terminationReason, terminateNotes);
        this.callPositionNames = callPositionNames;
        this.callSequence = callSequence;
    }

    @Override
    protected CourseBasedCompilePipelineData build(Parse parse, String logPreamble,
                                                   Optional<Method> method,
                                                   Optional<CompileTerminationReason> terminationReason, Optional<String> terminateNotes) {
        return new CourseBasedCompilePipelineData(parse, logPreamble,
                method,
                terminationReason, terminateNotes,
                callPositionNames, callSequence);
    }

    public CourseBasedCompilePipelineData setCallSequence(ImmutableList<CourseBasedDecomposedCall> callSequence) {
        return new CourseBasedCompilePipelineData(getParse(), getLogPreamble(), getCreatedMethod(),
                getTerminationReason(), getTerminateNotes(),
                getCallPositionNames(), callSequence);
    }

    public ImmutableList<CourseBasedDecomposedCall> getCallSequence() {
        return callSequence;
    }

    public CourseBasedCompilePipelineData setCallPositionNames(ImmutableList<Optional<String>> callPositionNames) {
        return new CourseBasedCompilePipelineData(getParse(), getLogPreamble(), getCreatedMethod(),
                getTerminationReason(), getTerminateNotes(), callPositionNames, getCallSequence());
    }

    public ImmutableList<Optional<String>> getCallPositionNames() {
        return callPositionNames;
    }
}


