package com.concurrentperformance.ringingmaster.engine.touch.container.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.concurrentperformance.ringingmaster.engine.touch.container.TouchCell;
import com.concurrentperformance.ringingmaster.engine.touch.container.TouchElement;
import com.concurrentperformance.ringingmaster.engine.touch.container.TouchWord;
import com.concurrentperformance.ringingmaster.engine.touch.parser.ParseType;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkPositionIndexes;


/**
 * TODO
 * User: Stephen
 */
public class DefaultTouchCell implements TouchCell {

	private List<TouchElement> elements = new ArrayList<>();

	@Override
	public TouchCell clone() throws CloneNotSupportedException {
		DefaultTouchCell cellClone = new DefaultTouchCell();
		cloneElementsTo(cellClone);
		return cellClone;
	}

	protected void cloneElementsTo(DefaultTouchCell cellClone) throws CloneNotSupportedException {
		TouchWord wordClone = null;
		TouchWord lastWord = null;
		for (int i=0;i<elements.size();i++) {
			TouchElement element = elements.get(i);
			TouchWord word = element.getWord();
			if (word != lastWord) {
				lastWord = word;
				wordClone = new DefaultTouchWord(cellClone);
			}
			TouchElement elementClone = element.clone(cellClone, wordClone);
			cellClone.elements.add(elementClone);
		}
	}

	@Override
	public TouchElement getElement(int index) {
		TouchElement element = elements.get(index);
		return element;
	}

	@Override
	public int getLength() {
		return elements.size();
	}

	@Override
	public TouchElement insert(char character, int index) {
		DefaultTouchElement element = new DefaultTouchElement(this, character);
		elements.add(index, element);
		return element;
	}

	@Override
	public void remove(int index) {
		elements.remove(index);
	}

	@Override
	public void add(String characters) {
		for (char character : characters.toCharArray()) {
			elements.add(new DefaultTouchElement(this, character));
		}
	}

	@Override
	public String getAsStringWithParsedElementsAsWhitespace() {
		StringBuilder asString = new StringBuilder();
		for (TouchElement element : elements) {
			if (element.getParseType() == ParseType.UNPARSED) {
				asString.append(element.getCharacter());
			}
			else {
				asString.append(' ');
			}
		}
		return asString.toString();

	}

	@Override
	public void resetParseData() {
		for (TouchElement element : elements) {
			element.resetParseData();
		}
	}

	@Override
	public boolean isAllOfType(int startIndex, int length, ParseType parseType) {
		checkNotNull(parseType, "parseType must not be null");
		checkPositionIndexes(startIndex, startIndex+length, getLength());
		for (int index=startIndex;index<startIndex+length;index++) {
			if (!parseType.equals(getElement(index).getParseType())) {
				return false;
			}
		}
		return true;
	}

	@Override
	public TouchWord createWord(int startIndex, int length, ParseType parseType) {
		checkNotNull(parseType, "parseType must not be null");
		checkPositionIndexes(startIndex, startIndex+length, getLength());

		TouchWord word = new DefaultTouchWord(this);

		for (int index=startIndex;index<startIndex+length;index++) {
			getElement(index).setWord(word, parseType);
		}

		return word;
	}

	@Override
	public List<TouchWord> words() {
		List<TouchWord> words = new ArrayList<>();
		TouchWord previousWord = null;
		for (TouchElement element : elements) {
			TouchWord word = element.getWord();
			if (word != null && word != previousWord) {
				words.add(word);
				previousWord = word;
			}
		}

		return Collections.unmodifiableList(words);
	}

	@Override
	public Iterator<TouchElement> iterator() {
		return elements.iterator();
	}

	@Override
	public String toString() {
		return "{" +
				"elements=" + elements +
				'}';
	}
}
