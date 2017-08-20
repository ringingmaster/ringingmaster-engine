package org.ringingmaster.engine.touch.container.impl;

import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.method.impl.MethodBuilder;
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.touch.container.Touch;
import org.ringingmaster.engine.touch.container.TouchCheckingType;

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
		final DefaultTouch defaultTouch = new DefaultTouch();
		defaultTouch.setNumberOfBells(numberOfBells);
		defaultTouch.setColumnCount(columnCount);
		defaultTouch.setRowCount(rowCount);
		return defaultTouch;
	}

	/**
	 * Shortcut to build a touch for a single notation plain course.
	 *
	 * @param notationBody
	 * @return
	 */
	public static Touch buildPlainCourseInstance(NotationBody notationBody) {
		final Touch touch = new DefaultTouch();
		touch.setTitle("Plain Course of " + notationBody.getNameIncludingNumberOfBells());
		touch.setNumberOfBells(notationBody.getNumberOfWorkingBells());
		touch.setColumnCount(1);
		touch.setRowCount(1);
		touch.addNotation(notationBody);
		touch.setTouchCheckingType(TouchCheckingType.LEAD_BASED);
		touch.setTerminationChange(MethodBuilder.buildRoundsRow(notationBody.getNumberOfWorkingBells()));
		touch.setTerminationMaxLeads(Touch.TERMINATION_MAX_LEADS_MAX);
		touch.setTerminationMaxRows(Touch.TERMINATION_MAX_ROWS_MAX);

		return touch;
	}
}
