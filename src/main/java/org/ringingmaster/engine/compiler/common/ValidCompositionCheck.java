package org.ringingmaster.engine.compiler.common;

import org.ringingmaster.engine.compiler.CompileTerminationReason;
import org.ringingmaster.engine.composition.Composition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class ValidCompositionCheck<T extends CompilePipelineData<T>> implements Function<T, T> {

    private final Logger log = LoggerFactory.getLogger(ValidCompositionCheck.class);

    @Override
    public T apply(T data) {
        log.debug("{} > checking for composition validity", data.getLogPreamble());

        final Composition composition = data.getParse().getComposition();
        if (composition.isSpliced()) {
            if (composition.getAvailableNotations().size() == 0) {
                return data.terminate(CompileTerminationReason.INVALID_COMPOSITION, "Spliced composition has no valid methods");
            }
        }
        else { // Not Spliced
            if (!composition.getNonSplicedActiveNotation().isPresent()) {
                return data.terminate(CompileTerminationReason.INVALID_COMPOSITION, "No active method");
            }
        }

        log.debug("{} < checking for composition validity", data.getLogPreamble());

        return data;
    }
}
