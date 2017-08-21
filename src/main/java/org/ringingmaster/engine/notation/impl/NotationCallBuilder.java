package org.ringingmaster.engine.notation.impl;

import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.notation.NotationCall;
import org.ringingmaster.engine.notation.NotationRow;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * TODO
 * User: Stephen
 */
public class NotationCallBuilder {

	private String name;
	private String nameShorthand;
	private String notationShorthand;

	public NotationCall build(NumberOfBells numberOfWorkingBells) {
		checkState(numberOfWorkingBells != null, "numberOfWorkingBells must not be null");
		checkState(name != null, "name must not be null");
		checkState(nameShorthand != null, "nameShorthand must not be null");
		checkState(notationShorthand != null, "notationShorthand must not be null");

		final List<NotationRow> notationElements = NotationBuilderHelper.getValidatedRowsFromShorthand(notationShorthand, numberOfWorkingBells);

		return new DefaultNotationCall(name,
										nameShorthand,
										numberOfWorkingBells,
										notationElements);
	}

	public NotationCallBuilder setName(final String name) {
		this.name = checkNotNull(name, "name must not be null");
		checkArgument(name.length() > 0, "name must not be empty");
		return this;
	}

	public NotationCallBuilder setNameShorthand(final String nameShorthand) {
		this.nameShorthand = checkNotNull(nameShorthand, "nameShorthand must not be null");
		checkArgument(nameShorthand.length() > 0, "name must not be empty");
		return this;
	}

	public NotationCallBuilder setUnfoldedNotationShorthand(final String notationShorthand) {
		this.notationShorthand = checkNotNull(notationShorthand, "notationShorthand must not be null");
		checkArgument(notationShorthand.length() > 0, "notationShorthand must not be empty");
		return this;
	}

	public void checkClashWith(NotationCallBuilder otherCallBuilder) {
		if (name.equals(otherCallBuilder.name)) {
			throw new IllegalArgumentException("Name of call [" + toDisplayString() + "] clashes with name of call [" + otherCallBuilder.toDisplayString() + "]");
		}
		if (name.equals(otherCallBuilder.nameShorthand)) {
			throw new IllegalArgumentException("Name of call [" + toDisplayString() + "] clashes with shorthand of call [" + otherCallBuilder.toDisplayString() + "]");
		}
		if (nameShorthand.equals(otherCallBuilder.name)) {
			throw new IllegalArgumentException("Shorthand of call [" + toDisplayString() + "] clashes with name of call [" + otherCallBuilder.toDisplayString() + "]");
		}
		if (nameShorthand.equals(otherCallBuilder.nameShorthand)) {
			throw new IllegalArgumentException("Shorthand of call [" + toDisplayString() + "] clashes with shorthand of call [" + otherCallBuilder.toDisplayString() + "]");
		}
		if (notationShorthand.equals(otherCallBuilder.notationShorthand)) {
			throw new IllegalArgumentException("Notation of call [" + toDisplayString() + "] clashes with Notation of call [" + otherCallBuilder.toDisplayString() + "]");
		}
	}

	public String toDisplayString() {
		return name + " (" + nameShorthand + ") :" + notationShorthand;
	}
		@Override
	public String toString() {
		return "NotationCallBuilder{" +
				"name='" + name + '\'' +
				", shorthand='" + nameShorthand + '\'' +
				", notationShorthand='" + notationShorthand + '\'' +
				'}';
	}
}