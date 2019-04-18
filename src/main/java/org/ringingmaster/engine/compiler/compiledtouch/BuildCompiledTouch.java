package org.ringingmaster.engine.compiler.compiledtouch;

import org.ringingmaster.engine.compiler.common.CompilePipelineData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

import static com.google.common.base.Preconditions.checkState;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class BuildCompiledTouch<T extends CompilePipelineData<T>> implements Function<T, CompiledTouch> {

    private final Logger log = LoggerFactory.getLogger(BuildCompiledTouch.class);

    @Override
    public CompiledTouch apply(T data) {

        log.debug("{} > build compiledTouch structures", data.getLogPreamble());


        checkState(data.isTerminated(), "Cannot build compiledTouch for unterminated touch [%s]", data);

        CompiledTouch compiledTouch = new DefaultCompiledTouch(
                data.getParse().getTouch(),
                data.getParse(),
                data.getTerminationReason().get(),
                data.getTerminateNotes(),
                data.getMethod(),
                -1);

        log.debug("{} > build compiledTouch structures", data.getLogPreamble());

        return compiledTouch;


    }
}
