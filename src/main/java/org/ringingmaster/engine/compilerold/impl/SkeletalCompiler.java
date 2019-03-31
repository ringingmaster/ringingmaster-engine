package org.ringingmaster.engine.compilerold.impl;

import org.ringingmaster.engine.analysis.Analysis;
import org.ringingmaster.engine.analysis.impl.AnalysisBuilder;
import org.ringingmaster.engine.compilerold.Compiler;
import org.ringingmaster.engine.compiler.CompileTerminationReason;
import org.ringingmaster.engine.compiler.proof.Proof;
import org.ringingmaster.engine.method.Method;
import org.ringingmaster.engine.method.MethodLead;
import org.ringingmaster.engine.method.MethodRow;
import org.ringingmaster.engine.method.Stroke;
import org.ringingmaster.engine.method.impl.MethodBuilder;
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.notation.NotationCall;
import org.ringingmaster.engine.notation.NotationRow;
import org.ringingmaster.engine.touch.Touch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.concurrent.ThreadSafe;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * TODO comments???
 * User: Stephen
 */
@ThreadSafe
public abstract class SkeletalCompiler<DCT extends DecomposedCall> implements Compiler {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	public static final int EMPTY_PART_TOLERANCE = 2;

	// inputs
	private final Touch touch;
	private final String logPreamble;

	// internal data.
	// For thread safety, these items should only be accessed from inside the compile() method.
	// This is so only one thread touched them.
	private DCT nextCall;
	private int callSequenceIndex;
	private int partIndex;
	private Map<String, NotationCall> callLookupByName;

	// outputs
	private volatile Optional<Method> method = Optional.empty();
	private volatile Optional<Analysis> analysis = Optional.empty();
	private volatile Optional<CompileTerminationReason> terminationReason = Optional.empty();
	private volatile Optional<String> terminateNotes = Optional.empty();
	private volatile Proof proof;


	public SkeletalCompiler(Touch touch, String logPreamble) {
		this.touch = checkNotNull(touch);
		this.logPreamble = checkNotNull(logPreamble);
	}

	public Proof compile(boolean withAnalysis, Supplier<Boolean> shouldTerminateEarly) {
		log.debug("{}> Start compiling [{}]", logPreamble, touch.getTitle());
		long start = System.currentTimeMillis();

		final Optional<String> invalidTouch = checkInvalidTouch(touch);

		if (invalidTouch.isPresent()) {
			terminate(CompileTerminationReason.INVALID_TOUCH, invalidTouch.get());
		}
		else { preCompile(touch);
			checkTerminateEarly(shouldTerminateEarly);

			buildCallLookupByName();
			checkTerminateEarly(shouldTerminateEarly);

			compileTouch(shouldTerminateEarly);
			checkTerminateEarly(shouldTerminateEarly);

			if (withAnalysis) {
				compileAnalysis();
				checkTerminateEarly(shouldTerminateEarly);
			}
		}
		long proofTime = System.currentTimeMillis() - start;
//		proof = new DefaultProof(touch, terminationReason.get(), terminateNotes, method, analysis, proofTime);
		log.debug("{}< Finished compiling [{}] in [{}]ms", logPreamble, touch.getTitle(), proofTime);
		return proof;
	}

	private void checkTerminateEarly(Supplier<Boolean> shouldTerminateEarly) {
		if (shouldTerminateEarly.get()) {
			throw new TerminateEarlyException("Terminate Early request");
		}
	}

	private Optional<String> checkInvalidTouch(Touch touch) {
		if (touch.isSpliced()) {
			if (touch.getAvailableNotations().size() == 0) {
				return Optional.of("Spliced touch has no valid methods");
			}
		}
		else { // Not Spliced
			if (touch.getNonSplicedActiveNotation() == null) {
				return Optional.of("No active method");
			}
		}

		return Optional.empty();
	}

	protected abstract void preCompile(Touch touch);

	protected abstract List<DCT> getImmutableCallSequence();

	private void buildCallLookupByName() {
		callLookupByName = new CallMapBuilder(touch, logPreamble).createCallMap();
	}

	private void compileTouch(Supplier<Boolean> shouldTerminateEarly) {
		log.debug("{} > create touch", logPreamble);
		log.debug("{}  - part [{}]", logPreamble, partIndex);
		partIndex = 0;
		// This is required here to handle the case when the first parts are omitted, and a check for empty parts are required.
		callSequenceIndex = -1;
		if (!isPlainCourse()) {
			advanceToNextCall();
		}

		MethodRow startChange = createStartChange();

		MaskedNotation maskedNotation = new MaskedNotation(touch.getNonSplicedActiveNotation().get());

		final List<MethodLead> leads = new ArrayList<>();

		if (maskedNotation.getRowCount() == 0) {
			terminate(CompileTerminationReason.INVALID_TOUCH, "Notation [" + maskedNotation.getNameIncludingNumberOfBells() + "] has no rows.");
		}
		while (!isTerminated()) {
			log.debug("{}   - lead [{}]", logPreamble, leads.size());
			final MethodLead lead = compileLead(maskedNotation, startChange);
			leads.add(lead);
			startChange = lead.getLastRow();
			checkTerminationMaxLeads(leads);
			checkTerminateEarly(shouldTerminateEarly);
		}
		method = Optional.of(MethodBuilder.buildMethod(touch.getNumberOfBells(), leads));
		log.debug("{} < create touch", logPreamble);
	}

