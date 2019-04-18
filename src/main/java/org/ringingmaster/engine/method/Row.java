package org.ringingmaster.engine.method;

import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.notation.NotationPlace;

import javax.annotation.concurrent.Immutable;


/**
 * Contains verified bells, representing a single row of a method.
 * If this method has covers, then these will be included in the row.
 * 
 * @author Stephen
 */
@Immutable
public interface Row extends Iterable<Bell>, Comparable<Row> {

	String ROUNDS_TOKEN = "Rounds";

	/**
	 * Get the number of bells in this row, including any covers.
	 * 
	 * @return NumberOfBells
	 */
	NumberOfBells getNumberOfBells();

	/**
	 * Get the bell at the specified zero based place.
	 * 
	 * @param place, the zero based index of the place to get the bell for.
	 * @return Bell
	 * @throws ArrayIndexOutOfBoundsException
	 */
	Bell getBellInPlace(int place); //TODO should this pass in an enum?

	/**
	 * Get the bell at the specified zero based place.
	 *
	 * @param place, the place to get the bell for.
	 * @return Bell
	 * @throws ArrayIndexOutOfBoundsException
	 */
	Bell getBellInPlace(NotationPlace place);

	/**
	 * For a given bell, indicate the place it is in.
	 * 
	 * @param bell bell to test the place for. not null
	 * @return Integer if valid, otherwise null.
	 */
	Integer getPlaceOfBell(Bell bell);

	/**
	 * Get the row index with respect to the whole method,
	 * with the first (usually rounds) being 0.
	 * @return int, the row number
	 */
	int getRowIndex();

	/**
	 * Get the stroke of the row. This alternates from row to row.
	 * @return
	 */
	Stroke getStroke();

	/**
	 * Set the Stroke. As this class is immutable, it returns a new instance
	 * @param stroke
	 */
	Row setStroke(Stroke stroke);

	/**
	 * Get the row course type.
	 * @return RowCourseType row course type.
	 */
	RowCourseType getRowCourseType();


	/**
	 * @return Return true if this row represents rounds for the specified number.
	 */
	boolean isRounds();

	/**
	 * Get the row as a display string. i.e. rounds on 12 would return '1234567890ET',
	 * unless the param useRoundsWord is set to true, then the word 'Rounds' will be returned.
	 * 
	 * @return String
	 */
	String getDisplayString(boolean useRoundsWord);

}
