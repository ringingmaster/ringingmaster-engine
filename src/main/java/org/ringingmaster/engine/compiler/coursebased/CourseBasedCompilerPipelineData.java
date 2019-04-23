package org.ringingmaster.engine.compiler.coursebased;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.ringingmaster.engine.compiler.CompileTerminationReason;
import org.ringingmaster.engine.compiler.common.CompilerPipelineData;
import org.ringingmaster.engine.compiler.variance.Variance;
import org.ringingmaster.engine.method.Method;
import org.ringingmaster.engine.notation.Call;
import org.ringingmaster.engine.notation.CallingPosition;
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

    private final ImmutableList<Optional<String>> callingPositionNameLookupByColumn;
    private final ImmutableMap<CallingPosition, Integer> tenorPlaceForCallingPositionLookup;
    private final ImmutableList<CourseBasedDenormalisedCall> callSequence;


    CourseBasedCompilerPipelineData(Parse parse) {
        this(parse, "[" + parse.getComposition().getTitle() + "]",
                ImmutableMap.of(), ImmutableMap.of(),
                Optional.empty(),Optional.empty(), Optional.empty(),
                ImmutableList.of(),
                ImmutableMap.of(),
                ImmutableList.of());
    }

    private CourseBasedCompilerPipelineData(Parse parse, String logPreamble,
                                            ImmutableMap<String, Call> callLookupByName, ImmutableMap<String, Variance> varianceLookupByName,
                                            Optional<Method> method, Optional<CompileTerminationReason> terminationReason, Optional<String> terminateNotes,
                                            ImmutableList<Optional<String>> callingPositionNameLookupByColumn, ImmutableMap<CallingPosition, Integer> tenorPlaceForCallingPositionLookup, ImmutableList<CourseBasedDenormalisedCall> callSequence) {
        super(parse, logPreamble,
                callLookupByName, varianceLookupByName,
                method, terminationReason, terminateNotes);
        this.callingPositionNameLookupByColumn = callingPositionNameLookupByColumn;
        this.tenorPlaceForCallingPositionLookup = tenorPlaceForCallingPositionLookup;
        this.callSequence = callSequence;
    }

    ImmutableList<Optional<String>> getCallingPositionNameLookupByColumn() {
        return callingPositionNameLookupByColumn;
    }

    CourseBasedCompilerPipelineData setCallingPositionNameLookupByColumn(ImmutableList<Optional<String>> callingPositionNameLookupByColumn) {
        return new CourseBasedCompilerPipelineData(getParse(), getLogPreamble(),
                getCallLookupByName(), getVarianceLookupByName(),
                getMethod(), getTerminationReason(), getTerminateNotes(),
                callingPositionNameLookupByColumn, tenorPlaceForCallingPositionLookup, callSequence);
    }

    ImmutableList<CourseBasedDenormalisedCall> getCallSequence() {
        return callSequence;
    }

    CourseBasedCompilerPipelineData setCallSequence(ImmutableList<CourseBasedDenormalisedCall> callSequence) {
        return new CourseBasedCompilerPipelineData(getParse(), getLogPreamble(),
                getCallLookupByName(), getVarianceLookupByName(),
                getMethod(), getTerminationReason(), getTerminateNotes(),
                callingPositionNameLookupByColumn, tenorPlaceForCallingPositionLookup, callSequence);
    }

    public ImmutableMap<CallingPosition, Integer> getTenorPlaceForCallingPositionLookup() {
        return tenorPlaceForCallingPositionLookup;
    }

    CourseBasedCompilerPipelineData setTenorPlaceForCallingPositionLookup(ImmutableMap<CallingPosition, Integer> tenorPlaceForCallingPositionLookup) {
        return new CourseBasedCompilerPipelineData(getParse(), getLogPreamble(),
                getCallLookupByName(), getVarianceLookupByName(),
                getMethod(), getTerminationReason(), getTerminateNotes(),
                callingPositionNameLookupByColumn, tenorPlaceForCallingPositionLookup, callSequence);
    }

    @Override
    protected CourseBasedCompilerPipelineData buildWhenBaseChanges(Parse parse, String logPreamble,
                                                                   ImmutableMap<String, Call> callLookupByName, ImmutableMap<String, Variance> varianceLookupByName,
                                                                   Optional<Method> method, Optional<CompileTerminationReason> terminationReason, Optional<String> terminateNotes) {
        return new CourseBasedCompilerPipelineData(parse, logPreamble,
                callLookupByName, varianceLookupByName,
                method, terminationReason, terminateNotes,
                callingPositionNameLookupByColumn, tenorPlaceForCallingPositionLookup, callSequence);
    }
}


