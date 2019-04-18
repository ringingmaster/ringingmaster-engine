package org.ringingmaster.engine.compilerold;

import org.ringingmaster.engine.compiler.compiledcomposition.CompiledComposition;

import javax.annotation.concurrent.ThreadSafe;
import java.util.function.Supplier;

/**
 * Compile Composition into a CompiledComposition
 * User: Stephen
 */
@ThreadSafe
public interface Compiler {

	CompiledComposition compile(boolean withAnalysis, Supplier<Boolean> shouldTerminateEarly);
}
