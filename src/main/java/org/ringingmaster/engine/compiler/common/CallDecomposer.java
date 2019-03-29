package org.ringingmaster.engine.compiler.common;

import com.google.common.collect.ImmutableList;
import org.ringingmaster.engine.arraytable.BackingTableLocationAndValue;
import org.ringingmaster.engine.arraytable.ImmutableArrayTable;
import org.ringingmaster.engine.compilerold.impl.LeadBasedDecomposedCall;
import org.ringingmaster.engine.parser.assignparsetype.ParseType;
import org.ringingmaster.engine.parser.cell.Group;
import org.ringingmaster.engine.parser.cell.ParsedCell;
import org.ringingmaster.engine.parser.cell.Section;
import org.ringingmaster.engine.parser.parse.Parse;
import org.ringingmaster.engine.touch.variance.impl.NullVariance;
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
import static org.ringingmaster.engine.touch.tableaccess.DefinitionTableAccess.DEFINITION_COLUMN;

/**
 * TODO comments???
 *
 * @author stevelake
 */
@Immutable
public class CallDecomposer {

    private final Logger log = LoggerFactory.getLogger(this.getClass());


    // TODO Can this can be a function that turns cells into a call list
    //TODO should flat map / stream this lot?
    public ImmutableList<LeadBasedDecomposedCall> createCallSequence(Parse parse, String logPreamble) {
        log.debug("{} > create call sequence", logPreamble);
        final Deque<CallSequenceMultiplier> multiplierFIFO = new ArrayDeque<>();
        multiplierFIFO.addFirst(new CallSequenceMultiplier(1));

        for (BackingTableLocationAndValue<ParsedCell> cell : parse.mainBodyCells()) {
            generateCallInstancesForCell(cell.getValue(), cell.getCol(), multiplierFIFO, parse, logPreamble);
        }

        checkState(multiplierFIFO.size() == 1);
        log.debug("{} < create call sequence {}", logPreamble, multiplierFIFO.getFirst());
        return ImmutableList.copyOf(multiplierFIFO.removeFirst());
    }

    private void generateCallInstancesForCell(ParsedCell cell, int columnIndex,
                                              final Deque<CallSequenceMultiplier> multiplierFIFO,
                                              Parse parse,
                                              String logPreamble) {
        final ImmutableList<Group> groups = cell.allGroups();
        for (Group group : groups) {
            switch (group.getFirstSectionParseType()) {
                case PLAIN_LEAD:
                case PLAIN_LEAD_MULTIPLIER:
                    decomposeMultiplierSection(cell, group, columnIndex, PLAIN_LEAD, PLAIN_LEAD_MULTIPLIER, multiplierFIFO, logPreamble);
                    break;
                case CALL:
                case CALL_MULTIPLIER:
                    decomposeMultiplierSection(cell, group, columnIndex, CALL, CALL_MULTIPLIER, multiplierFIFO, logPreamble);
                break;
                case MULTIPLIER_GROUP_OPEN:
                case MULTIPLIER_GROUP_OPEN_MULTIPLIER:
                    openMultiplierGroup(cell, group, multiplierFIFO);
                    break;
                case MULTIPLIER_GROUP_CLOSE:
                    closeMultiplierGroup(multiplierFIFO);
                    break;
                case VARIANCE_OPEN:
//                    openVariance(group);
                    // TODO: I want the variance definition defined as a normal parse type rather thane being 'special' from both a data and a UI POV
                    // TODO: i.e. omit odd variances [-O p-p]
                    // TODO: i.e. include even variances [+even p-p]
                    // TODO: i.e. omit specific variances [-1,2,5 p-p]
                    //break;
                    throw new RuntimeException("TODO Variances");
                case VARIANCE_CLOSE:
//                    closeVariance(group);
                    break;
                case DEFINITION:
                    insertExpandedDefinition(cell, group, columnIndex, multiplierFIFO, parse, logPreamble);
                    break;
            }
        }
    }

