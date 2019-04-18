package org.ringingmaster.engine.analyser.pipelinedata;

import com.google.common.collect.ImmutableList;
import org.ringingmaster.engine.compiler.compiledtouch.CompiledTouch;
import org.ringingmaster.engine.method.Row;

import javax.annotation.concurrent.Immutable;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
@Immutable
public class AnalysisPipelineData {


    private final CompiledTouch compiledTouch;
    private final ImmutableList<ImmutableList<Row>> falseRowGroups;

    AnalysisPipelineData(CompiledTouch compiledTouch) {
        this(compiledTouch, ImmutableList.of());
    }

    private AnalysisPipelineData(CompiledTouch compiledTouch, ImmutableList<ImmutableList<Row>> falseRowGroups) {
        this.compiledTouch = compiledTouch;
        this.falseRowGroups = falseRowGroups;
    }

    public CompiledTouch getCompiledTouch() {
        return compiledTouch;
    }

    public ImmutableList<ImmutableList<Row>> getFalseRowGroups() {
        return falseRowGroups;
    }

    public AnalysisPipelineData setFalseRowGroups(ImmutableList<ImmutableList<Row>> falseRowGroups) {
        return new AnalysisPipelineData(compiledTouch, falseRowGroups);
    }

}
