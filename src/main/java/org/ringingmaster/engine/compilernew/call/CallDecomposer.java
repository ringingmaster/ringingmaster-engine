package org.ringingmaster.engine.compilernew.call;

import com.google.common.collect.ImmutableList;
import org.ringingmaster.engine.arraytable.BackingTableLocationAndValue;
import org.ringingmaster.engine.compiler.impl.CourseBasedDecomposedCall;
import org.ringingmaster.engine.compiler.impl.DecomposedCall;
import org.ringingmaster.engine.parser.assignparsetype.ParseType;
import org.ringingmaster.engine.parser.cell.Group;
import org.ringingmaster.engine.parser.cell.ParsedCell;
import org.ringingmaster.engine.parser.parse.Parse;
import org.ringingmaster.engine.touch.variance.Variance;
import org.ringingmaster.engine.touch.variance.impl.NullVariance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.concurrent.Immutable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
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
    ImmutableList<CourseBasedDecomposedCall> createCallSequence(Parse parse, String logPreamble) {
        log.debug("{} > create call sequence", logPreamble);
        final Deque<CallGroup> groupFIFO = new ArrayDeque<>();
        Variance currentVariance = NullVariance.INSTANCE;

        groupFIFO.addFirst(new CallGroup(1));

        for (BackingTableLocationAndValue<ParsedCell> cell : parse.mainBodyCells()) {
            generateCallInstancesForCell(cell.getValue(), cell.getCol());
        }

        checkState(groupFIFO.size() == 1);
        log.debug("{} < create call sequence {}", logPreamble, groupFIFO.getFirst());
        return ImmutableList.copyOf(groupFIFO.removeFirst());
    }

    private void generateCallInstancesForCell(ParsedCell cell, int columnIndex) { //TODO should we flat map / stream this lot?
        final ImmutableList<Group> groups = cell.allGroups();
        for (Group group : groups) {
            switch (group.getFirstSectionParseType()) {
                case PLAIN_LEAD:
                case PLAIN_LEAD_MULTIPLIER:
                    decomposeSection(group, columnIndex, PLAIN_LEAD, PLAIN_LEAD_MULTIPLIER);
                    break;
                case CALL:
                case CALL_MULTIPLIER:
                    decomposeSection(group, columnIndex, CALL, CALL_MULTIPLIER);
                break;
                case GROUP_OPEN:
                case GROUP_OPEN_MULTIPLIER:
                    openGroup(group);
                    break;
                case GROUP_CLOSE:
                    closeGroup();
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

    private void decomposeSection(Group group, int columnIndex,
                               ParseType parseType, ParseType multiplierParseType) {
        MultiplierAndCall multiplierAndCall = getMultiplierAndCall(word, parseType, multiplierParseType);

        log.debug("{}  - Adding call [{}] with multiplier [{}] to group level [{}]",
                logPreamble, multiplierAndCall.getCallName(), multiplierAndCall.getMultiplier(), callFIFO.size());
        if (multiplierAndCall.getCallName().length() > 0 ) {
            for (int i=0;i<multiplierAndCall.getMultiplier();i++) {
                DC decomposedCall = buildDecomposedCall(multiplierAndCall.getCallName(), multiplierAndCall.getVariance(), columnIndex, parseType);
                callFIFO.peekFirst().add(decomposedCall);
            }
        }
    }
//
//    protected abstract DC buildDecomposedCall(String callName, Variance variance, int columnIndex, ParseType parseType);
//
//    MultiplierAndCall getMultiplierAndCall(TouchWord word, ParseType parseType, ParseType multiplierParseType) {
//        List<TouchElement> elementsInWord = word.getElements();
//        StringBuilder parseTypeBuff = new StringBuilder(elementsInWord.size());
//        StringBuilder multiplierBuff = new StringBuilder(elementsInWord.size());
//        boolean finishedMultiplier = false;
//        for (TouchElement element : elementsInWord) {
//            if (!finishedMultiplier && element.getParseType().equals(multiplierParseType)) {
//                multiplierBuff.append(element.getCharacter());
//            }
//            else if (element.getParseType().equals(parseType)) {
//                parseTypeBuff.append(element.getCharacter());
//                finishedMultiplier = true;
//            }
//        }
//        int multiplier = 1;
//        if (multiplierBuff.length() > 0) {
//            String multiplierString = multiplierBuff.toString();
//            multiplier = Integer.parseInt(multiplierString);
//        }
//
//        return new MultiplierAndCall(multiplier, parseTypeBuff.toString(), currentVariance);
//    }
//
//    private void openGroup(TouchWord word) {
//        MultiplierAndCall multiplierAndCall = getMultiplierAndCall(word, ParseType.GROUP_OPEN, ParseType.GROUP_OPEN_MULTIPLIER);
//        log.debug("Open Group level [{}] with multiplier [{}]", (callFIFO.size() + 1), multiplierAndCall.getMultiplier());
//        callFIFO.addFirst(new CallGroup(multiplierAndCall.getMultiplier()));
//    }
//
//    private void closeGroup() {
//        CallGroup callGroup = callFIFO.removeFirst();
//        log.debug("Close Group level [{}] with multiplier [{}]", (callFIFO.size() + 1), callGroup.getMultiplier());
//        for (int i=0;i<callGroup.getMultiplier();i++) {
//            callFIFO.peekFirst().addAll(callGroup);
//        }
//    }
//
//    private void openVariance(TouchWord word) {
//        log.debug("Open variance [{}]", word);
//        checkArgument(word.getElements().size() == 1, "Open Variance should have a word with a length of 1");
//        currentVariance = word.getElements().get(0).getVariance();
//    }
//
//    private void closeVariance(TouchWord word) {
//        log.debug("Close variance []", word);
//        checkArgument(word.getElements().size() == 1, "Close Variance should have a word with a length of 1");
//        currentVariance = NullVariance.INSTANCE;
//    }
//
//    private void insertDefinition(TouchWord word, int columnIndex) {
//        log.debug("Start definition [{}]", word);
//        String elementsAsString = word.getElementsAsString();
//        Optional<DefinitionCell> definitionByShorthand = touch.findDefinitionByShorthand(elementsAsString);
//        if (definitionByShorthand.isPresent()) {
//            generateCallInstancesForCell(definitionByShorthand.get(), columnIndex);
//        }
//        log.debug("Finish definition [{}]",word);
//    }

    protected CourseBasedDecomposedCall buildDecomposedCall(String callName, Variance variance, int columnIndex, ParseType parseType) {
        checkPositionIndex(columnIndex, callPositionNames.length, "column index out of bounds");
        String callPositionName = callPositionNames[columnIndex];
        checkNotNull(callPositionName, "callPositionName is null. Check that the parsing is correctly excluding columns with no valid call position");
        return new CourseBasedDecomposedCall(callName, variance, callPositionName);
    }

    private class CallGroup extends ArrayList<CourseBasedDecomposedCall> {
        private final int multiplier;

        CallGroup(int multiplier) {
            this.multiplier = multiplier;
        }

        int getMultiplier() {
            return multiplier;
        }
    }

    @Immutable
    private class MultiplierAndCall extends DecomposedCall {
        private final int multiplier;

        public MultiplierAndCall(int multiplier, String callName, Variance variance) {
            super(callName, variance);
            this.multiplier = multiplier;
            checkArgument(multiplier > 0, "Multiplier must be positive");
        }

        int getMultiplier() {
            return multiplier;
        }
    }
}
