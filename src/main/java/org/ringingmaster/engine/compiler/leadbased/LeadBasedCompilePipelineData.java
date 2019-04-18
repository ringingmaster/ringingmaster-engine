package org.ringingmaster.engine.compiler.leadbased;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.ringingmaster.engine.compiler.CompileTerminationReason;
import org.ringingmaster.engine.compiler.common.CompilePipelineData;
import org.ringingmaster.engine.compilerold.impl.LeadBasedDecomposedCall;
import org.ringingmaster.engine.method.Method;
import org.ringingmaster.engine.notation.NotationCall;
import org.ringingmaster.engine.parser.parse.Parse;
import org.ringingmaster.engine.compiler.variance.Variance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.concurrent.Immutable;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO Comments
 *
 * @author Lake
 */
@Immutable
public class LeadBasedCompilePipelineData extends CompilePipelineData<LeadBasedCompilePipelineData> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final ImmutableList<LeadBasedDecomposedCall> callSequence;
    private final ImmutableMap<String, NotationCall> callLookupByName; //TODO should this be in the super class?
    private final ImmutableMap<String, Variance> varianceLookupByName; //TODO should this be in the super class?


    LeadBasedCompilePipelineData(Parse parse) {
        this(parse, "[" + parse.getComposition().getTitle() + "]",
                Optional.empty(),
                Optional.empty(), Optional.empty(),
                ImmutableList.of(), ImmutableMap.of(), ImmutableMap.of());
    }

    private LeadBasedCompilePipelineData(Parse parse, String logPreamble,
                                         Optional<Method> method,
                                         Optional<CompileTerminationReason> terminationReason, Optional<String> terminateNotes,
                                         ImmutableList<LeadBasedDecomposedCall> callSequence, ImmutableMap<String, NotationCall> callLookupByName, ImmutableMap<String, Variance> varianceLookupByName) {
        super(parse, logPreamble, method, terminationReason, terminateNotes);
        this.callSequence = checkNotNull(callSequence);
        this.callLookupByName = checkNotNull(callLookupByName);
        this.varianceLookupByName = checkNotNull(varianceLookupByName);
    }

    public ImmutableList<LeadBasedDecomposedCall> getCallSequence() {
        return callSequence;
    }

    public LeadBasedCompilePipelineData setCallSequence(ImmutableList<LeadBasedDecomposedCall> callSequence) {
        return new LeadBasedCompilePipelineData(getParse(), getLogPreamble(),
                getMethod(),
                getTerminationReason(), getTerminateNotes(),
                callSequence, callLookupByName, varianceLookupByName);
    }

    public ImmutableMap<String, NotationCall> getLookupByName() {
        return callLookupByName;
    }

    public LeadBasedCompilePipelineData setLookupByName(ImmutableMap<String, NotationCall> callLookupByName) {
        return new LeadBasedCompilePipelineData(getParse(), getLogPreamble(),
                getMethod(),
                getTerminationReason(), getTerminateNotes(),
                callSequence, callLookupByName, varianceLookupByName);
    }

    public ImmutableMap<String, Variance> getVarianceLookupByName() {
        return varianceLookupByName;
    }

    public LeadBasedCompilePipelineData setVarianceLookupByName(ImmutableMap<String, Variance> varianceLookupByName) {
        return new LeadBasedCompilePipelineData(getParse(), getLogPreamble(),
                getMethod(),
                getTerminationReason(), getTerminateNotes(),
                callSequence, callLookupByName, varianceLookupByName);
    }


    @Override
    protected LeadBasedCompilePipelineData buildWhenBaseChanges(Parse parse, String logPreamble,
                                                                Optional<Method> method,
                                                                Optional<CompileTerminationReason> terminationReason, Optional<String> terminateNotes) {
        return new LeadBasedCompilePipelineData(parse, logPreamble,
                method,
                terminationReason, terminateNotes,
                callSequence, callLookupByName, varianceLookupByName);
    }




}
