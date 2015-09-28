package com.concurrentperformance.ringingmaster.engine.touch.container;

/**
 * A definition is a cell that can be reused in the main body
 * of the touch by referring to its shorthand.
 * User: Stephen
 */
public interface TouchDefinition extends TouchCell {

	TouchDefinition clone() throws CloneNotSupportedException;

	String getShorthand();
}
