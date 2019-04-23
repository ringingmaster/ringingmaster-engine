package org.ringingmaster.engine.compiler.denormaliser;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.ringingmaster.engine.arraytable.BackingTableLocationAndValue;
import org.ringingmaster.engine.arraytable.ImmutableArrayTable;
import org.ringingmaster.engine.compiler.variance.Variance;
import org.ringingmaster.engine.compiler.variance.VarianceFactory;
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
import static org.ringingmaster.engine.composition.tableaccess.DefinitionTableAccess.DEFINITION_COLUMN;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.CALL;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.CALL_MULTIPLIER;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.PLAIN_LEAD;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.PLAIN_LEAD_MULTIPLIER;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.VARIANCE_DETAIL;

/**
 * Takes nesting and grouping in cells, and denormalises it down to a list of calls that cary enough information to
 * support the runtime rules around call identification (from call names), Variance, Etc.
 *
 * @author stevelake
 */
@Immutable
public abstract class CallDenormaliser<T extends DenormalisedCall, PASSTHROUGH> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    protected class State {
        //inputs
        private final Parse parse;
        private final ImmutableMap<String, Variance> varianceLookupByName;
        private String logPreamble;

        //internal data
        private Variance currentVariance = VarianceFactory.nullVariance();
        private ParsedCell cell;
        private Group group;
        private int columnIndex;

        //outputs
        private final Deque<CallSequence> callSequenceNested = new ArrayDeque<>();

        private State(Parse parse, ImmutableMap<String, Variance> varianceLookupByName, String logPreamble) {
            this.parse = parse;
            this.varianceLookupByName = varianceLookupByName;
            this.logPreamble = logPreamble;

            callSequenceNested.addFirst(new CallSequence(1));
        }

        public Variance getCurrentVariance() {
            return currentVariance;
        }

