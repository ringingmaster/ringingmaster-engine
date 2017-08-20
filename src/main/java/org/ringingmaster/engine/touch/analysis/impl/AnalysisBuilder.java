package org.ringingmaster.engine.touch.analysis.impl;

import org.ringingmaster.engine.touch.analysis.Analysis;
import org.ringingmaster.engine.method.Method;

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
