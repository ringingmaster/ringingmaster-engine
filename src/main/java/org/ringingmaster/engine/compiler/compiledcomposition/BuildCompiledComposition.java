package org.ringingmaster.engine.compiler.compiledcomposition;

import org.ringingmaster.engine.compiler.common.CompilerPipelineData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

import static com.google.common.base.Preconditions.checkState;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class BuildCompiledComposition<T extends CompilerPipelineData<T>> implements Function<T, CompiledComposition> {

    private final Logger log = LoggerFactory.getLogger(BuildCompiledComposition.class);

    @Override
    public CompiledComposition apply(T data) {

        log.debug("{} > build compiledComposition structures", data.getLogPreamble());


        checkState(data.isTerminated(), "Cannot build compiledComposition for unterminated composition [%s]", data);

        CompiledComposition compiledComposition = new DefaultCompiledComposition(
                data.getParse().getComposition(),
                data.getParse(),
                data.getTerminationReason().get(),
                data.getTerminateNotes(),
                data.getMethod(),
                -1);

        log.debug("{} > build compiledComposition structures", data.getLogPreamble());

        return compiledComposition;


    }
}
