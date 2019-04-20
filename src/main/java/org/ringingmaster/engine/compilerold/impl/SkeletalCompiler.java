package org.ringingmaster.engine.compilerold.impl;

import org.ringingmaster.engine.analyser.proof.Proof;
import org.ringingmaster.engine.compiler.common.DenormalisedCall;
import org.ringingmaster.engine.compiler.compiledcomposition.CompiledComposition;
import org.ringingmaster.engine.compilerold.Compiler;
import org.ringingmaster.engine.compiler.CompileTerminationReason;
import org.ringingmaster.engine.composition.Composition;
import org.ringingmaster.engine.method.Lead;
import org.ringingmaster.engine.method.Method;
import org.ringingmaster.engine.method.Row;
import org.ringingmaster.engine.method.Stroke;
import org.ringingmaster.engine.method.MethodBuilder;
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.notation.NotationCall;
import org.ringingmaster.engine.notation.NotationRow;
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
public abstract class SkeletalCompiler<DCT extends DenormalisedCall> implements Compiler {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	public static final int EMPTY_PART_TOLERANCE = 2;

	// inputs
	private final Composition composition;
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
	private volatile Optional<Proof> analysis = Optional.empty();
	private volatile Optional<CompileTerminationReason> terminationReason = Optional.empty();
	private volatile Optional<String> terminateNotes = Optional.empty();
	private volatile CompiledComposition compiledComposition;


	public SkeletalCompiler(Composition composition, String logPreamble) {
		this.composition = checkNotNull(composition);
		this.logPreamble = checkNotNull(logPreamble);
	}

	public CompiledComposition compile(boolean withAnalysis, Supplier<Boolean> shouldTerminateEarly) {
		log.debug("{}> Start compiling [{}]", logPreamble, composition.getTitle());
		long start = System.currentTimeMillis();

		final Optional<String> invalidComposition = checkInvalidComposition(composition);

		if (invalidComposition.isPresent()) {
			terminate(CompileTerminationReason.INVALID_COMPOSITION, invalidComposition.get());
		}
		else { preCompile(composition);
			checkTerminateEarly(shouldTerminateEarly);

			buildCallLookupByName();
			checkTerminateEarly(shouldTerminateEarly);

			compileComposition(shouldTerminateEarly);
			checkTerminateEarly(shouldTerminateEarly);

			if (withAnalysis) {
				compileAnalysis();
				checkTerminateEarly(shouldTerminateEarly);
			}
		}
		long compileTime = System.currentTimeMillis() - start;
//		compiledComposition = new DefaultProof(composition, terminationReason.get(), terminateNotes, method, proof, compileTime);
		log.debug("{}< Finished compiling [{}] in [{}]ms", logPreamble, composition.getTitle(), compileTime);
		return compiledComposition;
	}

	private void checkTerminateEarly(Supplier<Boolean> shouldTerminateEarly) {
		if (shouldTerminateEarly.get()) {
			throw new TerminateEarlyException("Terminate Early request");
		}
	}

	private Optional<String> checkInvalidComposition(Composition composition) {
		if (composition.isSpliced()) {
			if (composition.getAvailableNotations().size() == 0) {
				return Optional.of("Spliced composition has no valid methods");
			}
		}
		else { // Not Spliced
			if (composition.getNonSplicedActiveNotation() == null) {
				return Optional.of("No active method");
			}
		}

		return Optional.empty();
	}

	protected abstract void preCompile(Composition composition);

	protected abstract List<DCT> getImmutableCallSequence();

	private void buildCallLookupByName() {
		callLookupByName = new CallMapBuilder(composition, logPreamble).createCallMap();
	}

