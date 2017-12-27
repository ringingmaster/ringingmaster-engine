package org.ringingmaster.engine.analysis;

import java.util.List;

import org.ringingmaster.engine.method.MethodRow;

/**
 * TODO comments???
 * User: Stephen
 */
public interface Analysis {

	public boolean isTrueTouch();

	void setFalseRowGroups(List<List<MethodRow>> falseRowGroups);
}
