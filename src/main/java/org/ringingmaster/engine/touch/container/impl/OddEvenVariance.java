package org.ringingmaster.engine.touch.container.impl;

import net.jcip.annotations.Immutable;

import org.ringingmaster.engine.touch.container.Variance;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO comments???
 * User: Stephen
 */
@Immutable
public class OddEvenVariance implements Variance {

	public enum OddEvenVarianceType {
		ODD,
		EVEN;
	}

	private final OddEvenVarianceType oddEvenVarianceType;
	private final VarianceLogicType varianceLogicType;

	OddEvenVariance(VarianceLogicType varianceLogicType, OddEvenVarianceType oddEvenVarianceType) {
		this.oddEvenVarianceType = checkNotNull(oddEvenVarianceType, "oddEvenVarianceType must not be null");
		this.varianceLogicType = checkNotNull(varianceLogicType, "varianceLogicType must not be null");
	}

	@Override
	public boolean includePart(int part) {
		checkArgument(part >= 0, "part for checking must be 0 or greater [{}]", part);
		if (varianceLogicType == VarianceLogicType.INCLUDE) {
			return passesEvenOdd(part);
		}
		else {
			return !passesEvenOdd(part);
		}
	}

	private boolean passesEvenOdd(int part) {
		boolean evenPart = ((part % 2) == 0);
		return (evenPart && oddEvenVarianceType.equals(OddEvenVarianceType.EVEN)) ||
				(!evenPart && oddEvenVarianceType.equals(OddEvenVarianceType.ODD));
	}

	@Override
	public String toString() {
		return "{" +
				varianceLogicType +
				" " + oddEvenVarianceType + " parts" +
				'}';
	}
}
