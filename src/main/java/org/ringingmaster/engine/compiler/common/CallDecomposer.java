package org.ringingmaster.engine.compiler.common;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import org.ringingmaster.engine.arraytable.BackingTableLocationAndValue;
import org.ringingmaster.engine.arraytable.ImmutableArrayTable;
import org.ringingmaster.engine.compiler.variance.Variance;
import org.ringingmaster.engine.compiler.variance.VarianceFactory;
import org.ringingmaster.engine.compilerold.impl.LeadBasedDecomposedCall;
import org.ringingmaster.engine.parser.assignparsetype.ParseType;
import org.ringingmaster.engine.parser.cell.ParsedCell;
import org.ringingmaster.engine.parser.cell.grouping.Group;
import org.ringingmaster.engine.parser.cell.grouping.Section;
import org.ringingmaster.engine.parser.parse.Parse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.concurrent.Immutable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.CALL;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.CALL_MULTIPLIER;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.PLAIN_LEAD;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.PLAIN_LEAD_MULTIPLIER;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.VARIANCE_DETAIL;
import static org.ringingmaster.engine.touch.tableaccess.DefinitionTableAccess.DEFINITION_COLUMN;

/**
 * TODO comments???
 *
 * @author stevelake
 */
@Immutable
public class CallDecomposer {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    class State {
        final Parse parse;
        final ImmutableMap<String, Variance> varianceLookupByName;
        final String logPreamble;
        final Deque<CallSequenceMultiplier> multiplierFIFO = new ArrayDeque<>();
        Variance currentVariance = VarianceFactory.nullVariance();

        public State(Parse parse, ImmutableMap<String, Variance> varianceLookupByName, String logPreamble) {
            this.parse = parse;
            this.varianceLookupByName = varianceLookupByName;
            this.logPreamble = logPreamble;
        }
    }



    // TODO Can this can be a function that turns cells into a call list
    //TODO should flat map / stream this lot?
    public ImmutableList<LeadBasedDecomposedCall> createCallSequence(Parse parse, ImmutableMap<String, Variance> varianceLookupByName, String logPreamble) {
        log.debug("{} > create call sequence", logPreamble);
        State state = new State(parse, varianceLookupByName, logPreamble);
        state.multiplierFIFO.addFirst(new CallSequenceMultiplier(1));

        for (BackingTableLocationAndValue<ParsedCell> cell : parse.mainBodyCells()) {
            generateCallInstancesForCell(cell.getValue(), cell.getCol(), state);
        }

        checkState(state.multiplierFIFO.size() == 1);
        log.debug("{} < create call sequence {}", logPreamble, state.multiplierFIFO.getFirst());
        return ImmutableList.copyOf(state.multiplierFIFO.removeFirst());
    }

    private void generateCallInstancesForCell(ParsedCell cell, int columnIndex, final State state) {
        final ImmutableList<Group> groups = cell.allGroups();
        for (Group group : groups) {
            ParseType parseType = group.getFirstSectionParseType();
            switch (parseType) {
                case PLAIN_LEAD:
                case PLAIN_LEAD_MULTIPLIER:
                    decomposeMultiplierSection(cell, group, columnIndex, PLAIN_LEAD, PLAIN_LEAD_MULTIPLIER, state);
                    break;
                case CALL:
                case CALL_MULTIPLIER:
                    decomposeMultiplierSection(cell, group, columnIndex, CALL, CALL_MULTIPLIER, state);
                break;
                case MULTIPLIER_GROUP_OPEN:
                case MULTIPLIER_GROUP_OPEN_MULTIPLIER:
                    openMultiplierGroup(cell, group, state);
                    break;
                case MULTIPLIER_GROUP_CLOSE:
                    closeMultiplierGroup(state);
                    break;
                case VARIANCE_OPEN:
                    openVariance(cell, group, state);
                    break;
                case VARIANCE_CLOSE:
                    closeVariance(group, state);
                    break;
                case DEFINITION:
                    insertExpandedDefinition(cell, group, columnIndex, state);
                    break;
                default:
                    throw new RuntimeException("Unhandled ParseType [" + parseType + "]");
            }
        }
    }

    private void decomposeMultiplierSection(final ParsedCell cell, final Group group, final int columnIndex,
                                            final ParseType parseType, final ParseType multiplierParseType, final State state) {
        MultiplierAndParseContents multiplierAndParseContents = getMultiplierAndCall(cell, group, parseType, multiplierParseType);

        log.debug("{}  - Adding call [{}] with multiplier [{}] to group level [{}]",
                state.logPreamble, multiplierAndParseContents.getParseContents(), multiplierAndParseContents.getMultiplier(), state.multiplierFIFO.size());
        if (multiplierAndParseContents.getParseContents().length() > 0 ) {
            for (int i = 0; i< multiplierAndParseContents.getMultiplier(); i++) {
                LeadBasedDecomposedCall decomposedCall = buildDecomposedCall(multiplierAndParseContents.getParseContents(), columnIndex, parseType, state);
                state.multiplierFIFO.peekFirst().add(decomposedCall);
            }
        }
    }

