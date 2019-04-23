package org.ringingmaster.engine.compiler.compile;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.ringingmaster.engine.compiler.CompileTerminationReason;
import org.ringingmaster.engine.compiler.denormaliser.DenormalisedCall;
import org.ringingmaster.engine.compiler.TerminateEarlyException;
import org.ringingmaster.engine.composition.Composition;
import org.ringingmaster.engine.method.Lead;
import org.ringingmaster.engine.method.Method;
import org.ringingmaster.engine.method.MethodBuilder;
import org.ringingmaster.engine.method.Row;
import org.ringingmaster.engine.method.Stroke;
import org.ringingmaster.engine.notation.Call;
import org.ringingmaster.engine.notation.PlaceSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
public abstract class Compile<T extends DenormalisedCall, PASS_THROUGH> {

    private final Logger log = LoggerFactory.getLogger(Compile.class);

    public static final int EMPTY_PART_TOLERANCE = 2; //TODO this should be paramaterised

    public interface  Result {

        Optional<Method> getMethod();
        CompileTerminationReason getTerminateReason();
        String getTerminateNotes();
    }

    protected class State implements Result {

        // inputs
        private final Supplier<Boolean> allowTerminateEarly;
        private final Composition composition;
        private final ImmutableList<T> denormalisedCallSequence;
        private final ImmutableMap<String, Call> callLookupByName;
        private final String logPreamble;
        private final PASS_THROUGH passthrough;

        // internal data.
        private int callSequenceIndex;
        private int partIndex;
        private T nextDenormalisedCall;
        private Row currentRow;
        private MaskedNotation maskedNotation;
        private final List<Lead> leads = new ArrayList<>();

        //outputs
        private Optional<Method> method = Optional.empty();
        private Optional<CompileTerminationReason> terminationReason = Optional.empty();
        private Optional<String> terminateNotes = Optional.empty();


        State(Supplier<Boolean> allowTerminateEarly, Composition composition, ImmutableList<T> denormalisedCallSequence,
              ImmutableMap<String, Call> callLookupByName, String logPreamble, PASS_THROUGH passthrough)  {
            this.allowTerminateEarly = checkNotNull(allowTerminateEarly);
            this.composition = checkNotNull(composition);
            this.denormalisedCallSequence = checkNotNull(denormalisedCallSequence);
            this.callLookupByName = checkNotNull(callLookupByName);
            this.logPreamble = checkNotNull(logPreamble);
            this.passthrough = passthrough;
        }

        @Override
        public Optional<Method> getMethod() {
            return method;
        }

        @Override
        public CompileTerminationReason getTerminateReason() {
            return terminationReason.get();
        }

        @Override
        public String getTerminateNotes() {
            return terminateNotes.get();
        }

        public T getNextDenormalisedCall() {
            return nextDenormalisedCall;
        }

        public MaskedNotation getMaskedNotation() {
            return maskedNotation;
        }

        public String getLogPreamble() {
            return logPreamble;
        }

        public PASS_THROUGH getPassthrough() {
            return passthrough;
        }

        public ImmutableMap<String, Call> getCallLookupByName() {
            return callLookupByName;
        }

        public Composition getComposition() {
            return composition;
        }

        public Row getCurrentRow() {
            return currentRow;
        }
    }


    public Result compileComposition(Composition composition, ImmutableList<T> denormalisedCallSequence,
                                     ImmutableMap<String, Call> callLookupByName, String logPreamble, PASS_THROUGH passthrough) {

        State state = new State(() -> false,
                composition, denormalisedCallSequence,
                callLookupByName, logPreamble, passthrough);

        log.debug("{}  - part [{}]", state.logPreamble, state.partIndex);

        // This is required here to handle the case when the first parts are omitted, and a check for empty parts are required.
        state.callSequenceIndex = -1;
        if (!isPlainCourse(state)) {
            advanceToNextCall(state);
        }

        state.currentRow = createStartChange(state);
        state.maskedNotation = new MaskedNotation(state.composition.getNonSplicedActiveNotation().get());

        if (state.maskedNotation.size() == 0) {
            terminate(CompileTerminationReason.INVALID_COMPOSITION, "Notation [" + state.maskedNotation.getNameIncludingNumberOfBells() + "] has no rows.", state);
        }
        while (!isTerminated(state)) {
            log.debug("{}   - lead [{}]", state.logPreamble, state.leads.size());
            final Lead lead = compileLead( state);
            state.leads.add(lead);
            state.currentRow = lead.getLastRow();
            checkTerminationMaxLeads(state);
            checkTerminateEarly(state);
        }
        state.method = Optional.of(MethodBuilder.buildMethod(state.composition.getNumberOfBells(), state.leads));

        return state;
    }

