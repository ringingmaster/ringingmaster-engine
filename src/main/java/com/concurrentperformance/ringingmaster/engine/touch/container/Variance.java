package com.concurrentperformance.ringingmaster.engine.touch.container;

import net.jcip.annotations.Immutable;

/**
 * TODO comments???
 * User: Stephen
 */
@Immutable
public interface Variance {

	/**
	 * Call to see if a specified part should be included.
	 */
	boolean includePart(int part);
}
