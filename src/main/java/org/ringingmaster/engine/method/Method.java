package org.ringingmaster.engine.method;

import org.ringingmaster.engine.NumberOfBells;
import javax.annotation.concurrent.Immutable;

import java.util.Iterator;


/**
 * Representation of fully expanded method.
 *
 * @author Stephen Lake
 */
@Immutable
public interface Method extends Iterable<MethodLead> {

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
	 * @param index the index of the MethodLead that is required.
	 * @return MethodLead
	 * @throws ArrayIndexOutOfBoundsException
	 */
	MethodLead getLead(int index);

	/**
	 * Get the number of rows in this method.
	 *
	 * @return int, rows in the method.
	 */
	int getRowCount();

	MethodRow getRow(int index);

	MethodRow getFirstRow();
	MethodRow getLastRow();

	/**
	 * Return all changes as text, using the system separator between each row. i.e.
	 * 123456
	 * 214365
	 * 241635
	 *
	 * @return
	 */
 	String getAllChangesAsText();

	Iterator<MethodRow> rowIterator();

}
