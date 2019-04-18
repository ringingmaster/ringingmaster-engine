package org.ringingmaster.engine.analyser.proof;

import org.ringingmaster.engine.analyser.pipelinedata.AnalysisPipelineData;

import java.util.function.Function;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
public class BuildProof implements Function<AnalysisPipelineData, Proof> {

    @Override
    public Proof apply(AnalysisPipelineData input) {
        return new DefaultProof(input.getCompiledComposition(),
                input.getFalseRowGroups());
    }
}
