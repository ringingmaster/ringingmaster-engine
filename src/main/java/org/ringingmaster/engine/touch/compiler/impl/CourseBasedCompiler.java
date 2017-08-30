	package org.ringingmaster.engine.touch.compiler.impl;

	import org.ringingmaster.engine.helper.PlainCourseHelper;
import org.ringingmaster.engine.method.Bell;
import org.ringingmaster.engine.method.Method;
import org.ringingmaster.engine.method.MethodRow;
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.notation.NotationCall;
import org.ringingmaster.engine.notation.NotationMethodCallingPosition;
import org.ringingmaster.engine.touch.compiler.Compiler;
import org.ringingmaster.engine.touch.container.Touch;
import org.ringingmaster.engine.touch.newcontainer.checkingtype.CheckingType;
import com.google.common.collect.ImmutableList;
import net.jcip.annotations.ThreadSafe;
    import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

/**
 * Takes a parsed touch, and converts it into a compiled proof. A proof consists of an expanded Method and
 * associated analysis.
 * This is a 1 shot class. Throw it away when you have finished.
 *
 * @author stephen
 */
@ThreadSafe
public class CourseBasedCompiler extends SkeletalCompiler<CourseBasedDecomposedCall> implements Compiler {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private final Bell callFromBell;
	private volatile List<CourseBasedDecomposedCall> immutableCallSequence;
	private volatile Map<NotationMethodCallingPosition, Integer> callingPositionToCallBellPlace;

	CourseBasedCompiler(Touch touch, String logPreamble) {
		super(touch, logPreamble);
		checkArgument(touch.getCheckingType() == CheckingType.COURSE_BASED, "Course based compiler must use a COURSE_BASED touch. Is actually [" + touch.getCheckingType() + "]");
		callFromBell = touch.getCallFromBell();
	}

	@Override
	protected void preCompile(Touch touch) {
		immutableCallSequence = buildImmutableCallSequence(touch);
		callingPositionToCallBellPlace = buildCallingPositionLookup(touch);
	}

	private ImmutableList<CourseBasedDecomposedCall> buildImmutableCallSequence(Touch touch) {
		return ImmutableList.copyOf(new CourseBasedCallDecomposer(touch, getLogPreamble()).createCallSequence());
	}

	private Map<NotationMethodCallingPosition, Integer> buildCallingPositionLookup(Touch touch) {
		log.info("{} > Build calling bell positions",getLogPreamble());
		// Build a plain course.
		NotationBody activeNotation = touch.getNonSplicedActiveNotation();
		Method plainCourse = PlainCourseHelper.buildPlainCourse(activeNotation, getLogPreamble() +  "  | ",false).getCreatedMethod().get();

		// build the map.
		Map<NotationMethodCallingPosition, Integer> callingPositionMap = new HashMap<>();

		for (NotationMethodCallingPosition methodCallingPosition: activeNotation.getMethodBasedCallingPositions()) {
			Integer place = plainCourse
					.getLead(methodCallingPosition.getLeadOfTenor())
					.getRow(methodCallingPosition.getCallInitiationRow())
					.getPlaceOfBell(touch.getNumberOfBells().getTenor());
			callingPositionMap.put(methodCallingPosition, place);
		}

		log.info(getLogPreamble() + " < Build calling bell positions"); //TODO log the calling bell positions
		return callingPositionMap;
	}

	@Override
	protected List<CourseBasedDecomposedCall> getImmutableCallSequence() {
		return immutableCallSequence;
	}

	@Override
	protected boolean applyNextCall(MaskedNotation maskedNotation, MethodRow currentMethodRow,
	                                CourseBasedDecomposedCall nextCall, NotationCall call) {

		// Find the method calling position.
		String callingPositionName = nextCall.getCallingPositionName();
		NotationMethodCallingPosition methodCallingPosition = maskedNotation.findMethodBasedCallingPositionByName(callingPositionName);
		checkState(methodCallingPosition != null, "Can't find calling position [" + callingPositionName + "] " +
				"in notation [" + maskedNotation.getName() + "]");

		// check we are at the correct call initiation row
		if (methodCallingPosition.getCallInitiationRow() != maskedNotation.getIteratorIndex()) {
			return false;
		}

		// Is our designated calling bell on the correct lead (of the tenor)
		int positionOfCallingBell = currentMethodRow.getPlaceOfBell(callFromBell);
		Integer place = callingPositionToCallBellPlace.get(methodCallingPosition);
		if (positionOfCallingBell != place) {
			return false;
		}

		// make the call
		maskedNotation.applyCall(call, getLogPreamble());
		return true;
	}
}
