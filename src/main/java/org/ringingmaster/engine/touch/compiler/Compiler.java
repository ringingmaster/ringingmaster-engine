package org.ringingmaster.engine.touch.compiler;

import org.ringingmaster.engine.touch.proof.Proof;
import net.jcip.annotations.ThreadSafe;

import java.util.function.Supplier;

/**
 * Compile Touch into a Proof
 * User: Stephen
 */
@ThreadSafe
public interface Compiler {

	Proof compile(boolean withAnalysis, Supplier<Boolean> shouldTerminateEarly);
}