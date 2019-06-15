package org.ringingmaster.engine.parser.observability;

import com.google.common.base.Function;
import org.ringingmaster.engine.parser.parse.Parse;
import org.ringingmaster.engine.parser.parse.ParseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
public class SetEndTime implements Function<Parse, Parse> {

    private final Logger log = LoggerFactory.getLogger(SetEndTime.class);

    @Override
    public Parse apply(Parse input) {
        log.debug("[{}] > set end time)", input.getComposition().getLoggingTag());

        ParseBuilder parseBuilder = new ParseBuilder()
                .prototypeOf(input)
                .setEndMs(System.currentTimeMillis());

        log.debug("[{}] < set end time)", input.getComposition().getLoggingTag());

        return parseBuilder.build();
    }
}
