package org.ringingmaster.engine.compiler.compiledcomposition;

import org.ringingmaster.engine.compiler.CompileTerminationReason;
import org.ringingmaster.engine.composition.Composition;
import org.ringingmaster.engine.method.Method;
import org.ringingmaster.engine.parser.parse.Parse;

import javax.annotation.concurrent.Immutable;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO comments???
 * User: Stephen
 */
@Immutable
class DefaultCompiledComposition implements CompiledComposition {

	private final Composition composition;
	private final Parse parse;
	private final CompileTerminationReason terminationReason;
	private final Optional<String> terminateNotes;
	private final Optional<Method> createdMethod;
	private final long compileTimeMs;


	DefaultCompiledComposition(Composition composition, Parse parse, CompileTerminationReason terminationReason, Optional<String> terminateNotes,
                               Optional<Method> createdMethod, long compileTimeMs) {
		this.composition = checkNotNull(composition);
		this.parse = checkNotNull(parse, "parse must not be null");
		this.terminationReason = checkNotNull(terminationReason, "terminationReason must not be null");
		this.terminateNotes = checkNotNull(terminateNotes, "terminateNotes must not be null");
		this.createdMethod = checkNotNull(createdMethod); // createdMethod can be absent when termination reason is INVALID_COMPOSITION
		this.compileTimeMs = compileTimeMs;
	}

	@Override
	public Composition getComposition() {
		return composition;
	}

	@Override
	public Parse getParse() {
		return parse;
	}

	@Override
	public CompileTerminationReason getTerminationReason() {
		return terminationReason;
	}

	@Override
	public String getTerminateReasonDisplayString() {
		switch(getTerminationReason()) {
//TODO sort out Optional handling as fluent functions
			case INVALID_COMPOSITION:
				return (terminateNotes.isPresent())?terminateNotes.get():"";
			case ROW_COUNT:
				return "Row limit (" + getMethod().get().getRowCount() + ")";
			case LEAD_COUNT:
				return "Lead limit (" + getMethod().get().getLeadCount() + ")";
			case SPECIFIED_ROW:
				getMethod().get().getLastRow()
						.map((input) -> "Change (" + input.getDisplayString(true) + ")")
						.orElse("Change");
			case EMPTY_PARTS:
				return  "Aborted - Empty parts found";
			// TODO this is from C++
//			case TR_PARTS:
//				str.Format("Part limit (%d)", method->getPartCount());
//				addLine("Termination:", str, RGB(255, 120, 255));
//				break;
//
//			case TR_CIRCLE:
//				addLine("Termination:", "Aborted - Circular composition", RGB(255, 120, 120));
//				break;

			default:
				throw new RuntimeException("Please code for termination reason [" + getTerminationReason() + "]");
		}
	}


	@Override
	public Optional<Method> getMethod() {
		return createdMethod;
	}

	@Override
	public long getCompileTimeMs() {
		return compileTimeMs;
	}
}
