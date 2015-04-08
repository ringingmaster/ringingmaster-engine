package com.concurrentperformance.ringingmaster.engine.touch.container.impl;

import com.concurrentperformance.ringingmaster.engine.touch.container.TouchElement;
import com.concurrentperformance.ringingmaster.engine.touch.container.TouchWord;
import com.concurrentperformance.ringingmaster.engine.touch.parser.ParseType;
import com.concurrentperformance.ringingmaster.engine.touch.container.TouchCell;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * TODO comments???
 * User: Stephen
 */
public class DefaultTouchWord implements TouchWord {

	private boolean valid;
	private String toolTipText;
	private final TouchCell parentCell;


	public DefaultTouchWord(TouchCell parentCell) {
		valid = true;
		toolTipText = "";
		this.parentCell = parentCell;
	}

	@Override
	public void setInvalid(String toolTipText) {
		valid = false;
		this.toolTipText = checkNotNull(toolTipText, "toolTipText must not be null");
	}

	@Override
	public boolean isValid() {
		return valid;
	}

	@Override
	public String getToolTip() {
		return toolTipText;
	}

	@Override
	public List<TouchElement> getElements() {
		List<TouchElement> elements = new ArrayList<>();
		for (TouchElement touchElement : parentCell) {
			if (touchElement.getWord() == this) {
				elements.add(touchElement);
			}
		}
		return elements;
	}

	@Override
	public String getElementsAsString() {
		List<TouchElement> elements = getElements();
		StringBuilder buff = new StringBuilder();

		for (TouchElement element : elements) {
			buff.append(element.getCharacter());
		}
		return buff.toString();
	}

	@Override
	public ParseType getFirstParseType() {
		return getElements().get(0).getParseType();
	}

	@Override
	public String toString() {
		return "DefaultTouchWord{" +
				"elements=" + getElements() +
				", valid=" + valid +
				", toolTipText=" + toolTipText +
				'}';
	}
}
