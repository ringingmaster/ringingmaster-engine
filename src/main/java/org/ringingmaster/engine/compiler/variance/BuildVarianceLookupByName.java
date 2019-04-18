package org.ringingmaster.engine.compiler.variance;

import com.google.common.collect.ImmutableMap;
import org.ringingmaster.engine.arraytable.BackingTableLocationAndValue;
import org.ringingmaster.engine.compiler.leadbased.LeadBasedCompilePipelineData;
import org.ringingmaster.engine.parser.cell.ParsedCell;
import org.ringingmaster.engine.parser.cell.grouping.Group;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

import static com.google.common.base.Preconditions.checkState;
import static org.ringingmaster.engine.compiler.variance.VarianceFactory.parseVariance;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.VARIANCE_DETAIL;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.VARIANCE_OPEN;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class BuildVarianceLookupByName implements Function<LeadBasedCompilePipelineData, LeadBasedCompilePipelineData> {
    private final Logger log = LoggerFactory.getLogger(BuildVarianceLookupByName.class);

    @Override
    public LeadBasedCompilePipelineData apply(LeadBasedCompilePipelineData input) {
        if (input.isTerminated()) {
            return input;
        }

        log.debug("{} > creating variance lookup", input.getLogPreamble());

        ImmutableMap.Builder<String, Variance> builder = ImmutableMap.builder();

        for (BackingTableLocationAndValue<ParsedCell> cellAndLocation : input.getParse().mainBodyCells()) {
            ParsedCell cell = cellAndLocation.getValue();
            for (Group group : cell.allGroups()) {
                if (group.isValid() &&
                    group.getFirstSectionParseType() == VARIANCE_OPEN) {

                    checkState(group.getSections().size() == 2);
                    checkState(group.getSections().get(1).getParseType() == VARIANCE_DETAIL);

                    String characters = cell.getCharacters(group.getSections().get(1)).toLowerCase();
                    Variance variance = parseVariance(characters);
                    log.debug("{} Adding variance [{}]=[{}]", input.getLogPreamble(), characters, variance);
                    builder.put(characters, variance);
                }
            }
        }

        //TODO also splice ???
        //TODO also definition ???

        log.debug("{} < creating variance lookup", input.getLogPreamble());

        ImmutableMap<String, Variance> varianceLookupByName = builder.build();
        if ( varianceLookupByName.size() == 0) {
            return input;
        }
        else {
            return input.setVarianceLookupByName(builder.build());
        }
    }

}
