package org.ringingmaster.engine.compilernew.internaldata;

import com.google.common.collect.ImmutableList;
import org.ringingmaster.engine.compiler.impl.LeadBasedDecomposedCall;
import org.ringingmaster.engine.compilernew.proof.ProofTerminationReason;
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
public class LeadBasedCompilerPipelineData  extends CompilerPipelineData<LeadBasedCompilerPipelineData> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final ImmutableList<LeadBasedDecomposedCall> callSequence;


    public LeadBasedCompilerPipelineData(Parse parse) {
        this(parse, "", Optional.empty(),
                Optional.empty(), Optional.empty(),
                ImmutableList.of());
    }

    private LeadBasedCompilerPipelineData(Parse parse, String logPreamble,
                                          Optional<Method> method,
                                          Optional<ProofTerminationReason> terminationReason, Optional<String> terminateNotes,
                                          ImmutableList<LeadBasedDecomposedCall> callSequence) {
        super(parse, logPreamble, method, terminationReason, terminateNotes);
        this.callSequence = callSequence;
    }


    public LeadBasedCompilerPipelineData setCallSequence(ImmutableList<LeadBasedDecomposedCall> callSequence) {
        return new LeadBasedCompilerPipelineData(getParse(), getLogPreamble(),
                getCreatedMethod(),
                getTerminationReason(), getTerminateNotes(), callSequence);
    }

    public ImmutableList<LeadBasedDecomposedCall> getCallSequence() {
        return callSequence;
    }

    @Override
    protected LeadBasedCompilerPipelineData build(Parse parse, String logPreamble,
                                                  Optional<Method> method,
                                                  Optional<ProofTerminationReason> terminationReason, Optional<String> terminateNotes) {
        return new LeadBasedCompilerPipelineData(parse, logPreamble,
                method,
                terminationReason, terminateNotes,
                callSequence);
    }


}
