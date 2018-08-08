package org.ringingmaster.engine.compilernew.leadbased;

import org.ringingmaster.engine.compilernew.internaldata.LeadBasedCompilerPipelineData;
import org.ringingmaster.engine.parser.parse.Parse;

import java.util.function.Function;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class BuildLeadBasedPipelineData implements Function<Parse, LeadBasedCompilerPipelineData> {

    @Override
    public LeadBasedCompilerPipelineData apply(Parse parse) {
        return new LeadBasedCompilerPipelineData(parse);
    }

}
