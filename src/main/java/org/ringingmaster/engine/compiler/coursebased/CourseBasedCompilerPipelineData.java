package org.ringingmaster.engine.compiler.coursebased;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.ringingmaster.engine.compiler.CompileTerminationReason;
import org.ringingmaster.engine.compiler.common.CompilePipelineData;
import org.ringingmaster.engine.compiler.variance.Variance;
import org.ringingmaster.engine.method.Method;
import org.ringingmaster.engine.notation.NotationCall;
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
public class CourseBasedCompilerPipelineData extends CompilePipelineData<CourseBasedCompilerPipelineData> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final ImmutableList<Optional<String>> callPositionLookupByColumn;
    private final ImmutableList<CourseBasedDenormalisedCall> callSequence;


    CourseBasedCompilerPipelineData(Parse parse) {
        this(parse, "[" + parse.getComposition().getTitle() + "]",
                ImmutableMap.of(), ImmutableMap.of(),
                Optional.empty(),Optional.empty(), Optional.empty(),
                ImmutableList.of(),
                ImmutableList.of());
    }

    private CourseBasedCompilerPipelineData(Parse parse, String logPreamble,
                                            ImmutableMap<String, NotationCall> callLookupByName, ImmutableMap<String, Variance> varianceLookupByName,
                                            Optional<Method> method, Optional<CompileTerminationReason> terminationReason, Optional<String> terminateNotes,
                                            ImmutableList<Optional<String>> callPositionLookupByColumn, ImmutableList<CourseBasedDenormalisedCall> callSequence) {
        super(parse, logPreamble,
                callLookupByName, varianceLookupByName,
                method, terminationReason, terminateNotes);
        this.callPositionLookupByColumn = callPositionLookupByColumn;
        this.callSequence = callSequence;
    }

    ImmutableList<Optional<String>> getCallPositionLookupByColumn() {
        return callPositionLookupByColumn;
    }

    CourseBasedCompilerPipelineData setCallPositionLookupByColumn(ImmutableList<Optional<String>> callPositionLookupByColumn) {
        return new CourseBasedCompilerPipelineData(getParse(), getLogPreamble(),
                getCallLookupByName(), getVarianceLookupByName(),
                getMethod(), getTerminationReason(), getTerminateNotes(),
                callPositionLookupByColumn, callSequence);
    }

    ImmutableList<CourseBasedDenormalisedCall> getCallSequence() {
        return callSequence;
    }

    CourseBasedCompilerPipelineData setCallSequence(ImmutableList<CourseBasedDenormalisedCall> callSequence) {
        return new CourseBasedCompilerPipelineData(getParse(), getLogPreamble(),
                getCallLookupByName(), getVarianceLookupByName(),
                getMethod(), getTerminationReason(), getTerminateNotes(),
                callPositionLookupByColumn, callSequence);
    }


    @Override
    protected CourseBasedCompilerPipelineData buildWhenBaseChanges(Parse parse, String logPreamble,
                                                                   ImmutableMap<String, NotationCall> callLookupByName, ImmutableMap<String, Variance> varianceLookupByName,
                                                                   Optional<Method> method, Optional<CompileTerminationReason> terminationReason, Optional<String> terminateNotes) {
        return new CourseBasedCompilerPipelineData(parse, logPreamble,
                callLookupByName, varianceLookupByName,
                method, terminationReason, terminateNotes,
                callPositionLookupByColumn, callSequence);
    }
}


