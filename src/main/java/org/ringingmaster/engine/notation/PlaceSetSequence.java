package org.ringingmaster.engine.notation;

import com.google.common.collect.ComparisonChain;
import org.ringingmaster.engine.NumberOfBells;

import javax.annotation.concurrent.Immutable;
import java.util.Comparator;

/**
 * Base interface for all things that contain sequences of PlaceSet's.
 * <p>
 * i.e. if the full notation or call was 'X.15', then this would represent 'X.15'
 * <br/>
 *
 * User: Stephen
 */
@Immutable
public interface PlaceSetSequence extends Iterable<PlaceSet> {

    String ROW_SEPARATOR = ".";

    Comparator<PlaceSetSequence> BY_NAME = (o1, o2) -> ComparisonChain.start()
            .compare(o1.getName(), o2.getName())
            .result();

    Comparator<PlaceSetSequence> BY_NUMBER_THEN_NAME = (o1, o2) -> ComparisonChain.start()
            .compare(o1.getNumberOfWorkingBells(), o2.getNumberOfWorkingBells())
            .compare(o1.getName(), o2.getName())
            .result();

    /**
     * Get the name of the method, excluding the Number of bells
     * name. i.e. Plain Bob.
     */
    String getName();

    /**
     * Get the number of bells the method is working over, excluding any
     * covering bells.
     *
     * @return int, Number of bells
     */
    NumberOfBells getNumberOfWorkingBells();

    /**
     * Get the name of the method, including the Number of bells
     * name. i.e. Major in Plain Bob major.
     */
    String getNameIncludingNumberOfBells();

    /**
     * Get the number of rows in this notations plain course. Where the
     * notation is folded palindrome symmetry, this returns the total unfolded number of rows.
     * <p>
     * 'X.14.X' will return 3
     * 'X.14.X.14 le:14 will return 8
     *
     * @return int, number of rows in the notation
     */
    int size();

    /**
     * Get the PlaceSet at the index position. This is a rows worth
     * of Place's.
     *
     * @param index the row number of the notation to get.
     * @return PlaceSet, all the elements for a row
     * @throws IndexOutOfBoundsException if the index is greater than the number of rows
     */
    PlaceSet get(int index);

    /**
     * Get the notation as a normalized string. By normalized, we mean that
     * all the places, and all change are capitalized, and the separator dots are
     * applied consistently. When the boolean 'concise' is set to true, it
     * will produce a consise version of the string
     * i.e.
     * concise =  -12-1T.12,12
     * not concise =  -.12.-.1T.12,12
     *
     * @param concise
     * @return
     */
    String getNotationDisplayString(boolean concise);


}
