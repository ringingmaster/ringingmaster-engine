package org.ringingmaster.engine.compilernew.proof;

import org.ringingmaster.engine.analysis.Analysis;
import org.ringingmaster.engine.compilernew.CompileTerminationReason;
import org.ringingmaster.engine.method.Method;
import org.ringingmaster.engine.parser.parse.Parse;

import javax.annotation.concurrent.Immutable;
import java.util.Optional;

/**
 * TODO comments.
 *
 * @author stephen
 */
@Immutable
public interface Proof {

	Parse getParse();

	CompileTerminationReason getTerminationReason();

	String getTerminateReasonDisplayString();

	Optional<Method> getMethod();

	Optional<Analysis> getAnalysis();

	long getProofTimeMs();
}
