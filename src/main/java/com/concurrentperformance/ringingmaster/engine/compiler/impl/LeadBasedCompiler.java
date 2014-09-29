package com.concurrentperformance.ringingmaster.engine.compiler.impl;

import com.google.common.collect.ImmutableList;
import net.jcip.annotations.ThreadSafe;

import java.util.List;

import com.concurrentperformance.ringingmaster.engine.compiler.Compiler;
import com.concurrentperformance.ringingmaster.engine.method.MethodRow;
import com.concurrentperformance.ringingmaster.engine.notation.NotationCall;
import com.concurrentperformance.ringingmaster.engine.parser.ParseType;
import com.concurrentperformance.ringingmaster.engine.touch.Touch;
import com.concurrentperformance.ringingmaster.engine.touch.TouchType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	public LeadBasedCompiler(Touch touch) {
		super(touch, "");
		checkArgument(touch.getTouchType() == TouchType.LEAD_BASED, "Lead based compiler must use a LEAD_BASED touch. Is actually [" + touch.getTouchType() + "]");
	}

	public LeadBasedCompiler(Touch touch, String logPreamble) {
		super(touch, logPreamble);
		checkArgument(touch.getTouchType() == TouchType.LEAD_BASED, "Lead based compiler must use a LEAD_BASED touch. Is actually [" + touch.getTouchType() + "]");
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
			log.info("{} Apply Plain lead", getLogPreamble());
		}
		else {
			maskedNotation.applyCall(call, getLogPreamble());
		}
		// We consumed the call
		return true;
	}


}
