package com.concurrentperformance.ringingmaster.engine.notation.impl;

import net.jcip.annotations.Immutable;

import com.concurrentperformance.ringingmaster.engine.notation.NotationMethodCallingPosition;

/**
 * TODO comments???
 * User: Stephen
 */
@Immutable
public class DefaultNotationMethodCallingPosition implements NotationMethodCallingPosition {

	private final int callInitiationRow;
	private final int leadOfTenor;
	private final String name;

	public DefaultNotationMethodCallingPosition(int callInitiationRow, int leadOfTenor, String name) {
		this.callInitiationRow = callInitiationRow;
		this.leadOfTenor = leadOfTenor;
		this.name = name;
	}

	@Override
	public int getCallInitiationRow() {
		return callInitiationRow;
	}

	@Override
	public int getLeadOfTenor() {
		return leadOfTenor;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int compareTo(NotationMethodCallingPosition other) {
		int result = Integer.compare(getLeadOfTenor(), other.getLeadOfTenor());
		if (result != 0) {
			return result;
		}
		result = Integer.compare(getCallInitiationRow(), other.getCallInitiationRow());
		if (result != 0) {
			return result;
		}

		return 0;
	}

	@Override
	public String toString() {
		return "{'" + name + '\'' +
				", lead " + leadOfTenor +
				"/row " + callInitiationRow +
				'}';
	}
}
