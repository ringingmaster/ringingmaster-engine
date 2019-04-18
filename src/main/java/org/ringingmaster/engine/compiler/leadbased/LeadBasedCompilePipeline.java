package org.ringingmaster.engine.compiler.leadbased;

import org.ringingmaster.engine.compiler.common.ValidTouchCheck;
import org.ringingmaster.engine.compiler.compiledtouch.BuildCompiledTouch;
import org.ringingmaster.engine.compiler.compiledtouch.CompiledTouch;
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
public class LeadBasedCompilePipeline implements Function<Parse, CompiledTouch> {


    @Override
    public CompiledTouch apply(Parse parse) {
//TODO Add in early terminate nechanisam
        return pipeline.apply(parse);
    }

    private static final Function<Parse, CompiledTouch> pipeline =
            new BuildLeadBasedPipelineData()
            .andThen(new ValidTouchCheck<>())
            .andThen(new BuildVarianceLookupByName())
            .andThen(new BuildCallLookupByName())
            .andThen(new BuildCallSequence())
            .andThen(new LeadBasedCompile())
            .andThen(new BuildCompiledTouch<>());

}
