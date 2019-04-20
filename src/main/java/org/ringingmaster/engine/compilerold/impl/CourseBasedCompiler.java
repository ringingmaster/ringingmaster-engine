package org.ringingmaster.engine.compilerold.impl;

import com.google.common.collect.ImmutableList;
import org.ringingmaster.engine.compiler.coursebased.CourseBasedDenormalisedCall;
import org.ringingmaster.engine.compilerold.Compiler;
import org.ringingmaster.engine.composition.Composition;
import org.ringingmaster.engine.helper.PlainCourseHelper;
import org.ringingmaster.engine.method.Bell;
import org.ringingmaster.engine.method.Method;
import org.ringingmaster.engine.method.Row;
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.notation.NotationCall;
import org.ringingmaster.engine.notation.NotationMethodCallingPosition;
import org.ringingmaster.engine.composition.checkingtype.CheckingType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.concurrent.ThreadSafe;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

/**
 * Takes a parsed composition, and converts it into a compiled composition. A proof consists of an expanded Method and termination details.
 * This is a 1 shot class. Throw it away when you have finished.
 *
 * @author stephen
 */
@ThreadSafe
public class CourseBasedCompiler extends SkeletalCompiler<CourseBasedDenormalisedCall> implements Compiler {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private final Bell callFromBell;
	private volatile List<CourseBasedDenormalisedCall> immutableCallSequence;
	private volatile Map<NotationMethodCallingPosition, Integer> callingPositionToCallBellPlace;

	CourseBasedCompiler(Composition composition, String logPreamble) {
		super(composition, logPreamble);
		checkArgument(composition.getCheckingType() == CheckingType.COURSE_BASED, "Course based compiler must use a COURSE_BASED composition. Is actually [" + composition.getCheckingType() + "]");
		callFromBell = composition.getCallFromBell();
	}

	@Override
	protected void preCompile(Composition composition) {
		immutableCallSequence = buildImmutableCallSequence(composition);
		callingPositionToCallBellPlace = buildCallingPositionLookup(composition);
	}

	private ImmutableList<CourseBasedDenormalisedCall> buildImmutableCallSequence(Composition composition) {
		return ImmutableList.copyOf(new CourseBasedCallDecomposer(composition, getLogPreamble()).createCallSequence());
	}

	private Map<NotationMethodCallingPosition, Integer> buildCallingPositionLookup(Composition composition) {
		log.info("{} > Build calling bell positions",getLogPreamble());
		// Build a plain course.
		NotationBody activeNotation = composition.getNonSplicedActiveNotation().get();
		Method plainCourse = PlainCourseHelper.buildPlainCourse(activeNotation, getLogPreamble() +  "  | ",false).getMethod().get();

		// build the map.
		Map<NotationMethodCallingPosition, Integer> callingPositionMap = new HashMap<>();

		for (NotationMethodCallingPosition methodCallingPosition: activeNotation.getMethodBasedCallingPositions()) {
			Integer place = plainCourse
					.getLead(methodCallingPosition.getLeadOfTenor())
					.getRow(methodCallingPosition.getCallInitiationRow())
					.getPlaceOfBell(composition.getNumberOfBells().getTenor());
			callingPositionMap.put(methodCallingPosition, place);
		}

		log.info(getLogPreamble() + " < Build calling bell positions"); //TODO log the calling bell positions
		return callingPositionMap;
	}

	@Override
	protected List<CourseBasedDenormalisedCall> getImmutableCallSequence() {
		return immutableCallSequence;
	}

	@Override
	protected boolean applyNextCall(MaskedNotation maskedNotation, Row currentRow,
                                    CourseBasedDenormalisedCall nextCall, NotationCall call) {

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
		int positionOfCallingBell = currentRow.getPlaceOfBell(callFromBell);
		Integer place = callingPositionToCallBellPlace.get(methodCallingPosition);
		if (positionOfCallingBell != place) {
			return false;
		}

		// make the call
		maskedNotation.applyCall(call, getLogPreamble());
		return true;
	}
}
