package org.ringingmaster.engine.analyser.proof;

import com.google.common.collect.ImmutableList;
import org.ringingmaster.engine.compiler.compiledtouch.CompiledTouch;
import org.ringingmaster.engine.method.Row;
import org.ringingmaster.engine.parser.parse.Parse;
import org.ringingmaster.engine.touch.Touch;

/**
 * TODO comments???
 * User: Stephen
 */
public interface Proof {

	Touch getTouch();

	Parse getParse();

	CompiledTouch getCompiledTouch();


	ImmutableList<ImmutableList<Row>> getFalseRowGroups();

	boolean isTrueTouch();
}
