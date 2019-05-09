package org.ringingmaster.engine.method;

import org.ringingmaster.engine.NumberOfBells;

import javax.annotation.concurrent.Immutable;
import java.util.Iterator;
import java.util.Optional;


/**
 * Representation of fully expanded method.
 *
 * @author Steve Lake
 */
@Immutable
public interface Method extends Iterable<Lead> {

	/**
	 * Get the number of bells for this method.
	 * 
	 * @return NumberOfBells
	 */
	NumberOfBells getNumberOfBells();

	/**
	 * Get the number of leads in this method.
	 * 
	 * @return int, leads in the method.
	 */
	int getLeadCount();

	/**
	 * Get the number of hunt bells in the first lead.
	 */
	int getNumberOfBellsInHunt();

	/**
	 * Get the lead at the passed index.
	 *
	 * @param index the index of the Lead that is required.
	 * @throws IndexOutOfBoundsException when less than 0 or greater than number of leads
	 */
	Lead getLead(int index);

	/**
	 * Get the number of rows in this method.
	 *
	 * @return int, rows in the method.
	 */
	int getRowCount();

	/**
	 * Get the row at the index.
	 *
	 * @throws IndexOutOfBoundsException when less than 0 or greater than number of rows
	 */
	Row getRow(int index);

	/**
	 * Convienence method to get the first row. Optional not populated when there are no rows.
	 */
	Optional<Row> getFirstRow();

	/**
	 * Convienence method to get the first row. Optional not populated when there are no rows.
	 */
	Optional<Row> getLastRow();

	/**
	 * true when both first and last rowe exist, and they have equalty
	 * @return
	 */
	boolean firstAndLastRowEqual();

	/**
	 * Return all changes as text, using the system separator between each row. i.e.
	 * 123456
	 * 214365
	 * 241635
	 */
 	String getAllChangesAsText();

	Iterator<Row> rowIterator();

	/**
	 * Get the underlying row array. Returns a copy.
	 * @param  includeLastRow The copy will exclude the final row if true
	 */
	Row[] getRows(boolean includeLastRow);

}
