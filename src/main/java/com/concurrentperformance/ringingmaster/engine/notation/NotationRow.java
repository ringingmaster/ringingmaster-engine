package com.concurrentperformance.ringingmaster.engine.notation;

import net.jcip.annotations.Immutable;

/**
 * Immutable Holder for a Set of NotationElements that make up a single row.
 *
 * i.e. if the full notation was 'X.15', then this would represent either the
 * 'X' or the '15'
 * <br/>
 * 
 * @author Stephen
 *
 */
@Immutable
public interface NotationRow {

	/**
	 * Get the NotationPlace at the index position. This is a single place
	 * on the NotationRow.
	 * @param index the row number of the notation to get.
	 * @return NotationPlace, The element at the passed position
	 * @throws IndexOutOfBoundsException if the index is greater than the number of rows
	 */
	NotationPlace getElement(int index);

	/**
	 * Return true if the row contains a single ALL_CHANGE row.
	 * 
	 * @return boolean
	 */
	boolean isAllChange();

	/**
	 * Get the notation as a string. i.e. for PLACE_1, PLACE_12, returns '1T'
	 * @return String Concise String for row
	 */
	String toDisplayString();

	/**
	 * Check to see if the passed in notationPlace is made in this row.
	 * 
	 * @param notationPlace
	 * @return true, if the place is made.
	 */
	boolean makesPlace(NotationPlace notationPlace);
}
