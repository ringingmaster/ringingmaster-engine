package org.ringingmaster.engine.parser.impl;

import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.touch.container.Touch;
import org.ringingmaster.engine.touch.container.TouchCell;
import org.ringingmaster.engine.touch.container.TouchDefinition;
import org.ringingmaster.engine.touch.container.TouchElement;
import org.ringingmaster.engine.touch.container.TouchWord;
import org.ringingmaster.engine.parser.ParseType;

import static org.ringingmaster.engine.parser.ParseType.CALL_MULTIPLIER;
import static org.ringingmaster.engine.parser.ParseType.DEFAULT_CALL_MULTIPLIER;
import static org.ringingmaster.engine.parser.ParseType.DEFINITION_MULTIPLIER;
import static org.ringingmaster.engine.parser.ParseType.GROUP_OPEN_MULTIPLIER;
import static org.ringingmaster.engine.parser.ParseType.PLAIN_LEAD_MULTIPLIER;
import static org.ringingmaster.engine.parser.ParseType.SPLICE_MULTIPLIER;
import static org.ringingmaster.engine.parser.ParseType.UNPARSED;


/**
 * TODO comments???
 * User: Stephen
 */
public class MultiplierParser {

	public void parse(Touch touch) {
		boolean hasDefaultCall = hasDefaultCall(touch);
		for (TouchCell cell : touch.mainBodyView()) {
			parseCellNumbers(cell, false, touch.isSpliced(), hasDefaultCall);
		}
		for (TouchDefinition cell : touch.getDefinitions()) {
			// TODO eventually parse definition based on usage in splice or main body.
			parseCellNumbers(cell, false, touch.isSpliced(), hasDefaultCall);
		}

		for (TouchCell cell : touch.spliceView()) {
			parseCellNumbers(cell, true, touch.isSpliced(), hasDefaultCall);
		}
	}

	private void parseCellNumbers(TouchCell cell, boolean spliceCell, boolean splicedPerformance, boolean hasDefaultCall) {
		TouchElement nextElement = null;

		// reverse iterate elements
		for (int index = cell.getLength()-1;index>=0;index--) {
			TouchElement element = cell.getElement(index);

			if (element.getParseType().equals(UNPARSED) &&
					Character.isDigit(element.getCharacter())) {
				if (nextElement == null) {
					matchAsDefaultCallMultiplier(element, spliceCell, splicedPerformance, hasDefaultCall);
				}
				else {
					switch (nextElement.getParseType()) {
						case UNPARSED:
						case WHITESPACE:
						case VARIANCE_OPEN:
						case VARIANCE_CLOSE:
						case GROUP_CLOSE:
							matchAsDefaultCallMultiplier(element, spliceCell, splicedPerformance, hasDefaultCall);
							break;

						case CALL:
							addToNextElementsWord(element, CALL_MULTIPLIER, nextElement);
							break;
						case GROUP_OPEN:
							addToNextElementsWord(element, GROUP_OPEN_MULTIPLIER, nextElement);
							break;
						case PLAIN_LEAD:
							addToNextElementsWord(element, PLAIN_LEAD_MULTIPLIER, nextElement);
							break;
						case DEFINITION:
							addToNextElementsWord(element, DEFINITION_MULTIPLIER, nextElement);
							break;
						case SPLICE:
							addToNextElementsWord(element, SPLICE_MULTIPLIER, nextElement);
							break;

						case DEFAULT_CALL_MULTIPLIER:
						case CALL_MULTIPLIER:
						case GROUP_OPEN_MULTIPLIER:
						case PLAIN_LEAD_MULTIPLIER:
						case DEFINITION_MULTIPLIER:
						case SPLICE_MULTIPLIER:
							addToNextElementsWord(element, nextElement.getParseType(), nextElement);
							break;
							//TODO BLOCK and BLOCK_MULTIPLIER
					}
				}
			}
			nextElement = element;
		}
	}

	//TODO need to enhance tool tips as per old ringingmaster. See: CString CellElement::getTipText()


	private void matchAsDefaultCallMultiplier(TouchElement element, boolean spliceCell, boolean splicedPerformance, boolean hasDefaultCall) {
		if (spliceCell) {
			return;
		}

		TouchWord word = element.createSingleElementWord(DEFAULT_CALL_MULTIPLIER);
		if (!hasDefaultCall) {
			if (splicedPerformance) {
				word.setInvalid("Default call not defined in all methods");
			}
			else {
				word.setInvalid("No default call defined");
			}
		}
	}

	private void addToNextElementsWord(TouchElement element, ParseType parseType, TouchElement nextElement) {
		TouchWord nextWord = nextElement.getWord();
		element.setWord(nextWord, parseType);
	}

	private boolean hasDefaultCall(Touch touch) {
		for (NotationBody notation : touch.getNotationsInUse()) {
			if (notation.getDefaultCall() == null) {
				return false;
			}
		}
		return true;
	}
}
