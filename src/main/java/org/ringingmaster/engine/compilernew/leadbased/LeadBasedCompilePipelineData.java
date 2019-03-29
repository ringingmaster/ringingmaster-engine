package org.ringingmaster.engine.compilernew.leadbased;

import com.google.common.collect.ImmutableList;
import org.ringingmaster.engine.compiler.impl.LeadBasedDecomposedCall;
import org.ringingmaster.engine.compilernew.common.CompilePipelineData;
import org.ringingmaster.engine.compilernew.CompileTerminationReason;
import org.ringingmaster.engine.method.Method;
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


    public LeadBasedCompilePipelineData(Parse parse) {
        this(parse, "[" + parse.getUnderlyingTouch().getTitle() + "]", Optional.empty(),
                Optional.empty(), Optional.empty(),
                ImmutableList.of());
    }

    private LeadBasedCompilePipelineData(Parse parse, String logPreamble,
                                         Optional<Method> method,
                                         Optional<CompileTerminationReason> terminationReason, Optional<String> terminateNotes,
                                         ImmutableList<LeadBasedDecomposedCall> callSequence) {
        super(parse, logPreamble, method, terminationReason, terminateNotes);
        this.callSequence = callSequence;
    }


    public LeadBasedCompilePipelineData setCallSequence(ImmutableList<LeadBasedDecomposedCall> callSequence) {
        return new LeadBasedCompilePipelineData(getParse(), getLogPreamble(),
                getCreatedMethod(),
                getTerminationReason(), getTerminateNotes(), callSequence);
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
                callSequence);
    }


}
