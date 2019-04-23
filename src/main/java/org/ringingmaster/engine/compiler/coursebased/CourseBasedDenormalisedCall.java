package org.ringingmaster.engine.compiler.coursebased;

import org.ringingmaster.engine.compiler.denormaliser.DenormalisedCall;
import org.ringingmaster.engine.compiler.variance.Variance;

import javax.annotation.concurrent.Immutable;

/**
 * TODO comments???
 * User: Stephen
 */
@Immutable
class CourseBasedDenormalisedCall extends DenormalisedCall {

    private final String callingPositionName;

    public CourseBasedDenormalisedCall(String callName, Variance variance, String callingPositionName) {
        super(callName, variance);
        this.callingPositionName = callingPositionName;
    }

    public String getCallingPositionName() {
        return callingPositionName;
    }

    @Override
    public String toString() {
        String varianceToString = getVariance().toString();
        return "{" + getCallName() + "@" + callingPositionName +
                ((varianceToString.length() > 0) ? (", " + varianceToString) : "") +
                '}';
    }
}
