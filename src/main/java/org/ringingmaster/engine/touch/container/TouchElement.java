package org.ringingmaster.engine.touch.container;

import org.ringingmaster.engine.touch.parser.ParseType;
import org.ringingmaster.engine.touch.variance.Variance;

/**
 * Represents a single character in a touch grid.
 * User: Stephen
 */
public interface TouchElement {

	public TouchElement clone(TouchCell parentCell, TouchWord wordClone) throws CloneNotSupportedException;

	char getCharacter();

	Variance getVariance();
	void setVariance(Variance variance);

	TouchWord createSingleElementWord(ParseType parseType);

	TouchWord getWord();
	void setWord(TouchWord word, ParseType parseType);

	ParseType getParseType();

	void resetParseData();
}
