package org.ringingmaster.engine.compilernew.leadbased;

import org.ringingmaster.engine.compilernew.internaldata.LeadBasedCompilerPipelineData;
import org.ringingmaster.engine.compilernew.proof.Proof;
import org.ringingmaster.engine.compilernew.proof.impl.BuildProof;
import org.ringingmaster.engine.compilernew.validity.CommonValidTouchCheck;
import org.ringingmaster.engine.parser.parse.Parse;

import java.util.function.Function;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class LeadBasedPipeline implements Function<Parse, Proof> {

    private final BuildLeadBasedPipelineData buildLeadBasedPipelineData = new BuildLeadBasedPipelineData();
    private final CommonValidTouchCheck<LeadBasedCompilerPipelineData> commonValidTouchCheck = new CommonValidTouchCheck<>();

    private final BuildProof<LeadBasedCompilerPipelineData> buildProof = new BuildProof<>();

    @Override
    public Proof apply(Parse parse) {

        return buildLeadBasedPipelineData
                .andThen(commonValidTouchCheck)

                .andThen(buildProof)

                .apply(parse);
    }
}
