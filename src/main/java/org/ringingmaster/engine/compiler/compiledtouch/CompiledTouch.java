package org.ringingmaster.engine.compiler.compiledtouch;

import org.ringingmaster.engine.compiler.CompileTerminationReason;
import org.ringingmaster.engine.method.Method;
import org.ringingmaster.engine.parser.parse.Parse;
import org.ringingmaster.engine.touch.Touch;

import javax.annotation.concurrent.Immutable;
import java.util.Optional;

/**
 * TODO comments.
 *
 * @author stephen
 */
@Immutable
public interface CompiledTouch {

	Touch getTouch();

	Parse getParse();


	CompileTerminationReason getTerminationReason();

	String getTerminateReasonDisplayString();

	Optional<Method> getMethod();

	long getCompileTimeMs();
}
