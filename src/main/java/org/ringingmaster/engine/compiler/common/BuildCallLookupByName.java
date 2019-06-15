package org.ringingmaster.engine.compiler.common;

import com.google.common.collect.ImmutableMap;
import org.ringingmaster.engine.notation.Call;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
public class BuildCallLookupByName<T extends CompilerPipelineData<T>> implements Function<T, T> {

    private final Logger log = LoggerFactory.getLogger(BuildCallLookupByName.class);

    @Override
    public T apply(T data) {
        if (data.isTerminated()) {
            return data;
        }

        log.debug("{} > creating call lookup by name", data.getLogPreamble());

        ImmutableMap.Builder<String, Call> builder = ImmutableMap.builder();
        if (data.getParse().getComposition().getNonSplicedActiveNotation().isPresent()) {
            for (Call call : data.getParse().getComposition().getNonSplicedActiveNotation().get().getCalls()) {
                builder.put(call.getName(), call);
                builder.put(call.getNameShorthand(), call);
            }
        }

        ImmutableMap<String, Call> result = builder.build();
        log.debug("{} < creating call lookup by name [{}]", data.getLogPreamble(), result);
        return data.setCallLookupByName(result);
    }
}
