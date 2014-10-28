package com.concurrentperformance.ringingmaster.engine.method;

import net.jcip.annotations.Immutable;

import com.concurrentperformance.ringingmaster.engine.NumberOfBells;



/**
 * Contains verified bells, representing a single row of a method.
 * If this method has covers, then these will be included in the row.
 * 
 * @author Stephen
 */
@Immutable
public interface MethodRow extends Iterable<Bell>, Comparable<MethodRow> {

	public static final String ROUNDS_TOKEN = "Rounds";

	/**
	 * Get the number of bells in this row, including any covers.
	 * 
	 * @return NumberOfBells
	 */
	public NumberOfBells getNumberOfBells();

	/**
	 * Get the bell at the specified zero based place.
	 * 
	 * @param place, the zero based index of the place to get the bell for.
	 * @return Bell
	 * @throws ArrayIndexOutOfBoundsException
	 */
	public Bell getBellInPlace(int place); //TODO should this pass in an enum?

	/**
	 * For a given bell, indicate the place it is in.
	 * 
	 * @param bell bell to test the place for. not null
	 * @return Integer if valid, otherwise null.
	 */
	public Integer getPlaceOfBell(Bell bell);

	/**
	 * Get the row number with reference to the whole method,
	 * with the first (usually rounds) being 0.
	 * @return int, the row number
	 */
	public int getRowNumber();

	/**
	 * Get the stroke of the row. This alternates from row to row.
	 * @return
	 */
	public Stroke getStroke();

	/**
	 * Get the row course type.
	 * @return RowCourseType row course type.
	 */
	public RowCourseType getRowCourseType();


	/**
	 * @return Return true if this row represents rounds for the specified number.
	 */
	public boolean isRounds();

	/**
	 * Get the row as a display string. i.e. rounds on 12 would return '1234567890ET',
	 * unless the param useRoundsWord is set to true, then the word 'Rounds' will be returned.
	 * 
	 * @return String
	 */
	public String getDisplayString(boolean useRoundsWord);

}
