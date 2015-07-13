package com.concurrentperformance.ringingmaster.engine.touch.compiler.impl;

import com.concurrentperformance.ringingmaster.engine.NumberOfBells;
import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import com.concurrentperformance.ringingmaster.engine.notation.NotationCall;
import com.concurrentperformance.ringingmaster.engine.notation.NotationMethodCallingPosition;
import com.concurrentperformance.ringingmaster.engine.notation.NotationRow;
import com.google.common.base.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A construct that allows the iteration of an embedded current notation,
 * but also allows the application of a call that takes precedence
 * over the current notation.
 * NOTE: Only one iterator should be in use at any one time.
 *
 * User: Stephen
 */
public class MaskedNotation implements NotationBody {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private NotationBody currentNotation;
	private int iteratorIndex = 0;
	private Optional<NotationCall> call = Optional.absent();
	private int callIndex;

	public MaskedNotation(NotationBody activeNotation) {
		setCurrentNotation(activeNotation);
	}

	public void setCurrentNotation(NotationBody currentNotation) {
		checkNotNull(currentNotation, "currentNotation must not be null");
		this.currentNotation = currentNotation;
	}

	public void applyCall(NotationCall call, String logPreamble) {
		log.debug("{}   - Applying Call [{}] at index [{}] ", logPreamble, call, iteratorIndex);

		this.call = Optional.fromNullable(call);
		this.callIndex = 0;
	}

	public boolean isAtCallPoint() {
		return currentNotation.getCallInitiationRows().contains(iteratorIndex);
	}

	@Override
	public NotationRow getRow(int index) {
		if (call.isPresent()) {
			if (callIndex < call.get().getRowCount()) {
				return call.get().getRow(callIndex++);
			}
			else {
				call = Optional.absent();
				callIndex = 0;
			}
		}
		return currentNotation.getRow(index);
	}

	@Override
	public int getRowCount() {
		return currentNotation.getRowCount();
	}

	@Override
	public SortedSet<Integer> getCallInitiationRows() {
		return currentNotation.getCallInitiationRows();
	}

	@Override
	public SortedSet<NotationMethodCallingPosition> getMethodBasedCallingPositions() {
		return currentNotation.getMethodBasedCallingPositions();
	}

	@Override
	public NotationMethodCallingPosition findMethodBasedCallingPositionByName(String callingPositionName) {
		return currentNotation.findMethodBasedCallingPositionByName(callingPositionName);
	}

	@Override
	public String getRawNotationDisplayString(int notationIndex, boolean concise) {
		return currentNotation.getRawNotationDisplayString(notationIndex, concise);
	}

	@Override
	public Iterator<NotationRow> iterator() {

		iteratorIndex = 0;

		return new Iterator<NotationRow>() {


			@Override
			public boolean hasNext() {
				return iteratorIndex < getRowCount();
			}

			@Override
			public NotationRow next() {
				return getRow(iteratorIndex++);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException("DefaultNotationBody.iterator() does not support remove()");
			}
		};
	}


	@Override
	public String getNotationDisplayString(boolean concise) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getName() {
		return currentNotation.getName();
	}

	@Override
	public NumberOfBells getNumberOfWorkingBells() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getNameIncludingNumberOfBells() {
		return currentNotation.getNameIncludingNumberOfBells();
	}

	@Override
	public boolean isFoldedPalindrome() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getLeadHeadCode() {
		return currentNotation.getLeadHeadCode();
	}

	@Override
	public boolean isCannedCalls() {
		return currentNotation.isCannedCalls();
	}

	@Override
	public Set<NotationCall> getCalls() {
		throw new UnsupportedOperationException();
	}

	@Override
	public NotationCall getDefaultCall() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getSpliceIdentifier() {
		throw new UnsupportedOperationException();
	}

	public int getIteratorIndex() {
		return iteratorIndex;
	}
}
