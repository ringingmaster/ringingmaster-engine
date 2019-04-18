package org.ringingmaster.engine.analyser.proof;

import com.google.common.collect.ImmutableList;
import org.ringingmaster.engine.compiler.compiledtouch.CompiledTouch;
import org.ringingmaster.engine.method.Row;
import org.ringingmaster.engine.parser.parse.Parse;
import org.ringingmaster.engine.touch.Touch;

import javax.annotation.concurrent.Immutable;

import static com.google.common.base.Preconditions.checkState;

/**
 * TODO comments???
 * User: Stephen
 */
@Immutable
class DefaultProof implements Proof {


	private final CompiledTouch compiledTouch;
	private final ImmutableList<ImmutableList<Row>> falseRowGroups;

	DefaultProof(CompiledTouch compiledTouch, ImmutableList<ImmutableList<Row>> falseRowGroups) {
		this.compiledTouch = compiledTouch;
		this.falseRowGroups = falseRowGroups;
	}

	@Override
	public Touch getTouch() {
		return compiledTouch.getTouch();
	}

	@Override
	public Parse getParse() {
		return compiledTouch.getParse();
	}

	@Override
	public CompiledTouch getCompiledTouch() {
		return compiledTouch;
	}

	@Override
	public ImmutableList<ImmutableList<Row>> getFalseRowGroups() {
		return falseRowGroups;
	}

	@Override
	public boolean isTrueTouch() {
		checkState(falseRowGroups != null, "falseRowGroups have not been set yet");
		boolean trueTouch = (falseRowGroups.size() == 0);
		return trueTouch;
	}
}
