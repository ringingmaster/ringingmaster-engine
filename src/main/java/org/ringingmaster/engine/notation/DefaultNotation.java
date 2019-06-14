package org.ringingmaster.engine.notation;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import org.ringingmaster.engine.NumberOfBells;

import javax.annotation.concurrent.Immutable;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Default implementation of Notation interface. This implementation is Immutable,
 * but constructs and keeps a fully unwrapped version of the list of elements that
 * have had the folded palindrome symmetry and LE values already applied to simplify
 * iteration.
 *
 * @author Steve Lake
 */
@Immutable
class DefaultNotation extends DefaultPlaceSetSequence implements Notation {

    /**
     * The raw PlaceSet's - only contains half the notation elements for folded palindrome notations
     */
    private final ImmutableList<ImmutableList<PlaceSet>> rawNotationRowsSets;
    private final boolean foldedPalindrome;
    private final String leadHeadCode;
    private final boolean cannedCalls;
    private final ImmutableSet<Call> calls;
    private final Call defaultCall;
    private final SortedSet<Integer> callInitiationRow; //TODO Use immutable list
    private final SortedSet<CallingPosition> methodBasedCallingPositions; //TODO Use immutable list
    private final String spliceIdentifier;

    /**
     * Construct a new notation using a simple list of sets of elements.
     * Use the NotationBuilder to construct.
     */
    DefaultNotation(final String name,
                    final NumberOfBells numberOfWorkingBells,
                    final List<PlaceSet> normalisedNotationElements,
                    final List<List<PlaceSet>> notationRowsSets,
                    final boolean foldedPalindrome,
                    final String leadHeadCode,
                    final boolean cannedCalls,
                    final Set<Call> calls,
                    final Call defaultCall,
                    final Set<Integer> callInitiationRow,
                    final Set<CallingPosition> methodBasedCallingPositions,
                    final String spliceIdentifier) {
        super(name, numberOfWorkingBells, normalisedNotationElements);
        ImmutableList.Builder<ImmutableList<PlaceSet>> rawNotationSetsBuilder = ImmutableList.builder();
        for (List<PlaceSet> placeSets : notationRowsSets) {
            ImmutableList<PlaceSet> immutableRows = ImmutableList.<PlaceSet>builder().addAll(placeSets).build();
            rawNotationSetsBuilder.add(immutableRows);
        }
        this.rawNotationRowsSets = rawNotationSetsBuilder.build();
        this.foldedPalindrome = foldedPalindrome;
        this.leadHeadCode = leadHeadCode;
        this.cannedCalls = cannedCalls;
        this.calls = ImmutableSet.<Call>builder().addAll(checkNotNull(calls)).build();
        this.defaultCall = defaultCall;
        this.callInitiationRow = ImmutableSortedSet.<Integer>naturalOrder().addAll(checkNotNull(callInitiationRow)).build();
        this.methodBasedCallingPositions = ImmutableSortedSet.<CallingPosition>naturalOrder().addAll(checkNotNull(methodBasedCallingPositions)).build();
        this.spliceIdentifier = spliceIdentifier;
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
        for (List<PlaceSet> rawNotationRowsSet : rawNotationRowsSets) {
            if (!firstTime) {
                buf.append(",");
            }
            firstTime = false;
            buf.append(NotationBuilderHelper.getAsDisplayString(rawNotationRowsSet, concise));
        }
        return buf.toString();
    }


    @Override
    public Set<Call> getCalls() {
        return calls;
    }

    @Override
    public Call getDefaultCall() {
        return defaultCall;
    }

    @Override
    public SortedSet<Integer> getCallInitiationRows() {
        return callInitiationRow;
    }

    @Override
    public SortedSet<CallingPosition> getCallingPositions() {
        return methodBasedCallingPositions;
    }

    @Override
    public CallingPosition findMethodBasedCallingPositionByName(String callingPositionName) {
        checkNotNull(callingPositionName, "callingPositionName must not be null");
        for (CallingPosition methodBasedCallingPosition : methodBasedCallingPositions) {
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
        return NotationBuilderHelper.getAsDisplayString(rawNotationRowsSets.get(notationIndex), concise);
    }

    @Override
    public String getSpliceIdentifier() {
        return spliceIdentifier;
    }

    // TODO regenerate when immutable
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DefaultNotation that = (DefaultNotation) o;

        if (foldedPalindrome != that.foldedPalindrome) return false;
        if (cannedCalls != that.cannedCalls) return false;
        if (rawNotationRowsSets != null ? !rawNotationRowsSets.equals(that.rawNotationRowsSets) : that.rawNotationRowsSets != null)
            return false;
        if (leadHeadCode != null ? !leadHeadCode.equals(that.leadHeadCode) : that.leadHeadCode != null) return false;
        if (calls != null ? !calls.equals(that.calls) : that.calls != null)
            return false;
        if (defaultCall != null ? !defaultCall.equals(that.defaultCall) : that.defaultCall != null) return false;
        if (callInitiationRow != null ? !callInitiationRow.equals(that.callInitiationRow) : that.callInitiationRow != null)
            return false;
        if (methodBasedCallingPositions != null ? !methodBasedCallingPositions.equals(that.methodBasedCallingPositions) : that.methodBasedCallingPositions != null)
            return false;
        return spliceIdentifier != null ? spliceIdentifier.equals(that.spliceIdentifier) : that.spliceIdentifier == null;
    }

    // TODO regenerate when immutable
    @Override
    public int hashCode() {
        int result = rawNotationRowsSets != null ? rawNotationRowsSets.hashCode() : 0;
        result = 31 * result + (foldedPalindrome ? 1 : 0);
        result = 31 * result + (leadHeadCode != null ? leadHeadCode.hashCode() : 0);
        result = 31 * result + (cannedCalls ? 1 : 0);
        result = 31 * result + (calls != null ? calls.hashCode() : 0);
        result = 31 * result + (defaultCall != null ? defaultCall.hashCode() : 0);
        result = 31 * result + (callInitiationRow != null ? callInitiationRow.hashCode() : 0);
        result = 31 * result + (methodBasedCallingPositions != null ? methodBasedCallingPositions.hashCode() : 0);
        result = 31 * result + (spliceIdentifier != null ? spliceIdentifier.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder();
        buf.append("Notation [name=").append(getName());
        buf.append(", numberOfWorkingBells=").append(getNumberOfWorkingBells());
        buf.append(", notation=").append(getNotationDisplayString(false));
        buf.append(", leadHeadCode=").append(getLeadHeadCode());
        buf.append(", cannedCalls=").append(isCannedCalls());
        buf.append(", calls=").append(getCalls());
        buf.append(", defaultCall=").append(getDefaultCall());
        buf.append(", callInitiationRow=").append(getCallInitiationRows());
        buf.append(", methodBasedCallingPositions=").append(getCallingPositions());
        buf.append(", spliceIdentifier=").append(getSpliceIdentifier());
        buf.append("]");
        return buf.toString();
    }
}