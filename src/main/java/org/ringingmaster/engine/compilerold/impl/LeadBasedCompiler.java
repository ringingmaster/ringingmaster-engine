package org.ringingmaster.engine.compilerold.impl;

import com.google.common.collect.ImmutableList;
import org.ringingmaster.engine.compilerold.Compiler;
import org.ringingmaster.engine.method.Row;
import org.ringingmaster.engine.notation.NotationCall;
import org.ringingmaster.engine.touch.Touch;
import org.ringingmaster.engine.touch.checkingtype.CheckingType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.concurrent.ThreadSafe;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.PLAIN_LEAD;

/**
 * Takes a parsed touch, and converts it into a compiled proof. A proof consists of an expanded Method and
 * associated proof.
 * This is a 1 shot class. Throw it away when you have finished.
 *
 * @author stephen
 */
@ThreadSafe
public class LeadBasedCompiler extends SkeletalCompiler<LeadBasedDecomposedCall> implements Compiler {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private volatile List<LeadBasedDecomposedCall> immutableCallSequence;

	LeadBasedCompiler(Touch touch) {
		super(touch, "");
		checkArgument(touch.getCheckingType() == CheckingType.LEAD_BASED, "Lead based compiler must use a LEAD_BASED touch. Is actually [" + touch.getCheckingType() + "]");
	}

	LeadBasedCompiler(Touch touch, String logPreamble) {
		super(touch, logPreamble);
		checkArgument(touch.getCheckingType() == CheckingType.LEAD_BASED, "Lead based compiler must use a LEAD_BASED touch. Is actually [" + touch.getCheckingType() + "]");
	}

	@Override
	protected void preCompile(Touch touch) {
		immutableCallSequence = ImmutableList.copyOf(new LeadBasedCallDecomposer(touch, getLogPreamble()).createCallSequence());
	}

	@Override
	protected List<LeadBasedDecomposedCall> getImmutableCallSequence() {
		return immutableCallSequence;
	}

	@Override
	protected boolean applyNextCall(MaskedNotation maskedNotation, Row currentRow,
	                                LeadBasedDecomposedCall nextCallMeta, NotationCall call) {
		if (nextCallMeta.getParseType().equals(PLAIN_LEAD)) {
			// No Call, but consume the call.
			log.debug("{} Apply Plain lead", getLogPreamble());
		}
		else {
			maskedNotation.applyCall(call, getLogPreamble());
		}
		// We consumed the call
		return true;
	}


}
