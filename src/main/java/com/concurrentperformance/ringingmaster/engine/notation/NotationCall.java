package com.concurrentperformance.ringingmaster.engine.notation;

/**
 * Contains the verified notation elements for the different types of
 * Call.
 *
 * User: Stephen
 */
public interface NotationCall extends Notation {

	/**
	 * Get the shorthand name of the call. i.e. '-' or 'S'
	 */
	String getNameShorthand();
}