        public int getColumnIndex() {
            return columnIndex;
        }
    }



    // TODO Can this can be a function that turns cells into a call list
    //TODO should flat map / stream this lot?
    public ImmutableList<T> createCallSequence(Parse parse, ImmutableMap<String, Variance> varianceLookupByName, String logPreamble, PASSTHROUGH passthrough) {
        log.debug("{} > decomposing call sequence", logPreamble);
        log.debug("{} Open level [0]", logPreamble);
        final State state = new State(parse, varianceLookupByName, logPreamble + "  ");

        for (BackingTableLocationAndValue<ParsedCell> cell : parse.mainBodyCells()) {
            state.cell = cell.getValue();
            state.columnIndex = cell.getCol();
            generateCallInstancesForCell(state, passthrough);
        }

        checkState(state.callSequenceNested.size() == 1);
        log.debug("{} Close level [0]", logPreamble);
        log.debug("{} < decomposing call sequence. result: {}", logPreamble, state.callSequenceNested.getFirst());
        return ImmutableList.copyOf(state.callSequenceNested.removeFirst());
    }

    private void generateCallInstancesForCell(final State state, PASSTHROUGH passthrough) {
        final ImmutableList<Group> groups = state.cell.allGroups();
        for (Group group : groups) {
            state.group = group;
            ParseType parseType = state.group.getFirstSectionParseType();
            switch (parseType) {
                case PLAIN_LEAD:
                case PLAIN_LEAD_MULTIPLIER:
                    decomposeMultiplierSection(state, PLAIN_LEAD, PLAIN_LEAD_MULTIPLIER, passthrough);
                    break;
                case CALL:
                case CALL_MULTIPLIER:
                    decomposeMultiplierSection(state, CALL, CALL_MULTIPLIER, passthrough);
                break;
                case MULTIPLIER_GROUP_OPEN:
                case MULTIPLIER_GROUP_OPEN_MULTIPLIER:
                    openMultiplierGroup(state);
                    break;
                case MULTIPLIER_GROUP_CLOSE:
                    closeMultiplierGroup(state);
                    break;
                case VARIANCE_OPEN:
                    openVariance(state);
                    break;
                case VARIANCE_CLOSE:
                    closeVariance(state);
                    break;
                case DEFINITION:
                    insertExpandedDefinition(state, passthrough);
                    break;
                default:
                    throw new RuntimeException("Unhandled ParseType [" + parseType + "]");
            }
        }
    }

    private void decomposeMultiplierSection(final State state, final ParseType parseType, final ParseType multiplierParseType, PASSTHROUGH passthrough) {
        MultiplierAndParseContents multiplierAndParseContents = getMultiplierAndCall(state, parseType, multiplierParseType);

        log.debug("{} Adding call [{}] with multiplier [{}], variance [{}]",
                state.logPreamble, multiplierAndParseContents.getParseContents(), multiplierAndParseContents.getMultiplier(), state.currentVariance);

        if (multiplierAndParseContents.getParseContents().length() > 0 ) {
            T decomposedCall = buildDecomposedCall(multiplierAndParseContents.getParseContents(), parseType, state, passthrough);
            for (int i = 0; i< multiplierAndParseContents.getMultiplier(); i++) {
                log.debug("{}   Add [{}]", state.logPreamble, decomposedCall);
                state.callSequenceNested.peekFirst().add(decomposedCall);
            }
        }
    }

    private void openMultiplierGroup(final State state) {
        MultiplierAndParseContents multiplierAndParseContents = getMultiplierAndCall(state, ParseType.MULTIPLIER_GROUP_OPEN, ParseType.MULTIPLIER_GROUP_OPEN_MULTIPLIER);
        log.debug("{} Open level [{}], multiplier [{}]", state.logPreamble, (state.callSequenceNested.size() ), multiplierAndParseContents.getMultiplier());
        state.logPreamble += "  ";
        state.callSequenceNested.addFirst(new CallSequence(multiplierAndParseContents.getMultiplier()));
    }

    private void closeMultiplierGroup(final State state) {
        CallSequence callSequence = state.callSequenceNested.removeFirst();
        state.logPreamble = state.logPreamble.substring(0,state.logPreamble.length()-2); //remove log indentation
        log.debug("{} Close level [{}], multiplier [{}]", state.logPreamble, (state.callSequenceNested.size()), callSequence.getMultiplier());
        // unwind the multiplier stack
        for (int i = 0; i< callSequence.getMultiplier(); i++) {
            log.debug("{} Add level [{}] to [{}] call sequence: [{}]", state.logPreamble, state.callSequenceNested.size(), state.callSequenceNested.size()-1, callSequence);
            state.callSequenceNested.peekFirst().addAll(callSequence);
        }
    }

    private void openVariance(final State state) {
        checkArgument(state.group.getSections().size() == 2, "Open Variance should have a group with a length of 2 %s", state.group);
        Section varianceDetailSection = state.group.getSections().get(1);
        checkArgument(varianceDetailSection.getParseType() == VARIANCE_DETAIL);
        String varianceCharacters = state.cell.getCharacters(varianceDetailSection);
        state.currentVariance = state.varianceLookupByName.get(varianceCharacters.toLowerCase());
        log.debug("{} [ Open variance [{}]", state.logPreamble, state.currentVariance);
    }

    private void closeVariance(final State state) {
        log.debug("{} ] Close variance [{}]", state.logPreamble, state.currentVariance);
        checkArgument(state.group.getSections().size() == 1, "Open Variance should have a group with a length of 1 %s", state.group);
        state.currentVariance = VarianceFactory.nullVariance();
    }

    private void insertExpandedDefinition(final State state, PASSTHROUGH passthrough) {
        log.debug("{} Start expand definition [{}]", state.logPreamble, state.group);
        String definitionIdentifier = state.cell.getCharacters(state.group);

        Optional<ImmutableArrayTable<ParsedCell>> definitionCells = state.parse.findDefinitionByShorthand(definitionIdentifier);
        checkState(definitionCells.isPresent(), "No definitionCells found for %s. Check that the parsing is correctly marking as valid definition ", state.columnIndex);

        // This allows a recursion without having a queue of ParsedCell's
        ParsedCell originalCell = state.cell;
        state.cell = definitionCells.get().get(0, DEFINITION_COLUMN);
        generateCallInstancesForCell(state, passthrough);
        state.cell = originalCell;

        log.debug("{} Finish expand definition [{}]", state.logPreamble,state.group);
    }

    private MultiplierAndParseContents getMultiplierAndCall(final State state, ParseType parseType, ParseType multiplierParseType) {
        checkState(state.group.getSections().size() > 0 && state.group.getSections().size() <=2);

        int multiplierValue = 1;
        String parseContents = "";

        for (Section section : state.group.getSections()) {
            if (section.getParseType() == multiplierParseType){
                String multiplierString = state.cell.getCharacters(section);
                multiplierValue = Integer.parseInt(multiplierString);
            }
            if (section.getParseType() == parseType) {
                parseContents = state.cell.getCharacters(section);
            }
        }

        return new MultiplierAndParseContents(multiplierValue, parseContents);
    }


    protected abstract T buildDecomposedCall(String callName, ParseType parseType, State state, PASSTHROUGH passthrough);

    private class CallSequence extends ArrayList<T> {

        private final int multiplier;

        CallSequence(int multiplier) {
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

        MultiplierAndParseContents(int multiplier, String parseContents) {
            this.multiplier = multiplier;
            checkArgument(multiplier > 0, "Multiplier must be positive");
            this.parseContents = parseContents;
        }

        int getMultiplier() {
            return multiplier;
        }

        String getParseContents() {
            return parseContents;
        }
    }
}
