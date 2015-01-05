package com.concurrentperformance.ringingmaster.engine.notation.impl;

import com.concurrentperformance.ringingmaster.engine.NumberOfBells;
import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import com.concurrentperformance.ringingmaster.engine.notation.NotationCall;
import com.concurrentperformance.ringingmaster.engine.notation.NotationMethodCallingPosition;
import com.concurrentperformance.ringingmaster.engine.notation.NotationRow;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

/**
 * A simplified implementation of the NotationBody interface just for building a plain course.
 *
 * User: Stephen
 */
public class PlainCourseNotationBody extends DefaultNotation implements NotationBody {

	PlainCourseNotationBody(String name, NumberOfBells numberOfWorkingBells, List<NotationRow> normalisedNotationElements) {
		super(name, numberOfWorkingBells, normalisedNotationElements);
	}

	@Override
	public String getNameIncludingNumberOfBells() {
		return getName() + " " + getNumberOfWorkingBells().getName();
	}

	@Override
	public boolean isFoldedPalindrome() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getLeadHeadCode()  {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<NotationCall> getCalls() {
		return Collections.emptySet();
	}

	@Override
	public NotationCall getDefaultCall() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getSpliceIdentifier() {
		throw new UnsupportedOperationException();
	}

	@Override
	public SortedSet<Integer> getCallInitiationRows() {
		throw new UnsupportedOperationException();
	}

	@Override
	public SortedSet<NotationMethodCallingPosition> getMethodBasedCallingPositions() {
		throw new UnsupportedOperationException();
	}

	@Override
	public NotationMethodCallingPosition findMethodBasedCallingPositionByName(String callingPositionName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getRawNotationDisplayString(boolean concise) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getRawLeadEndDisplayString(boolean concise) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getNotationDisplayString(boolean concise) {
		throw new UnsupportedOperationException();
	}
}
