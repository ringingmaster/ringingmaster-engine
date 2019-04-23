package org.ringingmaster.engine.notation;

import org.ringingmaster.engine.method.Bell;

import javax.annotation.concurrent.Immutable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a single place 'element' of a notation. i.e. if the full
 * notation was 'X.15', then this would represent either the
 * 'X' or the '1' or the '5'
 *
 * @author Stephen Lake
 */
@Immutable
public enum Place {
    ALL_CHANGE(Integer.MIN_VALUE, "Xx-", "-"),
    PLACE_1 (Bell.BELL_1,  "1" ),
    PLACE_2 (Bell.BELL_2,  "2" ),
    PLACE_3 (Bell.BELL_3,  "3" ),
    PLACE_4 (Bell.BELL_4,  "4" ),
    PLACE_5 (Bell.BELL_5,  "5" ),
    PLACE_6 (Bell.BELL_6,  "6" ),
    PLACE_7 (Bell.BELL_7,  "7" ),
    PLACE_8 (Bell.BELL_8,  "8" ),
    PLACE_9 (Bell.BELL_9,  "9" ),
    PLACE_10(Bell.BELL_10, "0" ),
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

    private static final Map<Integer, Place> entity = new HashMap<Integer, Place>();

    static {
        for (final Place value : Place.values()) {
            if (value != Place.ALL_CHANGE) {
                entity.put(Integer.valueOf(value.zeroBasedPlace), value);
            }
        }
    }


    /**
     * The actual position of place being made
     */
    private final int zeroBasedPlace;
    /**
     * The regex / Pattern for identifying the place
     */
    private final String regex;
    private final Pattern extractPattern;
    /**
     * The string used for display
     */
    private final String display;

    Place(final int zeroBasedPlace, final String regex, final String display) {
        this.zeroBasedPlace = zeroBasedPlace;
        this.regex = regex;
        extractPattern = Pattern.compile("[" + regex + "]");
        this.display = display;
    }

    Place(Bell bell, final String regex) {
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
     * @return Set<Place>, with one or more NotationElements
     */
    public static Set<Place> parsePlaces(final CharSequence notationShorthand) {
        final Set<Place> matchedElements = new HashSet<>();

        for (int i = 0; i < notationShorthand.length(); i++) {
            final char notationChar = notationShorthand.charAt(i);
            final Place place = parsePlace(notationChar);
            matchedElements.add(place);
        }

        return matchedElements;
    }

    /**
     * Decode the passed single shorthand character to a single Place.
     *
     * @param notationChar
     * @return Place, or null if not found
     */
    public static Place parsePlace(final char notationChar) {
        Place matchedElemenet = null;

        final String notationString = Character.toString(notationChar);

        for (final Place element : values()) {
            final Pattern p = element.extractPattern;

            final Matcher matcher = p.matcher(notationString);
            if (matcher.matches()) {
                matchedElemenet = element;
                break;
            }
        }
        return matchedElemenet;
    }


    public static synchronized Place valueOf(final int zeroBasedPlace) {
        return entity.get(zeroBasedPlace);
    }


}