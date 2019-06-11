package org.ringingmaster.engine.parser.observability;

import com.google.common.base.Function;
import org.ringingmaster.engine.arraytable.BackingTableLocationAndValue;
import org.ringingmaster.engine.parser.cell.ParsedCell;
import org.ringingmaster.engine.parser.parse.Parse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
public class  PrettyPrintCells implements Function<Parse, Parse> {

    private final Logger log = LoggerFactory.getLogger(PrettyPrintCells.class);

    @Override
    public Parse apply(Parse input) {
        log.debug("[{}] > pretty print cells (trace output only)", input.getComposition().getLoggingTag());

        if (log.isTraceEnabled()) {

            for (BackingTableLocationAndValue<ParsedCell> cell : input.allCompositionCells()) {
                log.trace("Composition Cell [{}/{}] = {}{}", cell.getCol(),cell.getRow(), System.lineSeparator(), cell.getValue().prettyPrint());
            }
            for (BackingTableLocationAndValue<ParsedCell> cell : input.allDefinitionCells()) {
                log.trace("Definition Cell [{}/{}] = {}{}", cell.getCol(),cell.getRow(), System.lineSeparator(), cell.getValue().prettyPrint());
            }
        }

        log.debug("[{}] < pretty print cells (trace output only)", input.getComposition().getLoggingTag());

        return input;
    }
}