    private void openMultiplierGroup(final ParsedCell cell, final Group group, final State state) {
        MultiplierAndParseContents multiplierAndParseContents = getMultiplierAndCall(cell, group, ParseType.MULTIPLIER_GROUP_OPEN, ParseType.MULTIPLIER_GROUP_OPEN_MULTIPLIER);
        log.debug("Open Group level [{}] with multiplier [{}]", (state.multiplierFIFO.size() + 1), multiplierAndParseContents.getMultiplier());
        state.multiplierFIFO.addFirst(new CallSequenceMultiplier(multiplierAndParseContents.getMultiplier()));
    }

    private void closeMultiplierGroup(final State state) {
        CallSequenceMultiplier callSequenceMultiplier = state.multiplierFIFO.removeFirst();
        log.debug("Close Group level [{}] with multiplier [{}]", (state.multiplierFIFO.size() + 1), callSequenceMultiplier.getMultiplier());
        for (int i = 0; i< callSequenceMultiplier.getMultiplier(); i++) {
            state.multiplierFIFO.peekFirst().addAll(callSequenceMultiplier);
        }
    }

    private void openVariance(ParsedCell cell, Group group, final State state) {
        log.debug("Open variance [{}]", group);
        checkArgument(group.getSections().size() == 2, "Open Variance should have a group with a length of 2 %s", group);
        Section varianceDetailSection = group.getSections().get(1);
        checkArgument(varianceDetailSection.getParseType() == VARIANCE_DETAIL);
        String varianceCharacters = cell.getCharacters(varianceDetailSection);
        state.currentVariance = state.varianceLookupByName.get(varianceCharacters.toLowerCase());
    }

    private void closeVariance(Group group, final State state) {
        log.debug("Close variance [{}]", group);
        checkArgument(group.getSections().size() == 1, "Open Variance should have a group with a length of 1 %s", group);
        state.currentVariance = VarianceFactory.nullVariance();
    }

    private void insertExpandedDefinition(final ParsedCell cell, final Group group, final int columnIndex, final State state) {
        log.debug("Start expand definition [{}]", group);
        String definitionIdentifier = cell.getCharacters(group);

        Optional<ImmutableArrayTable<ParsedCell>> definitionCells = state.parse.findDefinitionByShorthand(definitionIdentifier);
        checkState(definitionCells.isPresent(), "No definitionCells found for %s. Check that the parsing is correctly marking as valid definition ", columnIndex);

        generateCallInstancesForCell(definitionCells.get().get(0, DEFINITION_COLUMN), columnIndex, state);

        log.debug("Finish expand definition [{}]",group);
    }

    private MultiplierAndParseContents getMultiplierAndCall(ParsedCell cell, Group group, ParseType parseType, ParseType multiplierParseType) {
        checkState(group.getSections().size() > 0 && group.getSections().size() <=2);

        int multiplierValue = 1;
        String parseContents = "";

        for (Section section : group.getSections()) {
            if (section.getParseType() == multiplierParseType){
                String multiplierString = cell.getCharacters(section);
                multiplierValue = Integer.parseInt(multiplierString);
            }
            if (section.getParseType() == parseType) {
                parseContents = cell.getCharacters(section);
            }
        }

        return new MultiplierAndParseContents(multiplierValue, parseContents);
    }


    protected LeadBasedDecomposedCall buildDecomposedCall(String callName, int columnIndex, ParseType parseType, State state) {
        return new LeadBasedDecomposedCall(callName, state.currentVariance, parseType);
    }

    private class CallSequenceMultiplier extends ArrayList<LeadBasedDecomposedCall> {

        private final int multiplier;

        CallSequenceMultiplier(int multiplier) {
            this.multiplier = multiplier;
        }

        int getMultiplier() {
            return multiplier;
        }
    }

    @Immutable
    private class MultiplierAndParseContents {
        private final int multiplier;
        private final String parseContents;

        public MultiplierAndParseContents(int multiplier, String parseContents) {
            this.multiplier = multiplier;
            checkArgument(multiplier > 0, "Multiplier must be positive");
            this.parseContents = parseContents;
        }

        int getMultiplier() {
            return multiplier;
        }

        public String getParseContents() {
            return parseContents;
        }
    }
}
