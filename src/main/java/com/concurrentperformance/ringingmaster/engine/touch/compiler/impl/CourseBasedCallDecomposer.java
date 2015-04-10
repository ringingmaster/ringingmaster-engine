package com.concurrentperformance.ringingmaster.engine.touch.compiler.impl;

import net.jcip.annotations.NotThreadSafe;

import java.util.List;

import com.concurrentperformance.ringingmaster.engine.touch.parser.ParseType;
import com.concurrentperformance.ringingmaster.engine.touch.container.Grid;
import com.concurrentperformance.ringingmaster.engine.touch.container.Touch;
import com.concurrentperformance.ringingmaster.engine.touch.container.TouchCell;
import com.concurrentperformance.ringingmaster.engine.touch.container.TouchWord;
import com.concurrentperformance.ringingmaster.engine.touch.container.Variance;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkPositionIndex;
import static com.google.common.base.Preconditions.checkState;

/**
 * TODO comments???
 * User: Stephen
 */
@NotThreadSafe
public class CourseBasedCallDecomposer extends SkeletalCallDecomposer<CourseBasedDecomposedCall> {

	private String[] callPositionNames;

	public CourseBasedCallDecomposer(Touch touch, String logPreamble) {
		super(touch, logPreamble);
	}

	@Override
	protected void preGenerate(Touch touch) {
		Grid<TouchCell> callPositionCells = touch.callPositionView();
		checkState(callPositionCells.getRowCount() == 1);

		callPositionNames = new String[callPositionCells.getColumnCount()];

		for (int columnIndex=0;columnIndex<callPositionCells.getColumnCount();columnIndex++) {
			TouchCell cell = callPositionCells.getCell(columnIndex, 0);
			callPositionNames[columnIndex] = getCallPositionFromCell(cell);
		}
	}

	private String getCallPositionFromCell(TouchCell cell) {
		final List<TouchWord> words = cell.words();
		for (TouchWord word : words) {
			switch (word.getFirstParseType()) {
				case CALLING_POSITION:
					if (word.isValid()) {
						return word.getElementsAsString();
					}
			}
		}
		return null;
	}

	protected CourseBasedDecomposedCall buildDecomposedCall(String callName, Variance variance, int columnIndex, ParseType parseType) {
		checkPositionIndex(columnIndex, callPositionNames.length, "column index out of bounds");
		String callPositionName = callPositionNames[columnIndex];
		checkNotNull(callPositionName, "callPositionName is null. Check that the parsing is correctly excluding columns with no valid call position");
		return new CourseBasedDecomposedCall(callName, variance, callPositionName);
	}

}