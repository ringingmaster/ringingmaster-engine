package com.concurrentperformance.ringingmaster.engine.method;

/**
 * TODO comments???
 * User: Stephen
 */
public enum Stroke {
	HANDSTROKE,
	BACKSTROKE,
	;

	public static Stroke flipStroke(Stroke stroke) {
		return (stroke == HANDSTROKE)?BACKSTROKE:HANDSTROKE;
	}
}
