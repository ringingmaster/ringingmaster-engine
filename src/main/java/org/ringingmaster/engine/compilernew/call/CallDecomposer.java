package org.ringingmaster.engine.compilernew.call;

import com.google.common.collect.ImmutableList;
import org.ringingmaster.engine.arraytable.BackingTableLocationAndValue;
import org.ringingmaster.engine.compiler.impl.CourseBasedDecomposedCall;
import org.ringingmaster.engine.compiler.impl.DecomposedCall;
import org.ringingmaster.engine.parser.assignparsetype.ParseType;
import org.ringingmaster.engine.parser.cell.Group;
import org.ringingmaster.engine.parser.cell.ParsedCell;
import org.ringingmaster.engine.parser.cell.Section;
import org.ringingmaster.engine.parser.parse.Parse;
import org.ringingmaster.engine.touch.variance.Variance;
import org.ringingmaster.engine.touch.variance.impl.NullVariance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.concurrent.Immutable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkPositionIndex;
import static com.google.common.base.Preconditions.checkState;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.CALL;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.CALL_MULTIPLIER;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.PLAIN_LEAD;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.PLAIN_LEAD_MULTIPLIER;

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
    public ImmutableList<CourseBasedDecomposedCall> createCallSequence(Parse parse, String logPreamble) {
        log.debug("{} > create call sequence", logPreamble);
        final Deque<CallSequenceMultiplier> multiplierFIFO = new ArrayDeque<>();
        Variance currentVariance = NullVariance.INSTANCE;

        multiplierFIFO.addFirst(new CallSequenceMultiplier(1));

        for (BackingTableLocationAndValue<ParsedCell> cell : parse.mainBodyCells()) {
            generateCallInstancesForCell(cell.getValue(), cell.getCol(), multiplierFIFO, logPreamble);
        }

        checkState(multiplierFIFO.size() == 1);
        log.debug("{} < create call sequence {}", logPreamble, multiplierFIFO.getFirst());
        return ImmutableList.copyOf(multiplierFIFO.removeFirst());
    }

    private void generateCallInstancesForCell(ParsedCell cell, int columnIndex, final Deque<CallSequenceMultiplier> multiplierFIFO, String logPreamble) {
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
                    openMultiplierGroup(group);
                    break;
                case MULTIPLIER_GROUP_CLOSE:
                    closeMultiplierGroup();
                    break;
                case VARIANCE_OPEN:
                    openVariance(group);
                    break;
                case VARIANCE_CLOSE:
                    closeVariance(group);
                    break;
                case DEFINITION:
                    insertDefinition(group, columnIndex);
                    break;
            }
        }
    }

    private void decomposeMultiplierSection(ParsedCell cell, Group group, int columnIndex,
                                            ParseType parseType, ParseType multiplierParseType,
                                            final Deque<CallSequenceMultiplier> multiplierFIFO, String logPreamble) {
        MultiplierAndParseContents multiplierAndParseContents = getMultiplierAndCall(cell, group, parseType, multiplierParseType);

        log.debug("{}  - Adding call [{}] with multiplier [{}] to group level [{}]",
                logPreamble, multiplierAndParseContents.getParseContents(), multiplierAndParseContents.getMultiplier(), multiplierFIFO.size());
        if (multiplierAndParseContents.getParseContents().length() > 0 ) {
            for (int i = 0; i< multiplierAndParseContents.getMultiplier(); i++) {
                CourseBasedDecomposedCall decomposedCall = buildDecomposedCall(multiplierAndParseContents.getParseContents(), columnIndex, parseType);
                multiplierFIFO.peekFirst().add(decomposedCall);
            }
        }
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

    private void openMultiplierGroup(Group group) {
        MultiplierAndParseContents multiplierAndParseContents = getMultiplierAndCall(word, ParseType.MULTIPLIER_GROUP_OPEN, ParseType.MULTIPLIER_GROUP_OPEN_MULTIPLIER);
        log.debug("Open Group level [{}] with multiplier [{}]", (multiplierFIFO.size() + 1), multiplierAndParseContents.getMultiplier());
        multiplierFIFO.addFirst(new CallSequenceMultiplier(multiplierAndParseContents.getMultiplier()));
    }

    private void closeMultiplierGroup() {
        CallSequenceMultiplier callSequenceMultiplier = multiplierFIFO.removeFirst();
        log.debug("Close Group level [{}] with multiplier [{}]", (multiplierFIFO.size() + 1), callSequenceMultiplier.getMultiplier());
        for (int i = 0; i< callSequenceMultiplier.getMultiplier(); i++) {
            multiplierFIFO.peekFirst().addAll(callSequenceMultiplier);
        }
    }

    private void openVariance(Group group) {
        log.debug("Open variance [{}]", word);
        checkArgument(word.getElements().size() == 1, "Open Variance should have a word with a length of 1");
        currentVariance = word.getElements().get(0).getVariance();
    }

    private void closeVariance(Group group) {
        log.debug("Close variance []", word);
        checkArgument(word.getElements().size() == 1, "Close Variance should have a word with a length of 1");
        currentVariance = NullVariance.INSTANCE;
    }

    private void insertDefinition(Group group, int columnIndex) {
        log.debug("Start definition [{}]", word);
        String elementsAsString = word.getElementsAsString();
        Optional<DefinitionCell> definitionByShorthand = touch.findDefinitionByShorthand(elementsAsString);
        if (definitionByShorthand.isPresent()) {
            generateCallInstancesForCell(definitionByShorthand.get(), columnIndex);
        }
        log.debug("Finish definition [{}]",word);
    }

    protected CourseBasedDecomposedCall buildDecomposedCall(String callName, Variance variance, int columnIndex, ParseType parseType) {
        checkPositionIndex(columnIndex, callPositionNames.length, "column index out of bounds");
        String callPositionName = callPositionNames[columnIndex];
        checkNotNull(callPositionName, "callPositionName is null. Check that the parsing is correctly excluding columns with no valid call position");
        return new CourseBasedDecomposedCall(callName, variance, callPositionName);
    }

    private class CallSequenceMultiplier extends ArrayList<CourseBasedDecomposedCall> {
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
