package org.ringingmaster.engine.compilernew.leadbased;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.ringingmaster.engine.compiler.impl.LeadBasedDecomposedCall;
import org.ringingmaster.engine.compilernew.CompileTerminationReason;
import org.ringingmaster.engine.compilernew.common.CompilePipelineData;
import org.ringingmaster.engine.method.Method;
import org.ringingmaster.engine.notation.NotationCall;
import org.ringingmaster.engine.parser.parse.Parse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.concurrent.Immutable;
import java.util.Optional;

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


    public LeadBasedCompilePipelineData(Parse parse) {
        this(parse, "[" + parse.getUnderlyingTouch().getTitle() + "]", Optional.empty(),
                Optional.empty(), Optional.empty(),
                ImmutableList.of(), ImmutableMap.of());
    }

    private LeadBasedCompilePipelineData(Parse parse, String logPreamble,
                                         Optional<Method> method,
                                         Optional<CompileTerminationReason> terminationReason, Optional<String> terminateNotes,
                                         ImmutableList<LeadBasedDecomposedCall> callSequence, ImmutableMap<String, NotationCall> callLookupByName) {
        super(parse, logPreamble, method, terminationReason, terminateNotes);
        this.callSequence = callSequence;
        this.callLookupByName = callLookupByName;
    }

    public LeadBasedCompilePipelineData setCallSequence(ImmutableList<LeadBasedDecomposedCall> callSequence) {
        return new LeadBasedCompilePipelineData(getParse(), getLogPreamble(),
                getCreatedMethod(),
                getTerminationReason(), getTerminateNotes(),
                callSequence, callLookupByName);
    }

    public ImmutableMap<String, NotationCall> getLookupByName() {
        return callLookupByName;
    }

    public LeadBasedCompilePipelineData setLookupByName(ImmutableMap<String, NotationCall> callLookupByName) {
        return new LeadBasedCompilePipelineData(getParse(), getLogPreamble(),
                getCreatedMethod(),
                getTerminationReason(), getTerminateNotes(),
                callSequence, callLookupByName);
    }

    public ImmutableList<LeadBasedDecomposedCall> getCallSequence() {
        return callSequence;
    }

    @Override
    protected LeadBasedCompilePipelineData build(Parse parse, String logPreamble,
                                                 Optional<Method> method,
                                                 Optional<CompileTerminationReason> terminationReason, Optional<String> terminateNotes) {
        return new LeadBasedCompilePipelineData(parse, logPreamble,
                method,
                terminationReason, terminateNotes,
                callSequence, callLookupByName);
    }


}
