package com.concurrentperformance.ringingmaster.engine.touch.compiler.impl;

import com.concurrentperformance.ringingmaster.engine.touch.compiler.Compiler;
import com.concurrentperformance.ringingmaster.engine.method.MethodRow;
import com.concurrentperformance.ringingmaster.engine.notation.NotationCall;
import com.concurrentperformance.ringingmaster.engine.touch.parser.ParseType;
import com.concurrentperformance.ringingmaster.engine.touch.container.Touch;
import com.concurrentperformance.ringingmaster.engine.touch.container.TouchType;
import com.google.common.collect.ImmutableList;
import net.jcip.annotations.ThreadSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Takes a parsed touch, and converts it into a compiled proof. A proof consists of an expanded Method and
 * associated analysis.
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
		checkArgument(touch.getTouchType() == TouchType.LEAD_BASED, "Lead based compiler must use a LEAD_BASED touch. Is actually [" + touch.getTouchType() + "]");
	}

	LeadBasedCompiler(Touch touch, String logPreamble) {
		super(touch, logPreamble);
		checkArgument(touch.getTouchType() == TouchType.LEAD_BASED, "Lead based compiler must use a LEAD_BASED touch. Is actually [" + touch.getTouchType() + "]");
	}

	@Override
	protected Optional<String> checkInvalidTouch(Touch touch) {
		if (touch.isSpliced()) {
			if (touch.getNotationsInUse().size() == 0) {
				return Optional.of("Spliced touch has no valid methods");
			}
		}
		else { // Not Spliced
			if (touch.getSingleMethodActiveNotation() == null) {
				return Optional.of("No active method");
			}
		}

		return Optional.empty();
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
	protected boolean applyNextCall(MaskedNotation maskedNotation, MethodRow currentMethodRow,
	                                LeadBasedDecomposedCall nextCallMeta, NotationCall call) {
		if (nextCallMeta.getParseType().equals(ParseType.PLAIN_LEAD)) {
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
