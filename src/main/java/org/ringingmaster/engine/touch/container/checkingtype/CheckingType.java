package org.ringingmaster.engine.touch.container.checkingtype;

/**
 * TODO comments???
 * User: Stephen
 */
public enum CheckingType {

	/** Uses calling positions. i.e. W B M H*/
	COURSE_BASED("Course Based"),
	/** Makes a call or plain at every calling position */
	LEAD_BASED("Lead Based"),
	;

	private final String name;

	CheckingType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
