package org.ringingmaster.engine.touch.variance.impl;

import org.ringingmaster.engine.touch.variance.Variance;
import javax.annotation.concurrent.Immutable;

/**
 * A Null object pattern that always includes everything.
 * User: Stephen
 */
@Immutable
public enum NullVariance implements Variance {
	INSTANCE,
	;

	public static Variance getInstance() {
		return INSTANCE;
	}

	@Override
	public boolean includePart(int part) {
		return true;
	}


	@Override
	public String toString() {
		return "";
	}
}
