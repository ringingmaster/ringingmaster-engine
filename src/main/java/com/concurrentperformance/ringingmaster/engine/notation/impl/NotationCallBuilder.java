package com.concurrentperformance.ringingmaster.engine.notation.impl;

import com.concurrentperformance.ringingmaster.engine.NumberOfBells;
import com.concurrentperformance.ringingmaster.engine.notation.NotationCall;
import com.concurrentperformance.ringingmaster.engine.notation.NotationRow;

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
	private NumberOfBells numberOfWorkingBells = NumberOfBells.BELLS_8;
	private String notationShorthand;

	public NotationCall build() {
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

	public NotationCallBuilder setNumberOfWorkingBells(final NumberOfBells numberOfWorkingBells) {
		this.numberOfWorkingBells = checkNotNull(numberOfWorkingBells, "numberOfWorkingBells must not be null");
		return this;
	}

	public NotationCallBuilder setUnfoldedNotationShorthand(final String notationShorthand) {
		this.notationShorthand = checkNotNull(notationShorthand, "notationShorthand must not be null");
		checkArgument(notationShorthand.length() > 0, "notationShorthand must not be empty");
		return this;
	}

	public void checkClashWith(NotationCallBuilder otherCallBuilder) {
		if (name.equals(otherCallBuilder.name)||
				name.equals(otherCallBuilder.nameShorthand) ||
				nameShorthand.equals(otherCallBuilder.name) ||
				nameShorthand.equals(otherCallBuilder.nameShorthand)) {
			throw new IllegalArgumentException("Name clash between Calls [" + toDisplayString() + "] and [" + otherCallBuilder.toDisplayString() + "]");
		}
		if (notationShorthand.equals(otherCallBuilder.notationShorthand)) {
			throw new IllegalArgumentException("Notation clash between Calls [" + toDisplayString() + "] and [" + otherCallBuilder.toDisplayString() + "]");
		}
	}

	public String toDisplayString() {
		return name + "(" + nameShorthand + ") :" + notationShorthand;

	}
		@Override
	public String toString() {
		return "NotationCallBuilder{" +
				"name='" + name + '\'' +
				", shorthand='" + nameShorthand + '\'' +
				", numberOfWorkingBells=" + numberOfWorkingBells +
				", notationShorthand='" + notationShorthand + '\'' +
				'}';
	}
}
