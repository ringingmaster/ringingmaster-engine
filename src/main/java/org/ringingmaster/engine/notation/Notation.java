package org.ringingmaster.engine.notation;

import javax.annotation.concurrent.Immutable;
import java.util.Set;
import java.util.SortedSet;

/**
 * Contains the verified notation elements for the different types of
 * place notation. Use the Notation Builder to construct a new notation.
 * Can iterate over the rows.
 * <br/>
 *
 * @author Steve Lake
 */

@Immutable
public interface Notation extends PlaceSetSequence {

    /**
     * Is the notation has folded palindrome symmetry. When true, the place notation shorthand
     * will be used in reverse after being used forward.
     * i.e.
     * when true 'x.14' becomes 'x.14.14.x'
     * when false 'x.14' becomes 'x.14'
     * <p>
     * Defaults to false.
     *
     * @return boolean, true if folded palindrome symmetry
     */
    boolean isFoldedPalindrome();

    /**
     * Get the calculated lead head code
     */
    String getLeadHeadCode();

    /**
     * return true if the calls are generated.
     */
    boolean isCannedCalls();

    /**
     * Get all the the calls attached to this notation.
     */
    Set<Call> getCalls();

    /**
     * The call that is used when no call is specified.
     */
    Call getDefaultCall();

    /**
     * Get the splice identifier.
     */
    String getSpliceIdentifier();

    /**
     * Get the position that a call can be started on any lead
     */
    SortedSet<Integer> getCallInitiationRows();

    /**
     * Get the lead and position that a call can be started.
     */
    SortedSet<CallingPosition> getMethodBasedCallingPositions();

    /**
     * Find the {@link CallingPosition} for the passed name, otherwise null.
     */
    CallingPosition findMethodBasedCallingPositionByName(String callingPositionName);


    String getRawNotationDisplayString(int notationIndex, boolean concise);

}
