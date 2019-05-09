package org.ringingmaster.engine.compiler.compiledcomposition;

import org.ringingmaster.engine.compiler.CompileTerminationReason;
import org.ringingmaster.engine.method.Method;
import org.ringingmaster.engine.parser.parse.Parse;
import org.ringingmaster.engine.composition.Composition;

import javax.annotation.concurrent.Immutable;
import java.util.Optional;

/**
 * TODO comments.
 *
 * @author Steve Lake
 */
@Immutable
public interface CompiledComposition {

	Composition getComposition();

	Parse getParse();


	CompileTerminationReason getTerminationReason();

	String getTerminateReasonDisplayString();

	Optional<Method> getMethod();

	long getCompileTimeMs();
}
