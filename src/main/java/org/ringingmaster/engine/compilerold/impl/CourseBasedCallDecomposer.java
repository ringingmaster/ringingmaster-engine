package org.ringingmaster.engine.compilerold.impl;

import org.ringingmaster.engine.compiler.coursebased.CourseBasedDenormalisedCall;
import org.ringingmaster.engine.compiler.variance.Variance;
import org.ringingmaster.engine.parser.assignparsetype.ParseType;
import org.ringingmaster.engine.composition.Composition;

import javax.annotation.concurrent.NotThreadSafe;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkPositionIndex;

/**
 * TODO comments???
 * User: Stephen
 */
@NotThreadSafe
public class CourseBasedCallDecomposer extends SkeletalCallDecomposer<CourseBasedDenormalisedCall> {

	private String[] callPositionNames;

	public CourseBasedCallDecomposer(Composition composition, String logPreamble) {
		super(composition, logPreamble);
	}

	@Override
	protected void preGenerate(Composition composition) {
//TODO		Grid<CompositionCell> callPositionCells = composition.callPositionView();
//		checkState(callPositionCells.getRowCount() == 1);
//
//		callPositionNames = new String[callPositionCells.getColumnCount()];
//
//		for (int columnIndex=0;columnIndex<callPositionCells.getColumnCount();columnIndex++) {
//			CompositionCell cell = callPositionCells.getCell(columnIndex, 0);
//			callPositionNames[columnIndex] = getCallPositionFromCell(cell);
//		}
	}

//	private String getCallPositionFromCell(CompositionCell cell) {
//		final List<CompositionWord> words = cell.words();
//		for (CompositionWord word : words) {
//			switch (word.getFirstParseType()) {
//				case CALLING_POSITION:
//					if (word.isValid()) {
//						return word.getElementsAsString();
//					}
//			}
//		}
//		return null;
//	}

	protected CourseBasedDenormalisedCall buildDecomposedCall(String callName, Variance variance, int columnIndex, ParseType parseType) {
		checkPositionIndex(columnIndex, callPositionNames.length, "column index out of bounds");
		String callPositionName = callPositionNames[columnIndex];
		checkNotNull(callPositionName, "callPositionName is null. Check that the parsing is correctly excluding columns with no valid call position");
		return new CourseBasedDenormalisedCall(callName, variance, callPositionName);
	}

}
