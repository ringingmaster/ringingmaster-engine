package org.ringingmaster.engine.compilerold.impl;

/**
 * TODO comments???
 * User: Stephen
 */
//@NotThreadSafe
//public class CourseBasedCallDecomposer extends SkeletalCallDecomposer<CourseBasedDenormalisedCall> {
//
//	private String[] callingPositionNames;
//
//	public CourseBasedCallDecomposer(Composition composition, String logPreamble) {
//		super(composition, logPreamble);
//	}
//
//	@Override
//	protected void preGenerate(Composition composition) {
////TODO		Grid<CompositionCell> callingPositionCells = composition.callingPositionView();
////		checkState(callingPositionCells.getRowCount() == 1);
////
////		callingPositionNames = new String[callingPositionCells.getColumnCount()];
////
////		for (int columnIndex=0;columnIndex<callingPositionCells.getColumnCount();columnIndex++) {
////			CompositionCell cell = callingPositionCells.getCell(columnIndex, 0);
////			callingPositionNames[columnIndex] = getCallingPositionFromCell(cell);
////		}
//	}
//
////	private String getCallingPositionFromCell(CompositionCell cell) {
////		final List<CompositionWord> words = cell.words();
////		for (CompositionWord word : words) {
////			switch (word.getFirstParseType()) {
////				case CALLING_POSITION:
////					if (word.isValid()) {
////						return word.getElementsAsString();
////					}
////			}
////		}
////		return null;
////	}
//
//	protected CourseBasedDenormalisedCall buildDecomposedCall(String callName, Variance variance, int columnIndex, ParseType parseType) {
//		checkPositionIndex(columnIndex, callingPositionNames.length, "column index out of bounds");
//		String callingPositionName = callingPositionNames[columnIndex];
//		checkNotNull(callingPositionName, "callingPositionName is null. Check that the parsing is correctly excluding columns with no valid call position");
//		return new CourseBasedDenormalisedCall(callName, variance, callingPositionName);
//	}
//
//}
