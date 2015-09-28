package com.concurrentperformance.ringingmaster.engine.touch.container.impl;

import com.concurrentperformance.ringingmaster.engine.touch.container.TouchDefinition;

/**
 * TODO comments???
 * User: Stephen
 */
public class DefaultTouchDefinition extends DefaultTouchCell implements TouchDefinition {

	private final String shorthand;

	public DefaultTouchDefinition(String shorthand) {
		this.shorthand = shorthand;
	}

	@Override
	public TouchDefinition clone() throws CloneNotSupportedException {
		DefaultTouchDefinition definitionClone = new DefaultTouchDefinition(shorthand);
		cloneElementsTo(definitionClone);
		return definitionClone;
	}


	@Override
	public String getShorthand() {
		return shorthand;
	}

	@Override
	public String toString() {
		return "TouchDefinition{shorthand=" + shorthand +   super.toString() + "}";
	}
}
