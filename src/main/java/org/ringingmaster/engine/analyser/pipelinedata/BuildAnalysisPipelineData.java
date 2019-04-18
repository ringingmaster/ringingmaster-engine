package org.ringingmaster.engine.analyser.pipelinedata;

import org.ringingmaster.engine.compiler.compiledcomposition.CompiledComposition;

import java.util.function.Function;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
public class BuildAnalysisPipelineData implements Function<CompiledComposition, AnalysisPipelineData> {


    @Override
    public AnalysisPipelineData apply(CompiledComposition compiledComposition) {
        return new AnalysisPipelineData(compiledComposition);
    }
}
