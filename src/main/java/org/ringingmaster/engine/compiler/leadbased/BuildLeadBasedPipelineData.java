package org.ringingmaster.engine.compiler.leadbased;

import org.ringingmaster.engine.parser.parse.Parse;

import java.util.function.Function;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class BuildLeadBasedPipelineData implements Function<Parse, LeadBasedCompilePipelineData> {

    @Override
    public LeadBasedCompilePipelineData apply(Parse parse) {
        return new LeadBasedCompilePipelineData(parse);
    }

}
