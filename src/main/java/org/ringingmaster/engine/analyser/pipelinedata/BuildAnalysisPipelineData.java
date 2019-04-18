package org.ringingmaster.engine.analyser.pipelinedata;

import org.ringingmaster.engine.compiler.compiledtouch.CompiledTouch;

import java.util.function.Function;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
public class BuildAnalysisPipelineData implements Function<CompiledTouch, AnalysisPipelineData> {


    @Override
    public AnalysisPipelineData apply(CompiledTouch compiledTouch) {
        return new AnalysisPipelineData(compiledTouch);
    }
}
