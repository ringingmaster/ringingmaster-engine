package org.ringingmaster.engine; //TODO where should this live?

import org.ringingmaster.engine.method.Bell;
import org.ringingmaster.engine.notation.Place;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;


/**
 * Represents the number of bells in a notation / method type.
 *
 * @author Steve Lake
 */
public enum NumberOfBells implements Iterable<Place>, Comparable<NumberOfBells> {

    BELLS_3(3, "Singles"),
    BELLS_4(4, "Minimus"),
    BELLS_5(5, "Doubles"),
    BELLS_6(6, "Minor"),
    BELLS_7(7, "Triples"),
    BELLS_8(8, "Major"),
    BELLS_9(9, "Caters"),
    BELLS_10(10, "Royal"),
    BELLS_11(11, "Cinques"),
    BELLS_12(12, "Maximus"),
    BELLS_13(13, "Sextuples"),
    BELLS_14(14, "Fourteen"),
    BELLS_15(15, "Septuples"),
    BELLS_16(16, "Sixteen"),
    BELLS_17(17, "Octuples"),
    BELLS_18(18, "Eighteen"),
    BELLS_19(19, "Ninteen"),
    BELLS_20(20, "Twenty"),
    BELLS_21(21, "Twenty-one"),
    BELLS_22(22, "Twenty-two"),
    BELLS_23(23, "Twenty-three"),
    BELLS_24(24, "Twenty-four"),
    BELLS_25(25, "Twenty-five"),
    BELLS_26(26, "Twenty-six"),
    BELLS_27(27, "Twenty-seven"),
    BELLS_28(28, "Twenty-eight"),
    BELLS_29(29, "Twenty-nine"),
    BELLS_30(30, "Thirty");

    public static final NumberOfBells MAX = BELLS_30;

    private static Map<Integer, NumberOfBells> entity = new HashMap<>();

    static {
        for (final NumberOfBells value : NumberOfBells.values()) {
            entity.put(value.bellCount, value);
        }
    }

    private final String name;
    private final String displayString;
    private final int bellCount;
    private final Bell tenor;
    private final Place tenorPlace;

    NumberOfBells(final int bellCount, final String name) {
        checkArgument(bellCount > 0);
        this.name = name;
        checkArgument(name.length() > 0);
        this.bellCount = bellCount;
        this.tenor = Bell.valueOf(bellCount - 1);
        this.tenorPlace = Place.valueOf(bellCount - 1);
        this.displayString = name + " (" + bellCount + ")";
    }

    /**
     * Get the 1 based integer number of bells. e.g. For BELLS_8 return 8
     *
     * @return int
     */

    @Deprecated //TODO should this be replaced with a compareto?
    public int toInt() {
        return bellCount;
    }

    public Bell getTenor() {
        return tenor;
    }

    public Place getTenorPlace() {
        return tenorPlace;
    }

    public String getName() {
        return name;
    }

    public String getDisplayString() {
        return displayString;
    }

    public boolean isEven() {
        return (bellCount % 2) == 0;
    }

    public static NumberOfBells valueOf(final int bellCount) {
        return entity.get(bellCount);
    }

    @Override
    public Iterator<Place> iterator() {
        return new Iterator<>() {

            int notationPlaceIndex = 0;

            @Override
            public boolean hasNext() {
                return notationPlaceIndex < bellCount;
            }

            @Override
            public Place next() {
                return Place.valueOf(notationPlaceIndex++);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("NumberOfBells.iterator() does not support remove()");
            }
        };
    }

    @Override
    public String toString() {
        return getDisplayString();
    }
}
