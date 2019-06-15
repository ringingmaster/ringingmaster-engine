package org.ringingmaster.engine.analyser.proof;

import com.google.common.collect.ImmutableList;
import org.ringingmaster.engine.compiler.compiledcomposition.CompiledComposition;
import org.ringingmaster.engine.composition.Composition;
import org.ringingmaster.engine.method.Row;
import org.ringingmaster.engine.parser.parse.Parse;

import javax.annotation.concurrent.Immutable;

import static com.google.common.base.Preconditions.checkState;

/**
 * TODO comments???
 * User: Stephen
 */
@Immutable
class DefaultProof implements Proof {


    private final CompiledComposition compiledComposition;
    private final ImmutableList<ImmutableList<Row>> falseRowGroups;
    private final long startMs;
    private final long endMs;


    DefaultProof(CompiledComposition compiledComposition, ImmutableList<ImmutableList<Row>> falseRowGroups,
                 long startMs, long endMs) {
        this.compiledComposition = compiledComposition;
        this.falseRowGroups = falseRowGroups;
        this.startMs = startMs;
        this.endMs = endMs;
    }

    @Override
    public Composition getComposition() {
        return compiledComposition.getComposition();
    }

    @Override
    public Parse getParse() {
        return compiledComposition.getParse();
    }

    @Override
    public CompiledComposition getCompiledComposition() {
        return compiledComposition;
    }

    @Override
    public ImmutableList<ImmutableList<Row>> getFalseRowGroups() {
        return falseRowGroups;
    }

    @Override
    public boolean isTrueComposition() {
        checkState(falseRowGroups != null, "falseRowGroups have not been set yet");
        boolean trueComposition = (falseRowGroups.size() == 0);
        return trueComposition;
    }

    @Override
    public long getStartMs() {
        return startMs;
    }

    @Override
    public long getEndMs() {
        return endMs;
    }

}
