package org.ringingmaster.engine.compilerold;

import org.ringingmaster.engine.compiler.proof.Proof;

import javax.annotation.concurrent.ThreadSafe;
import java.util.function.Supplier;

/**
 * Compile Touch into a Proof
 * User: Stephen
 */
@ThreadSafe
public interface Compiler {

	Proof compile(boolean withAnalysis, Supplier<Boolean> shouldTerminateEarly);
}
