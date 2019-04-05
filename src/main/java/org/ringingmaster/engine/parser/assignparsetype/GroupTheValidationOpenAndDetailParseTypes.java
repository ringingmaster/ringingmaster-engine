package org.ringingmaster.engine.parser.assignparsetype;

import com.google.common.base.Function;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.ringingmaster.engine.parser.parse.Parse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Given a set Variance characters that have been parsed
 *
 *   [-o]
 *
 *   parsed as the following groups:
 *
 *  |             |                               |              |
 *  |        [    |        -               o      |        ]     |
 *   VARIANCE_OPEN VARIANCE_DETAIL VARIANCE_DETAIL VARIANCE_CLOSE
 *
 *  Combine into a single group for open and detail:
 *
 *  |                                             |              |
 *  |        [             -               o      |        ]     |
 *   VARIANCE_OPEN VARIANCE_DETAIL VARIANCE_DETAIL VARIANCE_CLOSE
 *
 * @author Steve Lake
 */
public class GroupTheValidationOpenAndDetailParseTypes implements Function<Parse, Parse> {

    private final Logger log = LoggerFactory.getLogger(GroupTheValidationOpenAndDetailParseTypes.class);

    @Nullable
    @Override
    public Parse apply(@Nullable Parse input) {
        log.debug("[{}] > group the validation open and detail parse types", input.getUnderlyingTouch().getTitle());

        log.debug("[{}] < group the validation open and detail parse types", input.getUnderlyingTouch().getTitle());

        return input;
    }
}
