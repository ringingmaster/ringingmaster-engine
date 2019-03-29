package org.ringingmaster.engine.compiler.leadbased;

import org.ringingmaster.engine.compiler.proof.Proof;
import org.ringingmaster.engine.compiler.proof.impl.BuildProof;
import org.ringingmaster.engine.compiler.common.ValidTouchCheck;
import org.ringingmaster.engine.parser.parse.Parse;

import java.util.function.Function;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class LeadBasedCompilePipeline implements Function<Parse, Proof> {

    private final BuildLeadBasedPipelineData buildLeadBasedPipelineData = new BuildLeadBasedPipelineData();
    private final ValidTouchCheck<LeadBasedCompilePipelineData> validTouchCheck = new ValidTouchCheck<>();
    private final BuildCallSequence buildCallSequence = new BuildCallSequence();
    private final BuildCallLookupByName buildCallLookupByName = new BuildCallLookupByName();
    private final LeadBasedCompile compile = new LeadBasedCompile();

    private final BuildProof<LeadBasedCompilePipelineData> buildProof = new BuildProof<>();

    @Override
    public Proof apply(Parse parse) {

        return buildLeadBasedPipelineData
                .andThen(validTouchCheck)
                .andThen(buildCallSequence)
                .andThen(buildCallLookupByName)
                .andThen(compile)
                .andThen(buildProof)

                .apply(parse);
    }
}
