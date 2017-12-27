package org.ringingmaster.engine.touch.variance.impl;

import org.ringingmaster.engine.touch.variance.Variance;
import javax.annotation.concurrent.Immutable;
import org.ringingmaster.engine.touch.variance.VarianceLogicType;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO comments???
 * User: Stephen
 */
@Immutable
public class SpecifiedPartsVariance implements Variance {

	private final Set<Integer> parts = new TreeSet<>();
	private final VarianceLogicType varianceLogicType;

	public SpecifiedPartsVariance(VarianceLogicType varianceLogicType, Collection<Integer> parts) {
		this.parts.addAll(checkNotNull(parts, "parts must not be null"));
		for (int part : parts) {
			checkArgument(part >= 0, "All included parts must be 0 or greater [{}]", parts);
		}
		this.varianceLogicType= checkNotNull(varianceLogicType, "varianceLogicType must not be null");
	}

	@Override
	public boolean includePart(int part) {
		checkArgument(part >= 0, "part for checking must be 0 or greater [{}]", part);
		if (varianceLogicType == VarianceLogicType.INCLUDE) {
			return parts.contains(part);
		}
		else {
			return !parts.contains(part);
		}
	}

	@Override
	public String toString() {
		return "{" +
				varianceLogicType +
				" parts " + parts +
				'}';
	}
}
