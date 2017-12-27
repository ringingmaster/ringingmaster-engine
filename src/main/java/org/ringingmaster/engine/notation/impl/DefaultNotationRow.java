package org.ringingmaster.engine.notation.impl;

import com.google.common.collect.ImmutableList;
import org.ringingmaster.engine.notation.NotationPlace;
import org.ringingmaster.engine.notation.NotationRow;

import javax.annotation.concurrent.Immutable;
import java.util.Set;

/**
 * Default implementation of the NotationRow interface. Only to be constructed from
 * this package, using the NotationBuilder.
 * 
 * @author Stephen Lake
 */
@Immutable
public class DefaultNotationRow implements NotationRow {

	private final ImmutableList<NotationPlace> sortedElements;

	DefaultNotationRow(final Set<NotationPlace> elements) {
		sortedElements = ImmutableList.sortedCopyOf(elements);
	}

	@Override
	public int getElementCount() {
		return sortedElements.size();
	}

	@Override
	public NotationPlace getElement(final int index) {
		return sortedElements.get(index);
	}

	@Override
	public boolean makesPlace(NotationPlace notationPlace) {
		return sortedElements.contains(notationPlace);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((sortedElements == null) ? 0 : sortedElements.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final DefaultNotationRow other = (DefaultNotationRow) obj;
		if (sortedElements == null) {
			if (other.sortedElements != null) {
				return false;
			}
		} else if (!sortedElements.equals(other.sortedElements)) {
			return false;
		}
		return true;
	}

	@Override
	public boolean isAllChange() {
		final boolean allChange = (sortedElements.size() == 1) &&
				sortedElements.get(0).equals(NotationPlace.ALL_CHANGE);
		return allChange;
	}

	@Override
	public String toDisplayString() {
		final StringBuffer buf = new StringBuffer();
		for (final NotationPlace element : sortedElements) {
			buf.append(element.toDisplayString());
		}

		return buf.toString();
	}

	@Override
	public String toString() {
		return "DefaultNotationRow [" + toDisplayString() + "]";
	}
}