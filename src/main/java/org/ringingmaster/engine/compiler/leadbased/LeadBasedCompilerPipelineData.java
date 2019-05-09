package org.ringingmaster.engine.compiler.leadbased;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.ringingmaster.engine.compiler.CompileTerminationReason;
import org.ringingmaster.engine.compiler.common.CompilerPipelineData;
import org.ringingmaster.engine.method.Method;
import org.ringingmaster.engine.notation.Call;
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
 * @author Steve Lake
 */
@Immutable
class LeadBasedCompilerPipelineData extends CompilerPipelineData<LeadBasedCompilerPipelineData> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final ImmutableList<LeadBasedDenormalisedCall> denormalisedCallSequence;


    LeadBasedCompilerPipelineData(Parse parse) {
        this(parse, "[" + parse.getComposition().getTitle() + "]",
                ImmutableMap.of(), ImmutableMap.of(),
                Optional.empty(), Optional.empty(), Optional.empty(),
                ImmutableList.of());
    }

    private LeadBasedCompilerPipelineData(Parse parse, String logPreamble,
                                          ImmutableMap<String, Call> callLookupByName, ImmutableMap<String, Variance> varianceLookupByName,
                                          Optional<Method> method, Optional<CompileTerminationReason> terminationReason, Optional<String> terminateNotes,
                                          ImmutableList<LeadBasedDenormalisedCall> denormalisedCallSequence) {
        super(parse, logPreamble,
                callLookupByName, varianceLookupByName,
                method, terminationReason, terminateNotes);
        this.denormalisedCallSequence = checkNotNull(denormalisedCallSequence);
    }

    ImmutableList<LeadBasedDenormalisedCall> getDenormalisedCallSequence() {
        return denormalisedCallSequence;
    }

    LeadBasedCompilerPipelineData setDenormalisedCallSequence(ImmutableList<LeadBasedDenormalisedCall> denormalisedCallSequence) {
        return new LeadBasedCompilerPipelineData(getParse(), getLogPreamble(),
                getCallLookupByName(), getVarianceLookupByName(),
                getMethod(), getTerminationReason(), getTerminateNotes(),
                denormalisedCallSequence);
    }


    @Override
    protected LeadBasedCompilerPipelineData buildWhenBaseChanges(Parse parse, String logPreamble,
                                                                 ImmutableMap<String, Call> callLookupByName, ImmutableMap<String, Variance> varianceLookupByName,
                                                                 Optional<Method> method, Optional<CompileTerminationReason> terminationReason, Optional<String> terminateNotes) {
        return new LeadBasedCompilerPipelineData(parse, logPreamble,
                callLookupByName, varianceLookupByName,
                method, terminationReason, terminateNotes,
                denormalisedCallSequence);
    }




}
