package com.concurrentperformance.ringingmaster.engine.touch.analysis;

import java.util.List;

import com.concurrentperformance.ringingmaster.engine.method.MethodRow;

/**
 * TODO comments???
 * User: Stephen
 */
public interface Analysis {

	public boolean isTrueTouch();

	void setFalseRowGroups(List<List<MethodRow>> falseRowGroups);
}
