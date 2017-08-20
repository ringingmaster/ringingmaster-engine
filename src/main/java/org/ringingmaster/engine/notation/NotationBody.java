package org.ringingmaster.engine.notation;

import net.jcip.annotations.Immutable;

import java.util.Set;
import java.util.SortedSet;

/**
 * Contains the verified notation elements for the different types of
 * place notation. Use the NotationBody Builder to construct a new notation.
 * Can iterate over the rows.
 * <br/>
 *
 * @author Stephen Lake
 */

@Immutable
public interface NotationBody extends Notation {

	/**
	 * Get the name of the method, including the Number of bells
	 * name. i.e. Major in Plain Bob major.
	 */
	String getNameIncludingNumberOfBells();

	/**
	 * Is the notation has folded palindrome symmetry. When true, the place notation shorthand
	 * will be used in reverse after being used forward.
	 * i.e.
	 * when true 'x.14' becomes 'x.14.14.x'
	 * when false 'x.14' becomes 'x.14'
	 * 
	 * Defaults to false.
	 * 
	 * @return boolean, true if folded palindrome symmetry
	 */
	boolean isFoldedPalindrome();

	/**
	 * Get the calculated lead head code
	 * */
	String getLeadHeadCode();

	/**
	 * return true if the calls are generated.
	 */
	boolean isCannedCalls();

	/**
	 * Get all the the calls attached to this notation.
	 */
	Set<NotationCall> getCalls();

	/**
	 * The call that is used when no call is specified.
	 */
	NotationCall getDefaultCall();

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
	SortedSet<NotationMethodCallingPosition> getMethodBasedCallingPositions();

	/**
	 * Find the {@link NotationMethodCallingPosition} for the passed name, otherwise null.
	 */
	NotationMethodCallingPosition findMethodBasedCallingPositionByName(String callingPositionName);


	String getRawNotationDisplayString(int notationIndex, boolean concise);

}