	private void compileComposition(Supplier<Boolean> shouldTerminateEarly) {
		log.debug("{} > create composition", logPreamble);
		log.debug("{}  - part [{}]", logPreamble, partIndex);
		partIndex = 0;
		// This is required here to handle the case when the first parts are omitted, and a check for empty parts are required.
		callSequenceIndex = -1;
		if (!isPlainCourse()) {
			advanceToNextCall();
		}

		Row startChange = createStartChange();

		MaskedNotation maskedNotation = new MaskedNotation(composition.getNonSplicedActiveNotation().get());

		final List<Lead> leads = new ArrayList<>();

		if (maskedNotation.getRowCount() == 0) {
			terminate(CompileTerminationReason.INVALID_COMPOSITION, "Notation [" + maskedNotation.getNameIncludingNumberOfBells() + "] has no rows.");
		}
		while (!isTerminated()) {
			log.debug("{}   - lead [{}]", logPreamble, leads.size());
			final Lead lead = compileLead(maskedNotation, startChange);
			leads.add(lead);
			startChange = lead.getLastRow();
			checkTerminationMaxLeads(leads);
			checkTerminateEarly(shouldTerminateEarly);
		}
		method = Optional.of(MethodBuilder.buildMethod(composition.getNumberOfBells(), leads));
		log.debug("{} < create composition", logPreamble);
	}

	private Row createStartChange() {
		Row startChange = composition.getStartChange();
		checkState(startChange.getNumberOfBells() == composition.getNumberOfBells());
		Stroke startStroke = composition.getStartStroke();

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

	private Lead compileLead(final MaskedNotation maskedNotation, Row currentRow) {

		final List<Row> rows = new ArrayList<>();
		final List<Integer> leadSeparatorPositions = new ArrayList<>();

		rows.add(currentRow);

		for (NotationRow notationRow : maskedNotation) {

			currentRow = buildNextRow(notationRow, currentRow);
			rows.add(currentRow);

			checkTerminationChange(currentRow);
			checkTerminationMaxRows(currentRow);
			if (isTerminated()) {
				break;
			}
			tryToMakeACall(maskedNotation, currentRow);
		}

//TODO		addLeadSeparator(currentNotation, rows, leadSeparatorPositions);

		final Lead lead = MethodBuilder.buildLead(composition.getNumberOfBells(), rows, leadSeparatorPositions);
		return lead;
	}

	private void tryToMakeACall(MaskedNotation maskedNotation, Row currentRow) {
		if (nextCall != null && maskedNotation.isAtCallPoint()) {
			NotationCall call = callLookupByName.get(nextCall.getCallName());
			boolean callConsumed = applyNextCall(maskedNotation, currentRow, nextCall, call);
			if (callConsumed) {
				advanceToNextCall();
			}
		}
	}

	protected abstract boolean applyNextCall(MaskedNotation maskedNotation, Row currentRow,
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

	private Row buildNextRow(final NotationRow notationRow, final Row previousRow) {
		Row nextRow;
		if (notationRow.isAllChange()) {
			nextRow = MethodBuilder.buildAllChangeRow(previousRow);
		}
		else {
			nextRow = MethodBuilder.buildRowWithPlaces(previousRow, notationRow);
		}
		return nextRow;
	}

	private void addLeadSeparator(NotationBody notationBody, List<Row> rows, List<Integer> leadSeparatorPositions) {
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

	private void checkTerminationMaxLeads(List<Lead> leads) {
		if (composition.getTerminationMaxLeads().isPresent() &&
				leads.size() >= composition.getTerminationMaxLeads().get()) {
			terminate(CompileTerminationReason.LEAD_COUNT, composition.getTerminationMaxLeads().get().toString());
		}
	}

	private void checkTerminationMaxRows(Row newRow) {
		if (newRow.getRowIndex() >= composition.getTerminationMaxRows()) {
			terminate(CompileTerminationReason.ROW_COUNT, Integer.toString(composition.getTerminationMaxRows()));
		}
	}

	private void checkTerminationChange(Row newRow) {
		if (composition.getTerminationChange().isPresent() &&
				composition.getTerminationChange().get().equals(newRow)) {
			terminate(CompileTerminationReason.SPECIFIED_ROW, composition.getTerminationChange().get().toString());
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
//		proof = Optional.of(AnalysisBuilder.buildAnalysisStructure());
//		AnalysisBuilder.falseRowAnalysis(method.get(), proof.get());
	}

	public String getLogPreamble() {
		return logPreamble;
	}
}
