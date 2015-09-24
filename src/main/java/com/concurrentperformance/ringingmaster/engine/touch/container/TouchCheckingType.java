package com.concurrentperformance.ringingmaster.engine.touch.container;

/**
 * TODO comments???
 * User: Stephen
 */
public enum TouchCheckingType {

	/** Uses calling positions. i.e. W B M H*/
	COURSE_BASED("Course Based"),
	/** Makes a call or plain at every calling position */
	LEAD_BASED("Lead Based"),
	;

	private final String name;

	TouchCheckingType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
