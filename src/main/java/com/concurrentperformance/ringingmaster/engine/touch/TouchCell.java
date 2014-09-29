package com.concurrentperformance.ringingmaster.engine.touch;

import java.util.List;

import com.concurrentperformance.ringingmaster.engine.parser.ParseType;

/**
 * TODO comments???
 * User: Stephen
 */
public interface TouchCell extends Iterable<TouchElement> , Cloneable {

	TouchCell clone() throws CloneNotSupportedException;

	TouchElement getElement(int index);

	int getLength();

	TouchElement insert(char character, int index);
	void remove(int index);

	void add(String characters);

	String getAsStringWithParsedElementsAsWhitespace();

	void resetParseData();

	boolean isAllOfType(int startIndex, int length, ParseType parseType);

	TouchWord createWord(int startIndex, int length, ParseType parseType);

	List<TouchWord> words();
}
