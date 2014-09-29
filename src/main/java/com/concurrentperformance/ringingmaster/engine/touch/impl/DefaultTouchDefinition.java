package com.concurrentperformance.ringingmaster.engine.touch.impl;

import com.concurrentperformance.ringingmaster.engine.touch.TouchDefinition;

/**
 * TODO comments???
 * User: Stephen
 */
public class DefaultTouchDefinition extends DefaultTouchCell implements TouchDefinition {

	private final String name;

	public DefaultTouchDefinition(String name) {
		this.name = name;
	}

	@Override
	public TouchDefinition clone() throws CloneNotSupportedException {
		DefaultTouchDefinition definitionClone = new DefaultTouchDefinition(name);
		cloneElementsTo(definitionClone);
		return definitionClone;
	}


	@Override
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "TouchDefinition{name=" + name +   super.toString() + "}";
	}
}
