package org.ringingmaster.engine.compiler.variance;

import javax.annotation.concurrent.Immutable;

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
