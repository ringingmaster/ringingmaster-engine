package com.concurrentperformance.ringingmaster.engine.touch.compiler;

import net.jcip.annotations.ThreadSafe;

import com.concurrentperformance.ringingmaster.engine.touch.proof.Proof;

/**
 * Compile Touch into a Proof
 * User: Stephen
 */
@ThreadSafe
public interface Compiler {

	Proof compile(boolean withAnalysis);

	Proof getProof();
}
