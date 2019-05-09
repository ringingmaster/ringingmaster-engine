package org.ringingmaster.engine.method;

import com.google.common.collect.Iterators;
import org.ringingmaster.engine.NumberOfBells;

import javax.annotation.concurrent.Immutable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkElementIndex;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The default implementation of the Method interface.
 * 
 * @author Steve Lake
 */

@Immutable
class DefaultMethod implements Method {

	private final NumberOfBells numberOfBells;
	private final Lead[] leads;
	private final Row[] rows;

	DefaultMethod(final NumberOfBells numberOfBells, final List<Lead> leads) {
		this.numberOfBells =  checkNotNull(numberOfBells, "numberOfBells must not be null");
		checkNotNull(leads, "leads must not be null");
		this.leads = leads.toArray(new Lead[leads.size()]);

		int rowCount = calculateRowCount();
		this.rows = new Row[rowCount];
		for (Lead lead : this) {
			for (Row row : lead) {
				rows[row.getRowIndex()] = row;
			}
		}
	}

	private int calculateRowCount() {
		Lead lastLead = getLastLead();
		if (lastLead == null) {
			return 0;
		}
		return lastLead.getLastRow().getRowIndex() + 1;
	}

	@Override
	public NumberOfBells getNumberOfBells() {
		return numberOfBells;
	}

	@Override
	public int getLeadCount() {
		return leads.length;
	}

	@Override
	public int getNumberOfBellsInHunt() {
		if (leads.length > 0) {
			return getLead(0).getHuntBellStartPlace().size();
		}
		return 0;
	}

	@Override
	public Lead getLead(final int index) {
		checkElementIndex(index, leads.length);
		return leads[index];
	}

	private Lead getLastLead() {
		if (leads.length > 0) {
			return leads[leads.length -1];
		}
		return null;
	}

	@Override
	public int getRowCount() {
		return rows.length - 1;
	}

	@Override
	public Row getRow(int index) {
		checkElementIndex(index, rows.length);
		return rows[index];
	}

	@Override
	public Optional<Row> getFirstRow() {
		if (rows.length > 0) {
			return Optional.of(getRow(0));
		}
		return Optional.empty();
	}

	@Override
	public Optional<Row> getLastRow() {
		if (rows.length > 0) {
			return Optional.of(getRow(rows.length-1));
		}
		return Optional.empty();
	}

	@Override
	public boolean firstAndLastRowEqual() {
		if (rows.length == 0) {
			return false;
		}
		return rows[0].equals(rows[rows.length-1]);
	}

	@Override
	public String getAllChangesAsText() {
		StringBuilder buff = new StringBuilder();
		for (int rowIndex = 0;rowIndex<rows.length;rowIndex++) {
			buff.append(rows[rowIndex].getDisplayString(false));
			if (rowIndex < rows.length-1) {
				buff.append(System.lineSeparator());
			}
		}
		return buff.toString();
	}

	public Iterator<Row> rowIterator() {
		return Iterators.forArray(rows);
	}

	@Override
	public Row[] getRows(boolean includeLastRow) {
		int length = Math.max(0, (includeLastRow? rows.length: rows.length-1));
		return Arrays.copyOf(rows, length);
	}

	@Override
	public Iterator<Lead> iterator() {
		return Iterators.forArray(leads);
	}

	@Override
	public String toString() {
		return "DefaultMethod [numberOfBells="
				+ numberOfBells + ", leadCount=" + getLeadCount()
				+ ", rowCount=" + getRowCount() + "]";
	}
}