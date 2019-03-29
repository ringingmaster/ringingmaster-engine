package org.ringingmaster.engine.compiler.leadbased;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.ringingmaster.engine.compilerold.impl.LeadBasedDecomposedCall;
import org.ringingmaster.engine.compilerold.impl.MaskedNotation;
import org.ringingmaster.engine.compilerold.impl.TerminateEarlyException;
import org.ringingmaster.engine.compiler.CompileTerminationReason;
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
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.PLAIN_LEAD;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
public class LeadBasedCompile implements Function<LeadBasedCompilePipelineData, LeadBasedCompilePipelineData> {

    private final Logger log = LoggerFactory.getLogger(LeadBasedCompile.class);

    public static final int EMPTY_PART_TOLERANCE = 2; //TODO this should be paramaterised

    class State {

        // inputs
        private final Supplier<Boolean> allowTerminateEarly;
        private final Touch touch;
        private final ImmutableList<LeadBasedDecomposedCall> callSequence;
        private final ImmutableMap<String, NotationCall> callLookupByName;
        private final String logPreamble;

        // internal data.
        private int callSequenceIndex;
        private int partIndex;
        private LeadBasedDecomposedCall nextCall;
        private MethodRow currentMethodRow;
        private MaskedNotation maskedNotation;
        private final List<MethodLead> leads = new ArrayList<>();

        //outputs
        private Optional<Method> method = Optional.empty();
        private Optional<CompileTerminationReason> terminationReason = Optional.empty();
        private Optional<String> terminateNotes = Optional.empty();

        State(Supplier<Boolean> allowTerminateEarly, Touch touch, ImmutableList<LeadBasedDecomposedCall> callSequence, ImmutableMap<String, NotationCall> callLookupByName, String logPreamble)  {
            this.allowTerminateEarly = checkNotNull(allowTerminateEarly);
            this.touch = checkNotNull(touch);
            this.callSequence = checkNotNull(callSequence);
            this.callLookupByName = checkNotNull(callLookupByName);
            this.logPreamble = checkNotNull(logPreamble);
        }
    }

    @Override
    public LeadBasedCompilePipelineData apply(LeadBasedCompilePipelineData data) {

        if (data.isTerminated()) {
            return data;
        }

        State state = new State(() -> false,
                data.getParse().getUnderlyingTouch(),
                data.getCallSequence(),
                data.getLookupByName(),
                data.getLogPreamble());

        compileTouch(state);

        return data
                .terminate(state.terminationReason.get(), state.terminateNotes.get())
                .setCreatedMethod(state.method);
    }

    private void compileTouch(State state) {
        log.debug("{} > compile lead based touch", state.logPreamble);
        log.debug("{}  - part [{}]", state.logPreamble, state.partIndex);
        int partIndex = 0;
        // This is required here to handle the case when the first parts are omitted, and a check for empty parts are required.
        state.callSequenceIndex = -1;
        if (!isPlainCourse(state)) {
            advanceToNextCall(state);
        }

        state.currentMethodRow = createStartChange(state);
        state.maskedNotation = new MaskedNotation(state.touch.getNonSplicedActiveNotation().get());

        if (state.maskedNotation.getRowCount() == 0) {
            terminate(CompileTerminationReason.INVALID_TOUCH, "Notation [" + state.maskedNotation.getNameIncludingNumberOfBells() + "] has no rows.", state);
        }
        while (!isTerminated(state)) {
            log.debug("{}   - lead [{}]", state.logPreamble, state.leads.size());
            final MethodLead lead = compileLead( state);
            state.leads.add(lead);
            state.currentMethodRow = lead.getLastRow();
            checkTerminationMaxLeads(state);
            checkTerminateEarly(state);
        }
        state.method = Optional.of(MethodBuilder.buildMethod(state.touch.getNumberOfBells(), state.leads));
        log.debug("{} < compile lead based touch", state.logPreamble);
    }

    private boolean isPlainCourse(State state) {
        // A plain course will have no callSequence length
        return state.callSequence.size() == 0;
    }

    private MethodRow createStartChange(State state) {
        MethodRow startChange = state.touch.getStartChange();
        checkState(startChange.getNumberOfBells() == state.touch.getNumberOfBells());
        Stroke startStroke = state.touch.getStartStroke();

        startChange = startChange.setStroke(startStroke);

        return startChange;
    }

