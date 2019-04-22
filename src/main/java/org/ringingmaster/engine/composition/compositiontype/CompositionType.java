package org.ringingmaster.engine.composition.compositiontype;

/**
 * TODO comments???
 * User: Stephen
 */
public enum CompositionType {

	/** Makes a call or plain at every calling position */ //TODO Should we expose this description text in UI?
	LEAD_BASED("Lead Based"),
	/** Uses calling positions. i.e. W B M H*/
	COURSE_BASED("Course Based"),
	;

	private final String name;

	CompositionType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