    private void decomposeMultiplierSection(final ParsedCell cell, final Group group, final int columnIndex,
                                            final ParseType parseType, final ParseType multiplierParseType,
                                            final Deque<CallSequenceMultiplier> multiplierFIFO,
                                            final String logPreamble) {
        MultiplierAndParseContents multiplierAndParseContents = getMultiplierAndCall(cell, group, parseType, multiplierParseType);

        log.debug("{}  - Adding call [{}] with multiplier [{}] to group level [{}]",
                logPreamble, multiplierAndParseContents.getParseContents(), multiplierAndParseContents.getMultiplier(), multiplierFIFO.size());
        if (multiplierAndParseContents.getParseContents().length() > 0 ) {
            for (int i = 0; i< multiplierAndParseContents.getMultiplier(); i++) {
                LeadBasedDecomposedCall decomposedCall = buildDecomposedCall(multiplierAndParseContents.getParseContents(), columnIndex, parseType);
                multiplierFIFO.peekFirst().add(decomposedCall);
            }
        }
    }

    private void openMultiplierGroup(final ParsedCell cell, final Group group, final Deque<CallSequenceMultiplier> multiplierFIFO) {
        MultiplierAndParseContents multiplierAndParseContents = getMultiplierAndCall(cell, group, ParseType.MULTIPLIER_GROUP_OPEN, ParseType.MULTIPLIER_GROUP_OPEN_MULTIPLIER);
        log.debug("Open Group level [{}] with multiplier [{}]", (multiplierFIFO.size() + 1), multiplierAndParseContents.getMultiplier());
        multiplierFIFO.addFirst(new CallSequenceMultiplier(multiplierAndParseContents.getMultiplier()));
    }

    private void closeMultiplierGroup(final Deque<CallSequenceMultiplier> multiplierFIFO) {
        CallSequenceMultiplier callSequenceMultiplier = multiplierFIFO.removeFirst();
        log.debug("Close Group level [{}] with multiplier [{}]", (multiplierFIFO.size() + 1), callSequenceMultiplier.getMultiplier());
        for (int i = 0; i< callSequenceMultiplier.getMultiplier(); i++) {
            multiplierFIFO.peekFirst().addAll(callSequenceMultiplier);
        }
    }

    // TODO
//    private void openVariance(Group group) {
//        log.debug("Open variance [{}]", word);
//        checkArgument(word.getElements().size() == 1, "Open Variance should have a word with a length of 1");
//        currentVariance = word.getElements().get(0).getVariance();
//    }
    // TODO
//    private void closeVariance(Group group) {
//        log.debug("Close variance []", word);
//        checkArgument(word.getElements().size() == 1, "Close Variance should have a word with a length of 1");
//        currentVariance = NullVariance.INSTANCE;
//    }

    private void insertExpandedDefinition(final ParsedCell cell, final Group group, final int columnIndex,
                                          final Deque<CallSequenceMultiplier> multiplierFIFO,
                                          final Parse parse,
                                          final String logPreamble) {
        log.debug("Start expand definition [{}]", group);
        String definitionIdentifier = cell.getCharacters(group);

        Optional<ImmutableArrayTable<ParsedCell>> definitionCells = parse.findDefinitionByShorthand(definitionIdentifier);
        checkState(definitionCells.isPresent(), "No definitionCells found for %s. Check that the parsing is correctly marking as valid definition ", columnIndex);

//        for (BackingTableLocationAndValue<ParsedCell> definitionContentsCell : definitionCells.get()) {
            generateCallInstancesForCell(definitionCells.get().get(0, DEFINITION_COLUMN), columnIndex, multiplierFIFO, parse, logPreamble);
//            log.debug(definitionContentsCell.toString());
//        }

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


    protected LeadBasedDecomposedCall buildDecomposedCall(String callName, int columnIndex, ParseType parseType) {
        return new LeadBasedDecomposedCall(callName, NullVariance.getInstance(), parseType); //TODO remove NullVariance
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
