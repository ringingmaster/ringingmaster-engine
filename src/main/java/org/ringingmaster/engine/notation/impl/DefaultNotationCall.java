package org.ringingmaster.engine.notation.impl;

import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.notation.NotationCall;
import org.ringingmaster.engine.notation.NotationRow;

import javax.annotation.concurrent.Immutable;
import java.util.List;

/**
 * Representation of a single call.
 * User: Stephen
 */
@Immutable
public class DefaultNotationCall extends DefaultNotation implements NotationCall {

	private final String nameShorthand;

	DefaultNotationCall(String name,
	                    String nameShorthand,
	                    NumberOfBells numberOfWorkingBells,
	                    List<NotationRow> normalisedNotationElements) {
		super(name, numberOfWorkingBells, normalisedNotationElements);
		this.nameShorthand = nameShorthand;
	}

	@Override
	public String getNameShorthand() {
		return nameShorthand;
	}

	@Override
	public String getNotationDisplayString(boolean concise) {
		return NotationBuilderHelper.getAsDisplayString(getNormalisedNotationElements(), concise);
	}

	@Override
	public String toDisplayString() {
		final StringBuilder buf = new StringBuilder();
		buf.append(getName());
		buf.append(" (").append(getNameShorthand()).append(") :");
		buf.append(getNotationDisplayString(false));
		return buf.toString();
	}

	@Override
	public String toString() {
		final StringBuilder buf = new StringBuilder();
		buf.append("[").append(getName());
		buf.append(", ").append(getNameShorthand());
		buf.append(", ").append(getNotationDisplayString(false)) ;
		buf.append("]");
		return buf.toString();
	}
}
