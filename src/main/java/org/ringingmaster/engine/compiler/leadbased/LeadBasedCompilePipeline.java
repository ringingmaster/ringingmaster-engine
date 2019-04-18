package org.ringingmaster.engine.compiler.leadbased;

import org.ringingmaster.engine.compiler.common.ValidCompositionCheck;
import org.ringingmaster.engine.compiler.compiledcomposition.BuildCompiledComposition;
import org.ringingmaster.engine.compiler.compiledcomposition.CompiledComposition;
import org.ringingmaster.engine.compiler.variance.BuildVarianceLookupByName;
import org.ringingmaster.engine.parser.parse.Parse;

import javax.annotation.concurrent.Immutable;
import java.util.function.Function;

/**
 * TODO comments???
 *
 * @author stevelake
 */
@Immutable
public class LeadBasedCompilePipeline implements Function<Parse, CompiledComposition> {


    @Override
    public CompiledComposition apply(Parse parse) {
//TODO Add in early terminate nechanisam
        return pipeline.apply(parse);
    }

    private static final Function<Parse, CompiledComposition> pipeline =
            new BuildLeadBasedPipelineData()
            .andThen(new ValidCompositionCheck<>())
            .andThen(new BuildVarianceLookupByName())
            .andThen(new BuildCallLookupByName())
            .andThen(new BuildCallSequence())
            .andThen(new LeadBasedCompile())
            .andThen(new BuildCompiledComposition<>());

}
