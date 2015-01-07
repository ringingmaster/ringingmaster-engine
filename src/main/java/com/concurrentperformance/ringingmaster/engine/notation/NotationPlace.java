package com.concurrentperformance.ringingmaster.engine.notation;

import com.concurrentperformance.ringingmaster.engine.method.Bell;
import net.jcip.annotations.Immutable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	PLACE_1( Bell.BELL_1,  "1"),
	PLACE_2( Bell.BELL_2,  "2"),
	PLACE_3( Bell.BELL_3,  "3"),
	PLACE_4( Bell.BELL_4,  "4"),
	PLACE_5( Bell.BELL_5,  "5"),
	PLACE_6( Bell.BELL_6,  "6"),
	PLACE_7( Bell.BELL_7,  "7"),
	PLACE_8( Bell.BELL_8,  "8"),
	PLACE_9( Bell.BELL_9,  "9"),
	PLACE_10(Bell.BELL_10, "0"),
	PLACE_11(Bell.BELL_11, "Ee"),
	PLACE_12(Bell.BELL_12, "Tt"),
	PLACE_13(Bell.BELL_13, "Aa"),
	PLACE_14(Bell.BELL_14, "Bb"),
	PLACE_15(Bell.BELL_15, "Cc"),
	PLACE_16(Bell.BELL_16, "Dd"),
	PLACE_17(Bell.BELL_17, "Ff"),
	PLACE_18(Bell.BELL_18, "Gg"),
	PLACE_19(Bell.BELL_19, "Hh"),
	PLACE_20(Bell.BELL_20, "Jj"),
	PLACE_21(Bell.BELL_21, "Kk"),
	PLACE_22(Bell.BELL_22, "Ll"),
	PLACE_23(Bell.BELL_23, "Mm"),
	PLACE_24(Bell.BELL_24, "Nn"),
	PLACE_25(Bell.BELL_25, "Pp"),
	PLACE_26(Bell.BELL_26, "Qq"),
	PLACE_27(Bell.BELL_27, "Rr"),
	PLACE_28(Bell.BELL_28, "Ss"),
	PLACE_29(Bell.BELL_29, "Uu"),
	PLACE_30(Bell.BELL_30, "Vv");

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

	NotationPlace(Bell bell, final String regex) {
		this(bell.getZeroBasedBell(), regex, bell.getMnemonic());
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