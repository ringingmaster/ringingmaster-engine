package org.ringingmaster.engine.notation;

import org.ringingmaster.engine.NumberOfBells;
import com.google.common.collect.ComparisonChain;

import javax.annotation.concurrent.Immutable;
import java.util.Comparator;

/**
 * Base interface for all notations, a Notation being the shorthand for a method.
 *
 * User: Stephen
 */
@Immutable
public interface Notation extends Iterable<NotationRow> {

	String ROW_SEPARATOR = ".";

	Comparator<Notation> BY_NAME = (o1, o2) -> ComparisonChain.start()
			.compare(o1.getName(), o2.getName())
			.result();

	Comparator<Notation> BY_NUMBER_THEN_NAME = (o1, o2) -> ComparisonChain.start()
			.compare(o1.getNumberOfWorkingBells(),o2.getNumberOfWorkingBells())
			.compare(o1.getName(), o2.getName())
			.result();

	/**
	 * Get the name of the method, excluding the Number of bells
	 * name. i.e. Plain Bob.
	 */
	String getName();

	/**
	 * Get the number of bells the method is working over, excluding any
	 * covering bells.
	 *
	 * @return int, Number of bells
	 */
	NumberOfBells getNumberOfWorkingBells();

	/**
	 * Get the number of rows in this notations plain course. Where the
	 * notation is folded palindrome symmetry, this returns the total unfolded number of rows.
	 *
	 * 'X.14.X' will return 3
	 * 'X.14.X.14 le:14 will return 8
	 *
	 * @return int, number of rows in the notation
	 */
	int getRowCount();

	/**
	 * Get the NotationRow at the index position. This is a rows worth
	 * of NotationPlace's.
	 * @param index the row number of the notation to get.
	 * @return NotationRow, all the elements for a row
	 * @throws IndexOutOfBoundsException if the index is greater than the number of rows
	 */
	NotationRow getRow(int index);

	/**
	 * Get the notation as a normalized string. By normalized, we mean that
	 * all the places, and all change are capitalized, and the separator dots are
	 * applied consistently. When the boolean 'concise' is set to true, it
	 * will produce a consise version of the string
	 * i.e.
	 * concise =  -12-1T.12,12
	 * not concise =  -.12.-.1T.12,12
	 * @param concise
	 * @return
	 */
	String getNotationDisplayString(boolean concise);


}
