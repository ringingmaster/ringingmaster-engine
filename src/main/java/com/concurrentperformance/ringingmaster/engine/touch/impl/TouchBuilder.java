package com.concurrentperformance.ringingmaster.engine.touch.impl;

import com.concurrentperformance.ringingmaster.engine.NumberOfBells;
import com.concurrentperformance.ringingmaster.engine.method.impl.MethodBuilder;
import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import com.concurrentperformance.ringingmaster.engine.touch.Touch;
import com.concurrentperformance.ringingmaster.engine.touch.TouchType;

/**
 * TODO comments???
 * User: Stephen
 */
public class TouchBuilder {

	private TouchBuilder() {
	}

	/**
	 * Build a fully fledged touch
	 */
	public static Touch getInstance(NumberOfBells numberOfBells, int columnCount, int rowCount) {
		return new DefaultTouch(numberOfBells, columnCount, rowCount);
	}

	/**
	 * Shortcut to build a touch for a single notation plain course.
	 *
	 * @param notationBody
	 * @return
	 */
	public static Touch buildPlainCourseInstance(NotationBody notationBody) {
		Touch touch = getInstance(notationBody.getNumberOfWorkingBells(), 1,1);
		touch.addNotation(notationBody);
		touch.setTouchType(TouchType.LEAD_BASED);
		touch.setTerminationSpecificRow(MethodBuilder.buildRoundsRow(notationBody.getNumberOfWorkingBells()));
		touch.setTerminationMaxRows(10000);// Just as a safety stop
		touch.setName("Plain Course of " + notationBody.getNameIncludingNumberOfBells());
		return touch;
	}
}
