package org.ringingmaster.engine.touch.variance;

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
