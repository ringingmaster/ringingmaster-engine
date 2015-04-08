package com.concurrentperformance.ringingmaster.engine.touch.container.impl;

import com.concurrentperformance.ringingmaster.engine.touch.container.Variance;
import net.jcip.annotations.Immutable;

/**
 * A Null object pattern that always includes everything.
 * User: Stephen
 */
@Immutable
public enum NullVariance implements Variance {
	INSTANCE,
	;

	static Variance getInstance() {
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
