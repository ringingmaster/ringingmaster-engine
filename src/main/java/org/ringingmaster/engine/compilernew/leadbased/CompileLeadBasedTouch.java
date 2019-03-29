package org.ringingmaster.engine.compilernew.leadbased;

import com.google.common.collect.ImmutableList;
import org.ringingmaster.engine.compiler.impl.LeadBasedDecomposedCall;
import org.ringingmaster.engine.compiler.impl.MaskedNotation;
import org.ringingmaster.engine.compiler.impl.TerminateEarlyException;
import org.ringingmaster.engine.compilernew.internaldata.LeadBasedCompilerPipelineData;
import org.ringingmaster.engine.compilernew.proof.ProofTerminationReason;
import org.ringingmaster.engine.method.Method;
import org.ringingmaster.engine.method.MethodLead;
import org.ringingmaster.engine.method.MethodRow;
import org.ringingmaster.engine.method.Stroke;
import org.ringingmaster.engine.method.impl.MethodBuilder;
import org.ringingmaster.engine.notation.NotationCall;
import org.ringingmaster.engine.notation.NotationRow;
import org.ringingmaster.engine.touch.Touch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.google.common.base.Preconditions.checkState;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.PLAIN_LEAD;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
public class CompileLeadBasedTouch implements Function<LeadBasedCompilerPipelineData, LeadBasedCompilerPipelineData> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public static final int EMPTY_PART_TOLERANCE = 2; //TODO this should be paramaterised


    @Override
    public LeadBasedCompilerPipelineData apply(LeadBasedCompilerPipelineData leadBasedCompilerPipelineData) {

        Optional<Method> method = compileTouch(() -> false,
                leadBasedCompilerPipelineData.getParse().getUnderlyingTouch(),
                leadBasedCompilerPipelineData.getCallSequence(),
                leadBasedCompilerPipelineData.getLogPreamble());

        return leadBasedCompilerPipelineData
                .terminate(terminationReason.get(), terminateNotes.get())
                .setCreatedMethod(method);
    }

    // inputs
    private int callSequenceIndex; //TODO make stateless
    private int partIndex;//TODO make stateless

    // internal data.
    private LeadBasedDecomposedCall nextCall;//TODO make stateless
    private Map<String, NotationCall> callLookupByName; //TODO make stateless

    //outputs

    private Optional<ProofTerminationReason> terminationReason = Optional.empty();//TODO make stateless
    private Optional<String> terminateNotes = Optional.empty();//TODO make stateless


    private Optional<Method> compileTouch(Supplier<Boolean> allowTerminateEarly, Touch touch, ImmutableList<LeadBasedDecomposedCall> callSequence, String logPreamble) {
        log.debug("{} > create touch", logPreamble);
        log.debug("{}  - part [{}]", logPreamble, partIndex);
        int partIndex = 0;
        // This is required here to handle the case when the first parts are omitted, and a check for empty parts are required.
        callSequenceIndex = -1;
        if (!isPlainCourse(callSequence)) {
            advanceToNextCall(callSequence, logPreamble);
        }

        MethodRow startChange = createStartChange(touch);

        MaskedNotation maskedNotation = new MaskedNotation(touch.getNonSplicedActiveNotation().get());

        final List<MethodLead> leads = new ArrayList<>();

        if (maskedNotation.getRowCount() == 0) {
            terminate(ProofTerminationReason.INVALID_TOUCH, "Notation [" + maskedNotation.getNameIncludingNumberOfBells() + "] has no rows.", logPreamble);
        }
        while (!isTerminated()) {
            log.debug("{}   - lead [{}]", logPreamble, leads.size());
            final MethodLead lead = compileLead(callSequence, maskedNotation, startChange, touch, logPreamble);
            leads.add(lead);
            startChange = lead.getLastRow();
            checkTerminationMaxLeads(leads, touch, logPreamble);
            checkTerminateEarly(allowTerminateEarly);
        }
        Optional<Method> method = Optional.of(MethodBuilder.buildMethod(touch.getNumberOfBells(), leads));
        log.debug("{} < create touch", logPreamble);

        return method;
    }

    private boolean isPlainCourse(ImmutableList<LeadBasedDecomposedCall> callSequence) {
        // A plain course will have no callSequence length
        return callSequence.size() == 0;
    }

    private MethodRow createStartChange(Touch touch) {
        MethodRow startChange = touch.getStartChange();
        checkState(startChange.getNumberOfBells() == touch.getNumberOfBells());
        Stroke startStroke = touch.getStartStroke();

        startChange = startChange.setStroke(startStroke);

        return startChange;
    }

    private MethodLead compileLead(ImmutableList<LeadBasedDecomposedCall> callSequence, final MaskedNotation maskedNotation, MethodRow currentMethodRow, Touch touch, String logPreamble) {

        final List<MethodRow> rows = new ArrayList<>();
        final List<Integer> leadSeparatorPositions = new ArrayList<>();

        rows.add(currentMethodRow);

        for (NotationRow notationRow : maskedNotation) {

            currentMethodRow = buildNextRow(notationRow, currentMethodRow);
            rows.add(currentMethodRow);

            checkTerminationChange(touch, currentMethodRow, logPreamble);
            checkTerminationMaxRows(touch, currentMethodRow, logPreamble);
            if (isTerminated()) {
                break;
            }
            tryToMakeACall(callSequence, maskedNotation, currentMethodRow, logPreamble);
        }

//TODO		addLeadSeparator(currentNotation, rows, leadSeparatorPositions);

        final MethodLead lead = MethodBuilder.buildLead(touch.getNumberOfBells(), rows, leadSeparatorPositions);
        return lead;
    }

    private MethodRow buildNextRow(final NotationRow notationRow, final MethodRow previousRow) {
        MethodRow nextRow;
        if (notationRow.isAllChange()) {
            nextRow = MethodBuilder.buildAllChangeRow(previousRow);
        }
        else {
            nextRow = MethodBuilder.buildRowWithPlaces(previousRow, notationRow);
        }
        return nextRow;
    }

    private void tryToMakeACall(ImmutableList<LeadBasedDecomposedCall> callSequence, MaskedNotation maskedNotation, MethodRow currentMethodRow, String logPreamble) {
        if (nextCall != null && maskedNotation.isAtCallPoint()) {
            NotationCall call = callLookupByName.get(nextCall.getCallName());
            boolean callConsumed = applyNextCall(maskedNotation, currentMethodRow, nextCall, call, logPreamble);
            if (callConsumed) {
                advanceToNextCall(callSequence,logPreamble);
            }
        }
    }

    private void advanceToNextCall(ImmutableList<LeadBasedDecomposedCall> callSequence, String logPreamble) {
        int enteringPartIndex = partIndex;
        do {
            callSequenceIndex++;
            if (callSequenceIndex >= callSequence.size()) {
                //New Part
                partIndex++;
                if (partIndex >= enteringPartIndex + EMPTY_PART_TOLERANCE) {
                    terminate(ProofTerminationReason.EMPTY_PARTS, Integer.toString(EMPTY_PART_TOLERANCE), logPreamble);
                    break;
                }
                log.debug("{}  - part [{}]", logPreamble, partIndex);
                callSequenceIndex = 0;
            }
            nextCall = callSequence.get(callSequenceIndex);

        } while (!nextCall.getVariance().includePart(partIndex));
    }

    protected boolean applyNextCall(MaskedNotation maskedNotation, MethodRow currentMethodRow,
                                    LeadBasedDecomposedCall nextCallMeta, NotationCall call, String logPreamble) {
        if (nextCallMeta.getParseType().equals(PLAIN_LEAD)) {
            // No Call, but consume the call.
            log.debug("{} Apply Plain lead", logPreamble);
        }
        else {
            maskedNotation.applyCall(call, logPreamble);
        }
        // We consumed the call
        return true;
    }

    private void checkTerminationMaxLeads(List<MethodLead> leads, Touch touch, String logPreamble) {
        if (touch.getTerminationMaxLeads().isPresent() &&
                leads.size() >= touch.getTerminationMaxLeads().get()) {
            terminate(ProofTerminationReason.LEAD_COUNT, touch.getTerminationMaxLeads().get().toString(), logPreamble);
        }
    }

    private void checkTerminateEarly(Supplier<Boolean> allowTerminateEarly) {
        if (allowTerminateEarly.get()) {
            throw new TerminateEarlyException("Terminate Early request");
        }
    }

    private void checkTerminationMaxRows(Touch touch, MethodRow newRow, String logPreamble) {
        if (newRow.getRowNumber() >= touch.getTerminationMaxRows()) {
            terminate(ProofTerminationReason.ROW_COUNT, Integer.toString(touch.getTerminationMaxRows()), logPreamble);
        }
    }

    private void checkTerminationChange(Touch touch, MethodRow newRow, String logPreamble) {
        if (touch.getTerminationChange().isPresent() &&
                touch.getTerminationChange().get().equals(newRow)) {
            terminate(ProofTerminationReason.SPECIFIED_ROW, touch.getTerminationChange().get().toString(), logPreamble);
        }
    }

    private void terminate(final ProofTerminationReason terminationReason, String terminateNotes, String logPreamble) {
        if (!isTerminated()) {
            log.debug("{}  - Terminate [{}] {}", logPreamble, terminateNotes, terminationReason);
            this.terminationReason = Optional.of(terminationReason);
            this.terminateNotes = Optional.of(terminateNotes);
        }
        else  {
            log.warn("Requesting second terminate [{}]{}", terminationReason, terminateNotes);
        }
    }

    private boolean isTerminated() {
        return terminationReason.isPresent();
    }
}
