package org.ringingmaster.engine.compilerold.impl;

/**
 * Takes a parsed composition, and converts it into a compiled proof. A proof consists of an expanded Method and
 * associated proof.
 * This is a 1 shot class. Throw it away when you have finished.
 *
 * @author stephen
 */
//@ThreadSafe
//public class LeadBasedCompiler extends SkeletalCompiler<LeadBasedDenormalisedCall> implements Compiler {
//
//	private final Logger log = LoggerFactory.getLogger(this.getClass());
//
//	private volatile List<LeadBasedDenormalisedCall> immutableCallSequence;
//
//
//	LeadBasedCompiler(Composition composition, String logPreamble) {
//		super(composition, logPreamble);
//		checkArgument(composition.getCompositionType() == CompositionType.LEAD_BASED, "Lead based compiler must use a LEAD_BASED composition. Is actually [" + composition.getCompositionType() + "]");
//	}
//
//	@Override
//	protected void preCompile(Composition composition) {
//		immutableCallSequence = ImmutableList.copyOf(new LeadBasedCallDecomposer(composition, getLogPreamble()).createCallSequence());
//	}
//
//	@Override
//	protected List<LeadBasedDenormalisedCall> getImmutableCallSequence() {
//		return immutableCallSequence;
//	}
//
//	@Override
//	protected boolean applyNextCall(MaskedNotation maskedNotation, Row currentRow,
//                                    LeadBasedDenormalisedCall nextCallMeta, NotationCall call) {
//		if (nextCallMeta.isPlainLead()) {
//			// No Call, but consume the call.
//			log.debug("{} Apply Plain lead", getLogPreamble());
//		}
//		else {
//			maskedNotation.applyCall(call, getLogPreamble());
//		}
//		// We consumed the call
//		return true;
//	}
//
//
//}
