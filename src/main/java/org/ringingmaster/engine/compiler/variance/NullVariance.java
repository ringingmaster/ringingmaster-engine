package org.ringingmaster.engine.compiler.variance;

import javax.annotation.concurrent.Immutable;

/**
 * A Null object pattern that always includes everything.
 * User: Stephen
 */
@Immutable
enum NullVariance implements Variance {
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
