package org.ringingmaster.engine.analyser.proof;

import com.google.common.collect.ImmutableList;
import org.ringingmaster.engine.compiler.compiledcomposition.CompiledComposition;
import org.ringingmaster.engine.composition.Composition;
import org.ringingmaster.engine.method.Row;
import org.ringingmaster.engine.parser.parse.Parse;

/**
 * TODO comments???
 * User: Stephen
 */
public interface Proof {

	Composition getComposition();

	Parse getParse();

	CompiledComposition getCompiledComposition();


	ImmutableList<ImmutableList<Row>> getFalseRowGroups();

	boolean isTrueComposition();
}
