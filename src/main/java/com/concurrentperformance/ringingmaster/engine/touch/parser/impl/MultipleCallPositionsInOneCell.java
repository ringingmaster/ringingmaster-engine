package com.concurrentperformance.ringingmaster.engine.touch.parser.impl;

import java.util.List;

import com.concurrentperformance.ringingmaster.engine.touch.parser.ParseType;
import com.concurrentperformance.ringingmaster.engine.touch.container.Grid;
import com.concurrentperformance.ringingmaster.engine.touch.container.Touch;
import com.concurrentperformance.ringingmaster.engine.touch.container.TouchCell;
import com.concurrentperformance.ringingmaster.engine.touch.container.TouchWord;

/**
 * TODO comments???
 * User: Stephen
 */
public class MultipleCallPositionsInOneCell {
	public void parse(Touch touch) {
		parseCells(touch.callPositionView());
	}

	private void parseCells(Grid<TouchCell> cells) {

		for (TouchCell cell : cells) {
			List<TouchWord> words = cell.words();

			boolean seenValidCallingPosition = false;
			for (TouchWord word : words) {
				if (word.isValid()) {
					if (word.getFirstParseType().equals(ParseType.CALLING_POSITION)) {
						if (!seenValidCallingPosition) {
							seenValidCallingPosition = true;
						}
						else {
							word.setInvalid("Only one Calling Position allowed in this cell");
						}
					}
					else {
						word.setInvalid("Only one Calling Position allowed in this cell");
					}
				}
			}
		}
	}
}
