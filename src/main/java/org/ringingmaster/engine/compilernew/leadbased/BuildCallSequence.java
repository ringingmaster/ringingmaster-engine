package org.ringingmaster.engine.compilernew.leadbased;

import com.google.common.collect.ImmutableList;
import org.ringingmaster.engine.compiler.impl.LeadBasedDecomposedCall;
import org.ringingmaster.engine.compilernew.call.CallDecomposer;
import org.ringingmaster.engine.compilernew.internaldata.LeadBasedCompilerPipelineData;

import java.util.function.Function;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class BuildCallSequence implements Function<LeadBasedCompilerPipelineData, LeadBasedCompilerPipelineData> {

    private final CallDecomposer callDecomposer = new CallDecomposer();

    @Override
    public LeadBasedCompilerPipelineData apply(LeadBasedCompilerPipelineData data) {
        final ImmutableList<LeadBasedDecomposedCall> callSequence = callDecomposer.createCallSequence(data.getParse(), data.getLogPreamble());
        return data.setCallSequence(callSequence);
    }
}
