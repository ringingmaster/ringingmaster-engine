package com.concurrentperformance.ringingmaster.engine.notation;

import net.jcip.annotations.Immutable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.concurrentperformance.ringingmaster.engine.method.Bell;

/**
 * Represents a single 'row' of a notation. i.e. if the full
 * notation was 'X.15.X', then this would represent either the
 * 'X' or the '15'
 *
 * @author Stephen Lake
 */
@Immutable
public enum NotationPlace {
	ALL_CHANGE(Integer.MIN_VALUE, "Xx-", "-"),
	PLACE_1(0,   "1",  Bell.BELL_1.getMnemonic()),
	PLACE_2(1,   "2",  Bell.BELL_2.getMnemonic()),
	PLACE_3(2,   "3",  Bell.BELL_3.getMnemonic()),
	PLACE_4(3,   "4",  Bell.BELL_4.getMnemonic()),
	PLACE_5(4,   "5",  Bell.BELL_5.getMnemonic()),
	PLACE_6(5,   "6",  Bell.BELL_6.getMnemonic()),
	PLACE_7(6,   "7",  Bell.BELL_7.getMnemonic()),
	PLACE_8(7,   "8",  Bell.BELL_8.getMnemonic()),
	PLACE_9(8,   "9",  Bell.BELL_9.getMnemonic()),
	PLACE_10(9,  "0",  Bell.BELL_10.getMnemonic()),
	PLACE_11(10, "Ee", Bell.BELL_11.getMnemonic()),
	PLACE_12(11, "Tt", Bell.BELL_12.getMnemonic()); //If adding more, check also in NotationSplitter REGEX

	private static final Map<Integer, NotationPlace> entity = new HashMap<Integer, NotationPlace>();

	static {
		for(final NotationPlace value : NotationPlace.values()) {
			if (value != NotationPlace.ALL_CHANGE) {
				entity.put(Integer.valueOf(value.zeroBasedPlace), value);
			}
		}
	}


	/** The actual position of place being made */
	private final int zeroBasedPlace;
	/** The regex / Pattern for identifying the place */
	private final String regex;
	private final Pattern extractPattern;
	/** The string used for display */
	private final String display;

	NotationPlace(final int zeroBasedPlace, final String regex, final String display) {
		this.zeroBasedPlace = zeroBasedPlace;
		this.regex = regex;
		extractPattern = Pattern.compile("[" + regex + "]");
		this.display = display;
	}

	public String getRegex() {
		return regex;
	}

	public String toDisplayString() {
		return display;
	}

	public int getZeroBasedPlace() {
		return zeroBasedPlace;
	}

	/**
	 * Decode the passed notationShorthand to a Set of NotationElements.
	 * 
	 * @param notationShorthand
	 * @return Set<NotationPlace>, with one or more NotationElements
	 */
	public static Set<NotationPlace> getNotationElements(final CharSequence notationShorthand) {
		final Set<NotationPlace> matchedElements = new HashSet<NotationPlace>();

		for (int i=0;i<notationShorthand.length();i++) {
			final char notationChar = notationShorthand.charAt(i);
			final NotationPlace notationPlace = getNotationElement(notationChar);
			matchedElements.add(notationPlace);
		}

		return matchedElements;
	}

	/**
	 * Decode the passed single shorthand character to a single NotationPlace.
	 * 
	 * @param notationChar
	 * @return NotationPlace, or null if not found
	 */
	public static NotationPlace getNotationElement(final char notationChar) {
		NotationPlace matchedElemenet = null;

		final String notationString = Character.toString(notationChar);

		for (final NotationPlace element : values()) {
			final Pattern p = element.extractPattern;

			final Matcher matcher = p.matcher(notationString);
			if (matcher.matches()) {
				matchedElemenet = element;
				break;
			}
		}
		return matchedElemenet;
	}


	public static synchronized NotationPlace valueOf(final int zeroBasedPlace) {
		return entity.get(zeroBasedPlace);
	}


}