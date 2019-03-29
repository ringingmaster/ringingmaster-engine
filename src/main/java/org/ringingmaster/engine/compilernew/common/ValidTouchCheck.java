package org.ringingmaster.engine.compilernew.common;

import org.ringingmaster.engine.compilernew.CompileTerminationReason;
import org.ringingmaster.engine.touch.Touch;

import java.util.function.Function;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class ValidTouchCheck<T extends CompilePipelineData<T>> implements Function<T, T> {

    @Override
    public T apply(T data) {
        final Touch touch = data.getParse().getUnderlyingTouch();
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

        return data;
    }
}
