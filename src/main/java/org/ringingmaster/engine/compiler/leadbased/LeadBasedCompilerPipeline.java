package org.ringingmaster.engine.compiler.leadbased;

import org.ringingmaster.engine.compiler.common.BuildCallLookupByName;
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
public class LeadBasedCompilerPipeline implements Function<Parse, CompiledComposition> {


    @Override
    public CompiledComposition apply(Parse parse) {
//TODO Add in early terminate mechanism
        return pipeline.apply(parse);
    }

    private static final Function<Parse, CompiledComposition> pipeline =
            new LeadBasedBuildCompilerPipelineData()
            .andThen(new ValidCompositionCheck<>())
            .andThen(new BuildVarianceLookupByName<>())
            .andThen(new BuildCallLookupByName())
            .andThen(new LeadBasedBuildCallSequence())
            .andThen(new LeadBasedCompile())
            .andThen(new BuildCompiledComposition<>());

}
