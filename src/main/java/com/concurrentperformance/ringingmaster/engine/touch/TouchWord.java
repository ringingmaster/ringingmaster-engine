package com.concurrentperformance.ringingmaster.engine.touch;

import java.util.List;

import com.concurrentperformance.ringingmaster.engine.parser.ParseType;

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
