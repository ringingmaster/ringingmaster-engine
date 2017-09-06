package org.ringingmaster.engine.touch.container.impl;

import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.method.impl.MethodBuilder;
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.touch.container.Touch;
import org.ringingmaster.engine.touch.newcontainer.ObservableTouch;
import org.ringingmaster.engine.touch.newcontainer.checkingtype.CheckingType;

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
	public static ObservableTouch newTouch(NumberOfBells numberOfBells) {
		final ObservableTouch touch = new ObservableTouch();
		touch.setNumberOfBells(numberOfBells);
		return touch;
	}

	/**
	 * Shortcut to build a touch for a single notation plain course.
	 *
	 * @param notationBody
	 * @return
	 */
	public static ObservableTouch buildPlainCourseInstance(NotationBody notationBody) {
		final ObservableTouch touch = new ObservableTouch();
		touch.setTitle("Plain Course of " + notationBody.getNameIncludingNumberOfBells());
		touch.setNumberOfBells(notationBody.getNumberOfWorkingBells());
//TODO		touch.setColumnCount(1);
//		touch.setRowCount(1);
		touch.addNotation(notationBody);
		touch.setTouchCheckingType(CheckingType.LEAD_BASED);
		touch.setTerminationChange(MethodBuilder.buildRoundsRow(notationBody.getNumberOfWorkingBells()));
		touch.setTerminationMaxLeads(Touch.TERMINATION_MAX_LEADS_MAX);
		touch.setTerminationMaxRows(Touch.TERMINATION_MAX_ROWS_MAX);

		return touch;
	}
}