	private MethodRow createStartChange() {
		MethodRow startChange = touch.getStartChange();
		checkState(startChange.getNumberOfBells() == touch.getNumberOfBells());
		Stroke startStroke = touch.getStartStroke();

		startChange = startChange.setStroke(startStroke);

		return startChange;
	}

	/**
	 * Must not call before callSequence has been set
	 */
	private boolean isPlainCourse() {
		// A plain course will have no callSequence length
		return getImmutableCallSequence().size() == 0;
	}

	private MethodLead compileLead(final MaskedNotation maskedNotation, MethodRow currentMethodRow) {

		final List<MethodRow> rows = new ArrayList<>();
		final List<Integer> leadSeparatorPositions = new ArrayList<>();

		rows.add(currentMethodRow);

		for (NotationRow notationRow : maskedNotation) {

			currentMethodRow = buildNextRow(notationRow, currentMethodRow);
			rows.add(currentMethodRow);

			checkTerminationChange(currentMethodRow);
			checkTerminationMaxRows(currentMethodRow);
			if (isTerminated()) {
				break;
			}
			tryToMakeACall(maskedNotation, currentMethodRow);
		}

//TODO		addLeadSeparator(currentNotation, rows, leadSeparatorPositions);

		final MethodLead lead = MethodBuilder.buildLead(touch.getNumberOfBells(), rows, leadSeparatorPositions);
		return lead;
	}

	private void tryToMakeACall(MaskedNotation maskedNotation, MethodRow currentMethodRow) {
		if (nextCall != null && maskedNotation.isAtCallPoint()) {
			NotationCall call = callLookupByName.get(nextCall.getCallName());
			boolean callConsumed = applyNextCall(maskedNotation, currentMethodRow, nextCall, call);
			if (callConsumed) {
				advanceToNextCall();
			}
		}
	}

	protected abstract boolean applyNextCall(MaskedNotation maskedNotation, MethodRow currentMethodRow,
	                                         DCT nextCall, NotationCall call);

	private void advanceToNextCall() {
		int enteringPartIndex = partIndex;
		do {
			callSequenceIndex++;
			if (callSequenceIndex >= getImmutableCallSequence().size()) {
				//New Part
				partIndex++;
				if (partIndex >= enteringPartIndex + EMPTY_PART_TOLERANCE) {
					terminate(CompileTerminationReason.EMPTY_PARTS, Integer.toString(EMPTY_PART_TOLERANCE));
					break;
				}
				log.debug("{}  - part [{}]", logPreamble, partIndex);
				callSequenceIndex = 0;
			}
			nextCall = getImmutableCallSequence().get(callSequenceIndex);

		} while (!nextCall.getVariance().includePart(partIndex));
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

	private void addLeadSeparator(NotationBody notationBody, List<MethodRow> rows, List<Integer> leadSeparatorPositions) {
	/*
	 * TODO this should come from notationBody, and then be checked for being greater than the number of rows.
	 *  then all this logic must change, as must check that we have one more row that we are wanting a seperator after
	 *  otherwise it will be drawn in free space.
	 */
		// calculate lead separator positions.
		final int expectedChangesInLead = notationBody.getRowCount();
		// do we have a full lead? -  if so, then add the seperator
		if ((rows.size()-1) == expectedChangesInLead) {
			leadSeparatorPositions.add(expectedChangesInLead - 1);
		}
	}

	private void checkTerminationMaxLeads(List<MethodLead> leads) {
		if (touch.getTerminationMaxLeads().isPresent() &&
				leads.size() >= touch.getTerminationMaxLeads().get()) {
			terminate(CompileTerminationReason.LEAD_COUNT, touch.getTerminationMaxLeads().get().toString());
		}
	}

	private void checkTerminationMaxRows(MethodRow newRow) {
		if (newRow.getRowNumber() >= touch.getTerminationMaxRows()) {
			terminate(CompileTerminationReason.ROW_COUNT, Integer.toString(touch.getTerminationMaxRows()));
		}
	}

	private void checkTerminationChange(MethodRow newRow) {
		if (touch.getTerminationChange().isPresent() &&
				touch.getTerminationChange().get().equals(newRow)) {
			terminate(CompileTerminationReason.SPECIFIED_ROW, touch.getTerminationChange().get().toString());
		}
	}

	private void terminate(final CompileTerminationReason terminationReason, String terminateNotes) {
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

	private void compileAnalysis() {
		analysis = Optional.of(AnalysisBuilder.buildAnalysisStructure());
		AnalysisBuilder.falseRowAnalysis(method.get(), analysis.get());
	}

	public String getLogPreamble() {
		return logPreamble;
	}
}