package org.ringingmaster.engine.compilerold.impl;

/**
 * Takes a parsed composition, and converts it into a compiled composition. A proof consists of an expanded Method and termination details.
 * This is a 1 shot class. Throw it away when you have finished.
 *
 * @author stephen
 */
//@ThreadSafe
//public class CourseBasedCompiler extends SkeletalCompiler<CourseBasedDenormalisedCall> implements Compiler {
//
//	private final Logger log = LoggerFactory.getLogger(this.getClass());
//
//	private final Bell callFromBell;
//	private volatile List<CourseBasedDenormalisedCall> immutableCallSequence;
//	private volatile Map<CallingPosition, Integer> callingPositionToCallBellPlace;
//
//	CourseBasedCompiler(Composition composition, String logPreamble) {
//		super(composition, logPreamble);
//		checkArgument(composition.getCompositionType() == CompositionType.COURSE_BASED, "Course based compiler must use a COURSE_BASED composition. Is actually [" + composition.getCompositionType() + "]");
//		callFromBell = composition.getCallFromBell();
//	}
//
//	@Override
//	protected void preCompile(Composition composition) {
//		immutableCallSequence = buildImmutableCallSequence(composition);
//		callingPositionToCallBellPlace = buildCallingPositionLookup(composition);
//	}
//
//	private ImmutableList<CourseBasedDenormalisedCall> buildImmutableCallSequence(Composition composition) {
//		return ImmutableList.copyOf(new CourseBasedCallDecomposer(composition, getLogPreamble()).createCallSequence());
//	}
//
//	private Map<CallingPosition, Integer> buildCallingPositionLookup(Composition composition) {
//		log.info("{} > Build calling bell positions",getLogPreamble());
//		// Build a plain course.
//		Notation activeNotation = composition.getNonSplicedActiveNotation().get();
//		Method plainCourse = PlainCourseHelper.buildPlainCourse(activeNotation, getLogPreamble() +  "  | ",false).getMethod().get();
//
//		// build the map.
//		Map<CallingPosition, Integer> callingPositionMap = new HashMap<>();
//
//		for (CallingPosition methodCallingPosition: activeNotation.getMethodBasedCallingPositions()) {
//			Integer place = plainCourse
//					.getLead(methodCallingPosition.getLeadOfTenor())
//					get(methodCallingPosition.getCallInitiationRow())
//					.getPlaceOfBell(composition.getNumberOfBells().getTenor());
//			callingPositionMap.put(methodCallingPosition, place);
//		}
//
//		log.info(getLogPreamble() + " < Build calling bell positions"); //TODO log the calling bell positions
//		return callingPositionMap;
//	}
//
//	@Override
//	protected List<CourseBasedDenormalisedCall> getImmutableCallSequence() {
//		return immutableCallSequence;
//	}
//
//	@Override
//	protected boolean applyNextCall(MaskedNotation maskedNotation, Row currentRow,
//                                    CourseBasedDenormalisedCall nextCall, Call call) {
//
//		// Find the method calling position.
//		String callingPositionName = nextCall.getCallingPositionName();
//		CallingPosition methodCallingPosition = maskedNotation.findMethodBasedCallingPositionByName(callingPositionName);
//		checkState(methodCallingPosition != null, "Can't find calling position [" + callingPositionName + "] " +
//				"in notation [" + maskedNotation.getName() + "]");
//
//		// check we are at the correct call initiation row
//		if (methodCallingPosition.getCallInitiationRow() != maskedNotation.getIteratorIndex()) {
//			return false;
//		}
//
//		// Is our designated calling bell on the correct lead (of the tenor)
//		int positionOfCallingBell = currentRow.getPlaceOfBell(callFromBell);
//		Integer place = callingPositionToCallBellPlace.get(methodCallingPosition);
//		if (positionOfCallingBell != place) {
//			return false;
//		}
//
//		// make the call
//		maskedNotation.applyCall(call, getLogPreamble());
//		return true;
//	}
//}
