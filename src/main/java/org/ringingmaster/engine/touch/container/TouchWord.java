package org.ringingmaster.engine.touch.container;

import java.util.List;

import org.ringingmaster.engine.parser.ParseType;

/**
 * TODO comments???
 * User: Stephen
 */
public interface TouchWord {

	void setInvalid(String toolTipText);

	boolean isValid();

	String getToolTip();

	List<TouchElement> getElements();

	String getElementsAsString();

	ParseType getFirstParseType();
}
