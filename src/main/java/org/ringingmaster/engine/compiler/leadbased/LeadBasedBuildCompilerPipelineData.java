package org.ringingmaster.engine.compiler.leadbased;

import org.ringingmaster.engine.parser.parse.Parse;

import java.util.function.Function;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class LeadBasedBuildCompilerPipelineData implements Function<Parse, LeadBasedCompilerPipelineData> {

    @Override
    public LeadBasedCompilerPipelineData apply(Parse parse) {
        return new LeadBasedCompilerPipelineData(parse);
    }

}
