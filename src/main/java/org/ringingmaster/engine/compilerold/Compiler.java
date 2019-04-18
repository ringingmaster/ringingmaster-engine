package org.ringingmaster.engine.compilerold;

import org.ringingmaster.engine.compiler.compiledtouch.CompiledTouch;

import javax.annotation.concurrent.ThreadSafe;
import java.util.function.Supplier;

/**
 * Compile Touch into a CompiledTouch
 * User: Stephen
 */
@ThreadSafe
public interface Compiler {

	CompiledTouch compile(boolean withAnalysis, Supplier<Boolean> shouldTerminateEarly);
}
