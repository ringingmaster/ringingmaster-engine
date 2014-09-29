package com.concurrentperformance.ringingmaster.engine.analysis.impl;

import com.concurrentperformance.ringingmaster.engine.analysis.Analysis;
import com.concurrentperformance.ringingmaster.engine.method.Method;

/**
 * TODO comments???
 * User: Stephen
 */
public class AnalysisBuilder {

	public static Analysis buildAnalysisStructure() {
		return new DefaultAnalysis();
	}

	public static void falseRowAnalysis(Method method, Analysis analysis) {
		new FalseRowAnalysis().analise(method, analysis);
	}
}