    private MethodLead compileLead(State state) {

        final List<MethodRow> rows = new ArrayList<>();
        final List<Integer> leadSeparatorPositions = new ArrayList<>();

        rows.add(state.currentMethodRow);

        for (NotationRow notationRow : state.maskedNotation) {

            buildNextRow(notationRow, state);
            rows.add(state.currentMethodRow);

            checkTerminationChange(state);
            checkTerminationMaxRows(state);
            if (isTerminated(state)) {
                break;
            }
            tryToMakeACall(state);
        }

//TODO		addLeadSeparator(currentNotation, rows, leadSeparatorPositions);

        final MethodLead lead = MethodBuilder.buildLead(state.touch.getNumberOfBells(), rows, leadSeparatorPositions);
        return lead;
    }

    private void buildNextRow(final NotationRow notationRow, State state) {
        if (notationRow.isAllChange()) {
            state.currentMethodRow = MethodBuilder.buildAllChangeRow(state.currentMethodRow);
        }
        else {
            state.currentMethodRow = MethodBuilder.buildRowWithPlaces(state.currentMethodRow, notationRow);
        }

        log.trace( "{} add row [{}]", state.logPreamble, state.currentMethodRow);
    }

    private void tryToMakeACall( State state) {
        if (state.nextCall != null && state.maskedNotation.isAtCallPoint()) {
            boolean callConsumed = applyNextCall(state);
            if (callConsumed) {
                advanceToNextCall(state);
            }
        }
    }

    private void advanceToNextCall(State state) {
        int enteringPartIndex = state.partIndex;
        do {
            state.callSequenceIndex++;
            if (state.callSequenceIndex >= state.callSequence.size()) {
                //New Part
                state.partIndex++;
                if (state.partIndex >= enteringPartIndex + EMPTY_PART_TOLERANCE) {
                    terminate(CompileTerminationReason.EMPTY_PARTS, Integer.toString(EMPTY_PART_TOLERANCE), state);
                    break;
                }
                log.debug("{}  - part [{}]", state.logPreamble, state.partIndex);
                state.callSequenceIndex = 0;
            }
            state.nextCall = state.callSequence.get(state.callSequenceIndex);

        } while (!state.nextCall.getVariance().includePart(state.partIndex));
    }

    private boolean applyNextCall( State state) {
        if (state.nextCall.getParseType().equals(PLAIN_LEAD)) {
            // No Call, but consume the call.
            log.debug("{} Apply Plain lead", state.logPreamble);
        }
        else {
            NotationCall call = state.callLookupByName.get(state.nextCall.getCallName());
            log.debug("{} Apply call [{}]", state.logPreamble, call);
            state.maskedNotation.applyCall(call, state.logPreamble);
        }
        // We consumed the call
        return true;
    }

    private void checkTerminationMaxLeads(State state) {
        if (state.touch.getTerminationMaxLeads().isPresent() &&
                state.leads.size() >= state.touch.getTerminationMaxLeads().get()) {
            terminate(CompileTerminationReason.LEAD_COUNT, state.touch.getTerminationMaxLeads().get().toString(), state);
        }
    }

    private void checkTerminateEarly(State state) {
        if (state.allowTerminateEarly.get()) {
            throw new TerminateEarlyException("Terminate Early request");
        }
    }

    private void checkTerminationMaxRows(State state) {
        if (state.currentMethodRow.getRowNumber() >= state.touch.getTerminationMaxRows()) {
            terminate(CompileTerminationReason.ROW_COUNT, Integer.toString(state.touch.getTerminationMaxRows()), state);
        }
    }

    private void checkTerminationChange(State state) {
        if (state.touch.getTerminationChange().isPresent() &&
                state.touch.getTerminationChange().get().equals(state.currentMethodRow)) {
            terminate(CompileTerminationReason.SPECIFIED_ROW, state.touch.getTerminationChange().get().toString(), state);
        }
    }

    private void terminate(final CompileTerminationReason terminationReason, String terminateNotes, State state) {
        if (!isTerminated(state)) {
            log.debug("{}  - Terminate [{}] {}", state.logPreamble, terminateNotes, terminationReason);
            state.terminationReason = Optional.of(terminationReason);
            state.terminateNotes = Optional.of(terminateNotes);
        }
        else  {
            log.warn("Requesting second terminate [{}]{}", terminationReason, terminateNotes);
        }
    }

    private boolean isTerminated(State state) {
        return state.terminationReason.isPresent();
    }

}
