package org.ringingmaster.engine.compiler.variance;

import javax.annotation.concurrent.Immutable;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.ringingmaster.engine.compiler.variance.OddEvenVariance.OddEvenVarianceType.EVEN;
import static org.ringingmaster.engine.compiler.variance.OddEvenVariance.OddEvenVarianceType.ODD;
import static org.ringingmaster.engine.compiler.variance.VarianceLogicType.INCLUDE;

/**
 * TODO comments???
 * User: Stephen
 */
@Immutable
class OddEvenVariance implements Variance {

	enum OddEvenVarianceType {
		ODD,
		EVEN
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
		if (varianceLogicType == INCLUDE) {
			return passesEvenOdd(part);
		}
		else {
			return !passesEvenOdd(part);
		}
	}

	private boolean passesEvenOdd(int part) {
		boolean evenPart = ((part % 2) == 0);
		return (evenPart && oddEvenVarianceType.equals(EVEN)) ||
				(!evenPart && oddEvenVarianceType.equals(ODD));
	}

	@Override
	public String toString() {
		return "{" +
				varianceLogicType +
				" " + oddEvenVarianceType + " parts" +
				'}';
	}
}
