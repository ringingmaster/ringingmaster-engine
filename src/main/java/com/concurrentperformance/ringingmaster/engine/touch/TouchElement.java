package com.concurrentperformance.ringingmaster.engine.touch;

import com.concurrentperformance.ringingmaster.engine.parser.ParseType;

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
