package org.ringingmaster.engine.compiler.coursebased;

import com.google.common.collect.ImmutableList;
import org.ringingmaster.engine.compiler.CompileTerminationReason;
import org.ringingmaster.engine.compiler.common.CompilePipelineData;
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

    private final ImmutableList<Optional<String>> callPositionLookupByColumn;
    private final ImmutableList<CourseBasedDenormalisedCall> callSequence;


    public CourseBasedCompilePipelineData(Parse parse) {
        this(parse, "[" + parse.getComposition().getTitle() + "]",
                Optional.empty(),
                Optional.empty(), Optional.empty(),
                ImmutableList.of(),
                ImmutableList.of());
    }

    private CourseBasedCompilePipelineData(Parse parse, String logPreamble,
                                           Optional<Method> method,
                                           Optional<CompileTerminationReason> terminationReason, Optional<String> terminateNotes,
                                           ImmutableList<Optional<String>> callPositionLookupByColumn,
                                           ImmutableList<CourseBasedDenormalisedCall> callSequence) {
        super(parse, logPreamble, method, terminationReason, terminateNotes);
        this.callPositionLookupByColumn = callPositionLookupByColumn;
        this.callSequence = callSequence;
    }

    @Override
    protected CourseBasedCompilePipelineData buildWhenBaseChanges(Parse parse, String logPreamble,
                                                                  Optional<Method> method,
                                                                  Optional<CompileTerminationReason> terminationReason, Optional<String> terminateNotes) {
        return new CourseBasedCompilePipelineData(parse, logPreamble,
                method,
                terminationReason, terminateNotes,
                callPositionLookupByColumn, callSequence);
    }

    public CourseBasedCompilePipelineData setCallSequence(ImmutableList<CourseBasedDenormalisedCall> callSequence) {
        return new CourseBasedCompilePipelineData(getParse(), getLogPreamble(),
                getMethod(),
                getTerminationReason(), getTerminateNotes(),
                getCallPositionNames(), callSequence);
    }

    public ImmutableList<CourseBasedDenormalisedCall> getCallPositionLookupByColumn() {
        return callSequence;
    }

    public CourseBasedCompilePipelineData setCallPositionLookupByColumn(ImmutableList<Optional<String>> callPositionNames) {
        return new CourseBasedCompilePipelineData(getParse(), getLogPreamble(),
                getMethod(),
                getTerminationReason(), getTerminateNotes(),
                callPositionNames, getCallPositionLookupByColumn());
    }

    public ImmutableList<Optional<String>> getCallPositionNames() {
        return callPositionLookupByColumn;
    }
}


