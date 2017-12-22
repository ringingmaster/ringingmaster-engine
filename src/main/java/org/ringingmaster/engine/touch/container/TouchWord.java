package org.ringingmaster.engine.touch.container;

import org.ringingmaster.engine.parser.ParseType;

import java.util.List;

/**
 * TODO comments???
 * User: Stephen
 */
@Deprecated
public interface TouchWord {

	void setInvalid(String toolTipText);

	boolean isValid();

	String getToolTip();

	List<TouchElement> getElements();

	String getElementsAsString();

	ParseType getFirstParseType();
}