    private boolean isPlainCourse(State state) {
        // A plain course will have no denormalisedCallSequence length
        return state.denormalisedCallSequence.size() == 0;
    }

    private Row createStartChange(State state) {
        Row startChange = state.composition.getStartChange();
        checkState(startChange.getNumberOfBells() == state.composition.getNumberOfBells());
        Stroke startStroke = state.composition.getStartStroke();

        startChange = startChange.setStroke(startStroke);

        return startChange;
    }

    private Lead compileLead(State state) {

        final List<Row> rows = new ArrayList<>();
        final List<Integer> leadSeparatorPositions = new ArrayList<>();

        rows.add(state.currentRow);

        for (PlaceSet placeSet : state.maskedNotation) {

            buildNextRow(placeSet, state);
            rows.add(state.currentRow);

            checkTerminationChange(state);
            checkTerminationMaxRows(state);
            if (isTerminated(state)) {
                break;
            }
            tryToMakeACall(state);
        }

//TODO		addLeadSeparator(currentNotation, rows, leadSeparatorPositions);

        final Lead lead = MethodBuilder.buildLead(state.composition.getNumberOfBells(), rows, leadSeparatorPositions);
        return lead;
    }

    private void buildNextRow(final PlaceSet placeSet, State state) {
        if (placeSet.isAllChange()) {
            state.currentRow = MethodBuilder.buildAllChangeRow(state.currentRow);
        }
        else {
            state.currentRow = MethodBuilder.buildRowWithPlaces(state.currentRow, placeSet);
        }

        log.trace( "{} add row [{}]", state.logPreamble, state.currentRow);
    }

    private void tryToMakeACall(State state) {
        if (state.nextDenormalisedCall != null && state.maskedNotation.isAtCallPoint()) {

            boolean callConsumed = applyNextCall(state);
            if (callConsumed) {
                advanceToNextCall(state);
            }
        }
    }

    protected abstract boolean applyNextCall(State state) ;

    private void advanceToNextCall(State state) {
        int enteringPartIndex = state.partIndex;
        do {
            state.callSequenceIndex++;
            if (state.callSequenceIndex >= state.denormalisedCallSequence.size()) {
                //New Part
                state.partIndex++;
                if (state.partIndex >= enteringPartIndex + EMPTY_PART_TOLERANCE) {
                    terminate(CompileTerminationReason.EMPTY_PARTS, Integer.toString(EMPTY_PART_TOLERANCE), state);
                    break;
                }
                log.debug("{}  - part [{}]", state.logPreamble, state.partIndex);
                state.callSequenceIndex = 0;
            }
            state.nextDenormalisedCall = state.denormalisedCallSequence.get(state.callSequenceIndex);

        } while (!state.nextDenormalisedCall.getVariance().includePart(state.partIndex));
    }


    private void checkTerminationMaxLeads(State state) {
        if (state.composition.getTerminationMaxLeads().isPresent() &&
                state.leads.size() >= state.composition.getTerminationMaxLeads().get()) {
            terminate(CompileTerminationReason.LEAD_COUNT, state.composition.getTerminationMaxLeads().get().toString(), state);
        }
    }

    private void checkTerminateEarly(State state) {
        if (state.allowTerminateEarly.get()) {
            throw new TerminateEarlyException("Terminate Early request");
        }
    }

    private void checkTerminationMaxRows(State state) {
        if (state.currentRow.getRowIndex() >= state.composition.getTerminationMaxRows()) {
            terminate(CompileTerminationReason.ROW_COUNT, Integer.toString(state.composition.getTerminationMaxRows()), state);
        }
    }

    private void checkTerminationChange(State state) {
        if (state.composition.getTerminationChange().isPresent() &&
                state.composition.getTerminationChange().get().equals(state.currentRow)) {
            terminate(CompileTerminationReason.SPECIFIED_ROW, state.composition.getTerminationChange().get().toString(), state);
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

    protected boolean isTerminated(State state) {
        return state.terminationReason.isPresent();
    }
}
