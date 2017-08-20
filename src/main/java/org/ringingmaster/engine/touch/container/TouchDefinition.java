package org.ringingmaster.engine.touch.container;

import com.google.common.collect.ComparisonChain;

import java.util.Comparator;

/**
 * A definition is a cell that can be reused in the main body
 * of the touch by referring to its shorthand.
 * User: Stephen
 */
public interface TouchDefinition extends TouchCell {

	Comparator<TouchDefinition> BY_SHORTHAND = (o1, o2) -> ComparisonChain.start()
			.compare(o1.getShorthand(), o2.getShorthand())
			.result();

	TouchDefinition clone() throws CloneNotSupportedException;

	String getShorthand();
}
