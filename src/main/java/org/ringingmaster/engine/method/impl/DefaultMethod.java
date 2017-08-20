package org.ringingmaster.engine.method.impl;

import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.method.Method;
import org.ringingmaster.engine.method.MethodLead;
import org.ringingmaster.engine.method.MethodRow;
import com.google.common.collect.Iterators;
import net.jcip.annotations.Immutable;

import java.util.Iterator;
import java.util.List;

import static com.google.common.base.Preconditions.checkElementIndex;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The default implementation of the Method interface.
 * 
 * @author Stephen Lake
 */

@Immutable
public class DefaultMethod implements Method {

	private final NumberOfBells numberOfBells;
	private final MethodLead[] leads;
	private final MethodRow[] rows;

	DefaultMethod(final NumberOfBells numberOfBells, final List<MethodLead> leads) {
		this.numberOfBells =  checkNotNull(numberOfBells, "numberOfBells must not be null");
		checkNotNull(leads, "leads must not be null");
		this.leads = leads.toArray(new MethodLead[leads.size()]);

		int rowCount = calculateRowCount();
		this.rows = new MethodRow[rowCount];
		for (MethodLead lead : this) {
			for (MethodRow row : lead) {
				rows[row.getRowNumber()] = row;
			}
		}
	}

	private int calculateRowCount() {
		MethodLead lastLead = getLastLead();
		if (lastLead == null) {
			return 0;
		}
		return lastLead.getLastRow().getRowNumber() + 1;
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
	public MethodLead getLead(final int index) {
		checkElementIndex(index, leads.length);
		return leads[index];
	}

	private MethodLead getLastLead() {
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
	public MethodRow getRow(int index) {
		checkElementIndex(index, rows.length);
		return rows[index];
	}

	@Override
	public MethodRow getFirstRow() {
		if (rows.length > 0) {
			return getRow(0);
		}
		return null;
	}

	@Override
	public MethodRow getLastRow() {
		if (rows.length > 0) {
			return getRow(rows.length-1);
		}
		return null;
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

	public Iterator<MethodRow> rowIterator() {
		return Iterators.forArray(rows);
	}

	@Override
	public Iterator<MethodLead> iterator() {
		return new Iterator<MethodLead>() { //TODO test coverage

			private int leadIndex = 0;

			@Override
			public boolean hasNext() {
				return leadIndex < getLeadCount();
			}

			@Override
			public MethodLead next() {
				return getLead(leadIndex++);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException("DefaultMethod.iterator() does not support remove()");
			}
		};
	}

	@Override
	public String toString() {
		return "DefaultMethod [numberOfBells="
				+ numberOfBells + ", leadCount=" + getLeadCount()
				+ ", rowCount=" + getRowCount() + "]";
	}
}