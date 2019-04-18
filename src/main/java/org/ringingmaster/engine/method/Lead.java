package org.ringingmaster.engine.method;

import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.notation.NotationPlace;
import javax.annotation.concurrent.Immutable;

import java.util.List;
import java.util.Set;

/**
 * Representation of a lead within a method. Contains and allows iteration
 * of the Row's
 * Where a Method contains multiple Lead's, the last Row in
 * Lead n is the same object as the first row in Lead n+1
 * 
 * @author Stephen Lake
 */
@Immutable
public interface Lead extends Iterable<Row> {

	/**
	 * Gets the number of bells in this Lead
	 * 
	 * @return NumberOfBells
	 */
	NumberOfBells getNumberOfBells();

	/**
	 * Get a count of the number of rows in this lead. This
	 * @return
	 */
	int getRowCount();

	/**
	 * Get the Row at index.
	 * @param index
	 * @return Row
	 * @throws IndexOutOfBoundsException
	 */
	Row getRow(int index);

	/**
	 * Convenience method to get last method row.
	 * @return Row
	 */
	Row getLastRow();

	/**
	 * Convenience method to get first method row.
	 * @return Row
	 */
	Row getFirstRow();

	/**
	 * Get the sequence of places that the bell takes through this
	 * lead, in the form of a list of integer
	 * 
	 * @param bell, of interest, not null
	 * @return List<Integer> or null if not a valid bell
	 */
	List<Integer> getPlaceSequenceForBell(Bell bell);

	/**
	 * Get a list of lead positions that have lines drawn after them as lead separators.
	 * These are zero based, so a value of 0 means the first row should
	 * have a separator drawn after it.
	 * 
	 * @return int[]
	 */
	int[] getLeadSeparatorPositions();

	/**
	 * Get the start place for a specific bell. The algorithm is to find the position of the
	 * passed in bell, and return the place as a bell so the mnemonic can be retrieved.
	 * 
	 * @param bell
	 */
	NotationPlace getStartPlace(Bell bell);

	/**
	 * Return the places that are in the hunt.
	 * @return
	 */
	Set<NotationPlace> getHuntBellStartPlace();

	/**
	 * Return all changes as text, using the system separator between each row. i.e.
	 * 123456
	 * 214365
	 * 241635
	 *
	 * @return
	 */
	String getAllChangesAsText();
}
