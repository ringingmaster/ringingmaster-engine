package com.concurrentperformance.ringingmaster.engine.analysis.impl;

import java.util.List;

import com.concurrentperformance.ringingmaster.engine.analysis.Analysis;
import com.concurrentperformance.ringingmaster.engine.method.MethodRow;

import static com.google.common.base.Preconditions.checkState;

/**
 * TODO comments???
 * User: Stephen
 */
public class DefaultAnalysis implements Analysis {

	private List<List<MethodRow>> falseRowGroups;

	@Override
	public void setFalseRowGroups(List<List<MethodRow>> falseRowGroups) {
		this.falseRowGroups = falseRowGroups;
	}

	@Override
	public boolean isTrueTouch() {
		checkState(falseRowGroups != null, "falseRowGroups have not been set yet");
		boolean trueTouch = (falseRowGroups.size() == 0);
		return trueTouch;
	}
}
