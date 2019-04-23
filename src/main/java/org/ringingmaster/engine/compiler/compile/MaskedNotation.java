package org.ringingmaster.engine.compiler.compile;

import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.notation.Call;
import org.ringingmaster.engine.notation.CallingPosition;
import org.ringingmaster.engine.notation.Notation;
import org.ringingmaster.engine.notation.PlaceSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A construct that allows the iteration of an embedded current notation,
 * but also allows the application of a call that takes precedence
 * over the current notation.
 * NOTE: Only one iterator should be in use at any one time.
 * <p>
 * User: Steve Lake
 */
public class MaskedNotation implements Notation { //TODO completely lacking any testing

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private Notation currentNotation;
    private int iteratorIndex = 0;
    private Optional<Call> call = Optional.empty();
    private int callIndex;

    public MaskedNotation(Notation activeNotation) {
        setCurrentNotation(activeNotation);
    }

    public void setCurrentNotation(Notation currentNotation) {
        checkNotNull(currentNotation, "currentNotation must not be null");
        this.currentNotation = currentNotation;
    }

    public void applyCall(Call call, String logPreamble) {
        log.debug("{}   - Applying Call [{}] at index [{}] ", logPreamble, call, iteratorIndex);

        this.call = Optional.ofNullable(call);
        this.callIndex = 0;
    }

    public boolean isAtCallPoint() {
        return currentNotation.getCallInitiationRows().contains(iteratorIndex);
    }

    @Override
    public PlaceSet get(int index) {
        if (call.isPresent()) {
            if (callIndex < call.get().size()) {
                return call.get().get(callIndex++);
            } else {
                call = Optional.empty();
                callIndex = 0;
            }
        }
        return currentNotation.get(index);
    }

    @Override
    public int size() {
        return currentNotation.size();
    }

    @Override
    public SortedSet<Integer> getCallInitiationRows() {
        return currentNotation.getCallInitiationRows();
    }

    @Override
    public SortedSet<CallingPosition> getMethodBasedCallingPositions() {
        return currentNotation.getMethodBasedCallingPositions();
    }

    @Override
    public CallingPosition findMethodBasedCallingPositionByName(String callingPositionName) {
        return currentNotation.findMethodBasedCallingPositionByName(callingPositionName);
    }

    @Override
    public String getRawNotationDisplayString(int notationIndex, boolean concise) {
        return currentNotation.getRawNotationDisplayString(notationIndex, concise);
    }

    @Override
    public Iterator<PlaceSet> iterator() {

        iteratorIndex = 0;

        return new Iterator<PlaceSet>() {


            @Override
            public boolean hasNext() {
                return iteratorIndex < size();
            }

            @Override
            public PlaceSet next() {
                return get(iteratorIndex++);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("DefaultNotation.iterator() does not support remove()");
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
    public Set<Call> getCalls() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Call getDefaultCall() {
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
