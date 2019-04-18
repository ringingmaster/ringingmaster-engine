package org.ringingmaster.engine.analyser.pipelinedata;

import com.google.common.collect.ImmutableList;
import org.ringingmaster.engine.compiler.compiledcomposition.CompiledComposition;
import org.ringingmaster.engine.method.Row;

import javax.annotation.concurrent.Immutable;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
@Immutable
public class AnalysisPipelineData {


    private final CompiledComposition compiledComposition;
    private final ImmutableList<ImmutableList<Row>> falseRowGroups;

    AnalysisPipelineData(CompiledComposition compiledComposition) {
        this(compiledComposition, ImmutableList.of());
    }

    private AnalysisPipelineData(CompiledComposition compiledComposition, ImmutableList<ImmutableList<Row>> falseRowGroups) {
        this.compiledComposition = compiledComposition;
        this.falseRowGroups = falseRowGroups;
    }

    public CompiledComposition getCompiledComposition() {
        return compiledComposition;
    }

    public ImmutableList<ImmutableList<Row>> getFalseRowGroups() {
        return falseRowGroups;
    }

    public AnalysisPipelineData setFalseRowGroups(ImmutableList<ImmutableList<Row>> falseRowGroups) {
        return new AnalysisPipelineData(compiledComposition, falseRowGroups);
    }

}
