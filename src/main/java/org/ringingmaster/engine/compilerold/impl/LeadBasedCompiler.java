package org.ringingmaster.engine.compilerold.impl;

import com.google.common.collect.ImmutableList;
import org.ringingmaster.engine.compiler.leadbased.LeadBasedDenormalisedCall;
import org.ringingmaster.engine.compilerold.Compiler;
import org.ringingmaster.engine.composition.Composition;
import org.ringingmaster.engine.composition.checkingtype.CheckingType;
import org.ringingmaster.engine.method.Row;
import org.ringingmaster.engine.notation.NotationCall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.concurrent.ThreadSafe;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Takes a parsed composition, and converts it into a compiled proof. A proof consists of an expanded Method and
 * associated proof.
 * This is a 1 shot class. Throw it away when you have finished.
 *
 * @author stephen
 */
@ThreadSafe
public class LeadBasedCompiler extends SkeletalCompiler<LeadBasedDenormalisedCall> implements Compiler {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private volatile List<LeadBasedDenormalisedCall> immutableCallSequence;


	LeadBasedCompiler(Composition composition, String logPreamble) {
		super(composition, logPreamble);
		checkArgument(composition.getCheckingType() == CheckingType.LEAD_BASED, "Lead based compiler must use a LEAD_BASED composition. Is actually [" + composition.getCheckingType() + "]");
	}

	@Override
	protected void preCompile(Composition composition) {
		immutableCallSequence = ImmutableList.copyOf(new LeadBasedCallDecomposer(composition, getLogPreamble()).createCallSequence());
	}

	@Override
	protected List<LeadBasedDenormalisedCall> getImmutableCallSequence() {
		return immutableCallSequence;
	}

	@Override
	protected boolean applyNextCall(MaskedNotation maskedNotation, Row currentRow,
                                    LeadBasedDenormalisedCall nextCallMeta, NotationCall call) {
		if (nextCallMeta.isPlainLead()) {
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
