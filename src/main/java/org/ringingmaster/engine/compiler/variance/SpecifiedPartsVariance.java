package org.ringingmaster.engine.compiler.variance;

import com.google.common.collect.ImmutableSet;

import javax.annotation.concurrent.Immutable;
import java.util.Collection;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO comments???
 * User: Stephen
 */
@Immutable
class SpecifiedPartsVariance implements Variance {

    private final ImmutableSet<Integer> parts;
    private final VarianceLogicType varianceLogicType;

    SpecifiedPartsVariance(VarianceLogicType varianceLogicType, Collection<Integer> parts) {
        this.parts = ImmutableSet.copyOf(checkNotNull(parts, "parts must not be null"));

        for (int part : parts) {
            checkArgument(part >= 0, "All included parts must be 0 or greater [{}]", parts);
        }
        this.varianceLogicType = checkNotNull(varianceLogicType, "varianceLogicType must not be null");
    }

    @Override
    public boolean includePart(int part) {
        checkArgument(part >= 0, "part for checking must be 0 or greater [{}]", part);
        if (varianceLogicType == VarianceLogicType.INCLUDE) {
            return parts.contains(part);
        } else {
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
