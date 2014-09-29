package com.concurrentperformance.ringingmaster.engine.parser.impl;

import com.google.common.collect.Lists;

import java.util.ArrayDeque;
import java.util.Deque;

import com.concurrentperformance.ringingmaster.engine.touch.Touch;
import com.concurrentperformance.ringingmaster.engine.touch.TouchCell;
import com.concurrentperformance.ringingmaster.engine.touch.TouchDefinition;
import com.concurrentperformance.ringingmaster.engine.touch.TouchElement;

/**
 * TODO comments???
 * User: Stephen
 */
public class GroupLogicParser {

	public void parse(Touch touch) {
		parseCells(touch.mainBodyView());
		// We parse definitions individually. This is so that any grouping in a definition
		// must be complete sets within the definition. i.e a matched open and close brace.
		for (TouchDefinition definition : touch.getDefinitions()) {
			parseCells(Lists.<TouchCell>newArrayList(definition));
		}
	}

	public void parseCells(Iterable<TouchCell> cells) {
		Deque<TouchElement> openGroups = new ArrayDeque<>();

		for (TouchCell cell : cells) {
			for (TouchElement touchElement : cell) {
				switch (touchElement.getParseType()) {
					case GROUP_OPEN:
						openGroups.addFirst(touchElement);
						break;
					case GROUP_CLOSE:
						if (openGroups.size() > 0) {
							openGroups.removeFirst();
						}
						else {
							touchElement.getWord().setInvalid("No matching opening brace");
						}
						break;
				}
			}
		}
		while (openGroups.peekFirst() != null ) {
			openGroups.removeFirst().getWord().setInvalid("No matching closing brace");
		}
	}
}
