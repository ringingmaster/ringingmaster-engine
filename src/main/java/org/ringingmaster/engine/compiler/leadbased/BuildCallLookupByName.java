package org.ringingmaster.engine.compiler.leadbased;

import com.google.common.collect.ImmutableMap;
import org.ringingmaster.engine.notation.NotationCall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class BuildCallLookupByName implements Function<LeadBasedCompilePipelineData, LeadBasedCompilePipelineData> {

    private final Logger log = LoggerFactory.getLogger(BuildCallLookupByName.class);

    @Override
    public LeadBasedCompilePipelineData apply(LeadBasedCompilePipelineData data) {
        if (data.isTerminated()) {
            return data;
        }

        log.debug("{} > creating call lookup by name", data.getLogPreamble());

        ImmutableMap.Builder<String, NotationCall> builder = ImmutableMap.builder();
        if (data.getParse().getTouch().getNonSplicedActiveNotation().isPresent()) {
            for (NotationCall notationCall : data.getParse().getTouch().getNonSplicedActiveNotation().get().getCalls()) {
                builder.put(notationCall.getName(), notationCall);
                builder.put(notationCall.getNameShorthand(), notationCall);
            }
        }

        ImmutableMap<String, NotationCall> result = builder.build();
        log.debug("{} < creating call lookup by name [{}]", data.getLogPreamble(), result);
        return data.setLookupByName(result);
    }
}
