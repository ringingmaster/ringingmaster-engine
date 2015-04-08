package com.concurrentperformance.ringingmaster.engine.touch.parser.impl;

import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import com.concurrentperformance.ringingmaster.engine.notation.NotationCall;
import com.concurrentperformance.ringingmaster.engine.notation.NotationMethodCallingPosition;
import com.concurrentperformance.ringingmaster.engine.touch.parser.ParseType;
import com.concurrentperformance.ringingmaster.engine.touch.container.Touch;
import com.concurrentperformance.ringingmaster.engine.touch.container.TouchCell;
import com.concurrentperformance.ringingmaster.engine.touch.container.TouchDefinition;
import com.concurrentperformance.ringingmaster.engine.touch.container.TouchType;
import com.google.common.base.Strings;

import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import static com.google.common.base.Preconditions.checkState;

/**
 * Parser to do the initial pass of identifying all the basic types.
 * User: Stephen
 */
public class AssignParseTypeParser {


	private static Comparator<String> SORT_SIZE_THEN_NAME = new Comparator<String>() {
		@Override
		public int compare(String o1, String o2) {
			int result = (o2.length() - o1.length());
			if (result != 0) {
				return result;
			}
			return o1.compareTo(o2);
		}
	};


	public void parse(Touch touch) {
		parseCallPositionArea(touch);
		parseSpliceArea(touch);
		parseMainBodyArea(touch);
		parseDefinitions(touch);
	}

	private void parseCallPositionArea(Touch touch) {
		if (touch.getTouchType() != TouchType.COURSE_BASED) {
			return;
		}
		SortedMap<String, ParseType> parseTokenMappings = new TreeMap<>(SORT_SIZE_THEN_NAME);
		addCallingPositionTokens(touch, parseTokenMappings);
		addWhitespaceTokens(parseTokenMappings);

		for (TouchCell cell : touch.callPositionView()) {
			parseCell(cell, parseTokenMappings);
		}
	}

	private void parseSpliceArea(Touch touch) {
		if (!touch.isSpliced()) {
			return;
		}
		SortedMap<String, ParseType> parseTokenMappings = new TreeMap<>(SORT_SIZE_THEN_NAME);
		addSpliceTokens(touch, parseTokenMappings);
		addVarianceTokens(parseTokenMappings);
		addGroupTokens(parseTokenMappings);
		addDefinitionTokens(touch, parseTokenMappings);
		addWhitespaceTokens(parseTokenMappings);

		for (TouchCell cell : touch.spliceView()) {
			parseCell(cell, parseTokenMappings);
		}
	}

	private void parseMainBodyArea(Touch touch) {
		SortedMap<String, ParseType> parseTokenMappings = new TreeMap<>(SORT_SIZE_THEN_NAME);
		addCallTokens(touch, parseTokenMappings);
		addPlainLeadToken(touch, parseTokenMappings);
		addVarianceTokens(parseTokenMappings);
		addGroupTokens(parseTokenMappings);
		addDefinitionTokens(touch, parseTokenMappings);
		addWhitespaceTokens(parseTokenMappings);

		for (TouchCell cell : touch.mainBodyView()) {
			parseCell(cell, parseTokenMappings);
		}
	}

	private void parseDefinitions(Touch touch) {
		SortedMap<String, ParseType> parseTokenMappings = new TreeMap<>(SORT_SIZE_THEN_NAME);
		addCallTokens(touch, parseTokenMappings);
		addPlainLeadToken(touch, parseTokenMappings);
//TODO should we allow variance in definitions?	 Probably not.	addVarianceTokens(parseTokenMappings);
		addGroupTokens(parseTokenMappings);
//TODO should we allow embedded definitions in definitions?	probably, but will need some good tests. addDefinitionTokens(touch, parseTokenMappings);
		addWhitespaceTokens(parseTokenMappings);
		for (TouchDefinition definition : touch.getDefinitions()) {
			parseCell(definition, parseTokenMappings);
		}

	}



	private void parseCell(TouchCell cell, SortedMap <String, ParseType> parseTokenMappings) {
		for (Map.Entry<String, ParseType> parseToken : parseTokenMappings.entrySet()) {
			String token = parseToken.getKey();
			ParseType parseType = parseToken.getValue();
			checkState(token.length() > 0, "Should never have an empty token. Mapped to [{}]", parseType);

			int currentIndex = 0;
			String cellAsString = cell.getAsStringWithParsedElementsAsWhitespace();
			while ((currentIndex = cellAsString.indexOf(token,currentIndex)) != -1) {
				if (cell.isAllOfType(currentIndex, token.length(), ParseType.UNPARSED)) {
					cell.createWord(currentIndex, token.length(), parseType);
					currentIndex+=token.length();
				}
				else {
					currentIndex++;
				}
			}
		}
	}

	private void addCallingPositionTokens(Touch touch, SortedMap<String, ParseType> parsings) {
		for (NotationBody notation : touch.getNotationsInUse()) {
			for (NotationMethodCallingPosition callingPosition : notation.getMethodBasedCallingPositions()) {
				parsings.put(callingPosition.getName(), ParseType.CALLING_POSITION);
			}
		}
	}

	private void addPlainLeadToken(Touch touch, SortedMap<String, ParseType> parsings) {
		if (touch.getTouchType() == TouchType.LEAD_BASED) {
			parsings.put(touch.getPlainLeadToken(), ParseType.PLAIN_LEAD);
		}
	}

	private void addCallTokens(Touch touch, SortedMap<String, ParseType> parsings) {
		for (NotationBody notation : touch.getNotationsInUse()) {
			for (NotationCall notationCall : notation.getCalls()) {
				parsings.put(notationCall.getNameShorthand(), ParseType.CALL);
				parsings.put(notationCall.getName(), ParseType.CALL);
			}
		}
	}

	private void addSpliceTokens(Touch touch, SortedMap<String, ParseType> parsings) {
		for (NotationBody notation : touch.getNotationsInUse()) {
			if (!Strings.isNullOrEmpty(notation.getSpliceIdentifier())){
				parsings.put(notation.getSpliceIdentifier(), ParseType.SPLICE);
			}
			parsings.put(notation.getName(), ParseType.SPLICE);
			parsings.put(notation.getNameIncludingNumberOfBells(), ParseType.SPLICE);
		}
	}

	private void addDefinitionTokens(Touch touch, SortedMap<String, ParseType> parsings) {
		for (TouchDefinition definition : touch.getDefinitions()) {
			parsings.put(definition.getName(), ParseType.DEFINITION);
		}
	}

	private void addWhitespaceTokens(SortedMap<String, ParseType> parsings) {
		parsings.put(" ", ParseType.WHITESPACE);
	}

	private void addVarianceTokens(SortedMap<String, ParseType> parsings) { //TODO ensure these chars cant appear anywhere else. i.e.in calls method names Etc
		parsings.put("[", ParseType.VARIANCE_OPEN);
		parsings.put("]", ParseType.VARIANCE_CLOSE);
	}

	private void addGroupTokens(SortedMap<String, ParseType> parsings) { //TODO ensure these chars cant appear anywhere else. i.e.in calls method names Etc
		parsings.put("(", ParseType.GROUP_OPEN);
		parsings.put(")", ParseType.GROUP_CLOSE);
	}

}
