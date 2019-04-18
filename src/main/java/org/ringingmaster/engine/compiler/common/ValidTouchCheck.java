package org.ringingmaster.engine.compiler.common;

import org.ringingmaster.engine.compiler.CompileTerminationReason;
import org.ringingmaster.engine.touch.Touch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class ValidTouchCheck<T extends CompilePipelineData<T>> implements Function<T, T> {

    private final Logger log = LoggerFactory.getLogger(ValidTouchCheck.class);

    @Override
    public T apply(T data) {
        log.debug("{} > checking for touch validity", data.getLogPreamble());

        final Touch touch = data.getParse().getTouch();
        if (touch.isSpliced()) {
            if (touch.getAvailableNotations().size() == 0) {
                return data.terminate(CompileTerminationReason.INVALID_TOUCH, "Spliced touch has no valid methods");
            }
        }
        else { // Not Spliced
            if (!touch.getNonSplicedActiveNotation().isPresent()) {
                return data.terminate(CompileTerminationReason.INVALID_TOUCH, "No active method");
            }
        }

        log.debug("{} < checking for touch validity", data.getLogPreamble());

        return data;
    }
}
