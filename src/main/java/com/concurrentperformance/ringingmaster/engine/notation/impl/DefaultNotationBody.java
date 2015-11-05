package com.concurrentperformance.ringingmaster.engine.notation.impl;

import com.concurrentperformance.ringingmaster.engine.NumberOfBells;
import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import com.concurrentperformance.ringingmaster.engine.notation.NotationCall;
import com.concurrentperformance.ringingmaster.engine.notation.NotationMethodCallingPosition;
import com.concurrentperformance.ringingmaster.engine.notation.NotationRow;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import net.jcip.annotations.Immutable;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Default implementation of NotationBody interface. This implementation is Immutable,
 * but constructs and keeps a fully unwrapped version of the list of elements that
 * have had the folded palindrome symmetry and LE values already applied to simplify
 * iteration.
 *
 * @author Stephen Lake
 */
@Immutable
public class DefaultNotationBody extends DefaultNotation implements NotationBody {

	/** The raw NotationRow's - only contains half the notation elements for folded palindrome notations */
	private final ImmutableList<ImmutableList<NotationRow>> rawNotationRowsSets;
	private final boolean foldedPalindrome;
	private final String leadHeadCode;
	private final boolean cannedCalls;
	private final Set<NotationCall> notationCalls;
	private final NotationCall defaultCall;
	private final SortedSet<Integer> callInitiationRow;
	private final SortedSet<NotationMethodCallingPosition> methodBasedCallingPositions;
	private final String spliceIdentifier;

	/**
	 * Construct a new notation using a simple list of sets of elements.
	 * Use the NotationBuilder to construct.
	 */
	DefaultNotationBody(final String name,
	                    final NumberOfBells numberOfWorkingBells,
	                    final List<NotationRow> normalisedNotationElements,
	                    final List<List<NotationRow>> notationRowsSets,
	                    final boolean foldedPalindrome,
	                    final String leadHeadCode,
	                    final boolean cannedCalls,
	                    final Set<NotationCall> notationCalls,
	                    final NotationCall defaultCall,
	                    final Set<Integer> callInitiationRow,
	                    final Set<NotationMethodCallingPosition> methodBasedCallingPositions,
	                    final String spliceIdentifier) {
		super(name, numberOfWorkingBells, normalisedNotationElements);
		ImmutableList.Builder<ImmutableList<NotationRow>> rawNotationSetsBuilder = ImmutableList.<ImmutableList<NotationRow>>builder();
		for (List<NotationRow> notationRows : notationRowsSets) {
			ImmutableList<NotationRow> immutableRows = ImmutableList.<NotationRow>builder().addAll(notationRows).build();
			rawNotationSetsBuilder.add(immutableRows);
		}
		this.rawNotationRowsSets = rawNotationSetsBuilder.build();
		this.foldedPalindrome = foldedPalindrome;
		this.leadHeadCode = leadHeadCode;
		this.cannedCalls = cannedCalls;
		this.notationCalls = ImmutableSet.<NotationCall>builder().addAll(checkNotNull(notationCalls)).build();
		this.defaultCall = defaultCall;
		this.callInitiationRow = ImmutableSortedSet.<Integer>naturalOrder().addAll(checkNotNull(callInitiationRow)).build();
		this.methodBasedCallingPositions = ImmutableSortedSet.<NotationMethodCallingPosition>naturalOrder().addAll(checkNotNull(methodBasedCallingPositions)).build();
		this.spliceIdentifier = spliceIdentifier;
	}

	@Override
	public String getNameIncludingNumberOfBells() {
		return getName() + " " + getNumberOfWorkingBells().getName();
	}

	@Override
	public boolean isFoldedPalindrome() {
		return foldedPalindrome;
	}

	@Override
	public String getLeadHeadCode() {
		return leadHeadCode;
	}

	@Override
	public boolean isCannedCalls() {
		return cannedCalls;
	}

	@Override
	public String getNotationDisplayString(final boolean concise) {
		final StringBuilder buf = new StringBuilder();
		boolean firstTime = true;
		for (List<NotationRow> rawNotationRowsSet : rawNotationRowsSets) {
			if (!firstTime) {
				buf.append(",");
			}
			firstTime = false;
			buf.append(getAsDisplayString(rawNotationRowsSet, concise)) ;
		}
		return buf.toString();
	}



	@Override
	public Set<NotationCall> getCalls() {
		return notationCalls;
	}

	@Override
	public NotationCall getDefaultCall() {
		return defaultCall;
	}

	@Override
	public SortedSet<Integer> getCallInitiationRows() {
		return callInitiationRow;
	}

	@Override
	public SortedSet<NotationMethodCallingPosition> getMethodBasedCallingPositions() {
		return methodBasedCallingPositions;
	}

	@Override
	public NotationMethodCallingPosition findMethodBasedCallingPositionByName(String callingPositionName) {
		checkNotNull(callingPositionName, "callingPositionName must not be null");
		for (NotationMethodCallingPosition methodBasedCallingPosition : methodBasedCallingPositions) {
			if (methodBasedCallingPosition.getName().equals(callingPositionName)) {
				return methodBasedCallingPosition;
			}
		}
		return null;
	}

	@Override
	public String getRawNotationDisplayString(int notationIndex, boolean concise) {
		if (notationIndex >= rawNotationRowsSets.size()) {
			return "";
		}
		return getAsDisplayString(rawNotationRowsSets.get(notationIndex), concise);
	}

	@Override
	public String getSpliceIdentifier() {
		return spliceIdentifier;
	}

	@Override
	public String toString() {
		final StringBuilder buf = new StringBuilder();
		buf.append("NotationBody [name=").append(getName());
		buf.append(", numberOfWorkingBells=").append(getNumberOfWorkingBells());
		buf.append(", notation=").append(getNotationDisplayString(false)) ;
		buf.append(", leadHeadCode=").append(getLeadHeadCode());
		buf.append(", useCannedCalls=").append(isCannedCalls());
		buf.append(", calls=").append(getCalls());
		buf.append(", defaultCall=").append(getDefaultCall());
		buf.append(", callInitiationRow=").append(getCallInitiationRows());
		buf.append(", methodBasedCallingPositions=").append(getMethodBasedCallingPositions());
		buf.append(", spliceIdentifier=").append(getSpliceIdentifier());
		buf.append("]");
		return buf.toString();
	}

}