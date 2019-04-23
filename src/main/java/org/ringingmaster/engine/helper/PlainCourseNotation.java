package org.ringingmaster.engine.helper;

import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.notation.Call;
import org.ringingmaster.engine.notation.CallingPosition;
import org.ringingmaster.engine.notation.Notation;
import org.ringingmaster.engine.notation.PlaceSet;

import javax.annotation.concurrent.Immutable;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
@Immutable
public class PlainCourseNotation implements Notation {

    private final Notation delegate;
    private final String logPreamble;

    public PlainCourseNotation(Notation delegate, String logPreamble) {
        this.delegate = delegate;
        this.logPreamble = logPreamble;
    }


    @Override
    public String getName() {
        return logPreamble + delegate.getName();
    }

    @Override
    public NumberOfBells getNumberOfWorkingBells() {
        return delegate.getNumberOfWorkingBells();
    }

    @Override
    public String getNameIncludingNumberOfBells() {
        return logPreamble + delegate.getNameIncludingNumberOfBells();
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public PlaceSet get(int index) {
        return delegate.get(index);
    }

    @Override
    public String getNotationDisplayString(boolean concise) {
        return delegate.getNotationDisplayString(concise);
    }

    @Override
    public boolean isFoldedPalindrome() {
        return delegate.isFoldedPalindrome();
    }

    @Override
    public String getLeadHeadCode() {
        return delegate.getLeadHeadCode();
    }

    @Override
    public boolean isCannedCalls() {
        return delegate.isCannedCalls();
    }

    @Override
    public Set<Call> getCalls() {
        return delegate.getCalls();
    }

    @Override
    public Call getDefaultCall() {
        return delegate.getDefaultCall();
    }

    @Override
    public String getSpliceIdentifier() {
        return delegate.getSpliceIdentifier();
    }

    @Override
    public SortedSet<Integer> getCallInitiationRows() {
        return delegate.getCallInitiationRows();
    }

    @Override
    public SortedSet<CallingPosition> getMethodBasedCallingPositions() {
        return delegate.getMethodBasedCallingPositions();
    }

    @Override
    public CallingPosition findMethodBasedCallingPositionByName(String callingPositionName) {
        return delegate.findMethodBasedCallingPositionByName(callingPositionName);
    }

    @Override
    public String getRawNotationDisplayString(int notationIndex, boolean concise) {
        return delegate.getRawNotationDisplayString(notationIndex, concise);
    }

    @Override
    public Iterator<PlaceSet> iterator() {
        return delegate.iterator();
    }
}
