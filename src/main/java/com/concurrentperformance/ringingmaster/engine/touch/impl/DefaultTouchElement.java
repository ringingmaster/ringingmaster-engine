package com.concurrentperformance.ringingmaster.engine.touch.impl;

import com.concurrentperformance.ringingmaster.engine.parser.ParseType;
import com.concurrentperformance.ringingmaster.engine.touch.TouchCell;
import com.concurrentperformance.ringingmaster.engine.touch.TouchElement;
import com.concurrentperformance.ringingmaster.engine.touch.TouchWord;
import com.concurrentperformance.ringingmaster.engine.touch.Variance;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Represents a single character, along with its variance data and its
 * temp parse data.
 *
 * @author Stephen
 */
public class DefaultTouchElement implements TouchElement {

	private final TouchCell parentCell;
	private final char character;
	private Variance variance;

	// parse data - can be cleared
	private ParseType parseType;
	private TouchWord word;


	DefaultTouchElement(TouchCell parentCell,char character) {
		this.parentCell = parentCell;
		this.character = character;
		this.variance = NullVariance.getInstance();
		resetParseData();
	}

	@Override
	public TouchElement clone(TouchCell parentCell, TouchWord wordClone) throws CloneNotSupportedException {
		DefaultTouchElement elementClone = new DefaultTouchElement(parentCell, character);
		elementClone.variance = this.variance;
		elementClone.word = wordClone;
		elementClone.parseType = this.parseType;
		return elementClone;
	}

	@Override
	public char getCharacter() {
		return character;
	}

	@Override
	public Variance getVariance() {
		return variance;
	}

	@Override
	public void setVariance(Variance variance) {
		checkNotNull(variance, "variance must not be null");
		checkArgument(character == '[', "variance can only be set on TouchElement's with a character of '['");
		this.variance = variance;
	}

	@Override
	public TouchWord createSingleElementWord(ParseType parseType) {
		this.parseType = parseType;
		word = new DefaultTouchWord(parentCell);
		return word;
	}

	@Override
	public TouchWord getWord() {
		return word;
	}

	@Override
	public void setWord(TouchWord word, ParseType parseType) {
		this.word = word;
		this.parseType = parseType;
	}

	@Override
	public ParseType getParseType() {
		return parseType;
	}

	@Override
	public void resetParseData() {
		this.word = null;
		this.parseType = ParseType.UNPARSED;
	}

	@Override
	public String toString() {
		String varianceToString = variance.toString();
		return "{" +
				"'" + character + "' " +
				"'" + parseType + "' " +
				((varianceToString.length() > 0)?("'" + varianceToString + "' "):"") +
				"}";
	}
}
