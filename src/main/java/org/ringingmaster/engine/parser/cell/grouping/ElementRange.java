package org.ringingmaster.engine.parser.cell.grouping;

import java.util.Comparator;

/**
 * Represents a range of indexes by means of a start index and length. 2,3,4   = Start==2, length==3
 */
public interface ElementRange {

    Comparator<ElementRange> BY_START_INDEX = Comparator.comparingInt(ElementRange::getStartIndex);

    /**
     * Start index inclusive
     */
    int getStartIndex();

    /**
     * Length of the range
     */
    int getLength();

    /**
     * End index exclusive
     */
    int getEndIndex();

    /**
     * Does the passed index fall within the bounds of this sequence?
     *
     * Passing 2 into a sequence that spans 1-3 will return true
     * Passing 2 into a sequence that spans 3-3 will return false
     */
    boolean fallsWithin(int otherIndex);

    /**
     * Does the passed element range fall entirely within the bounds of this sequence?
     *
     * Passing 2 into a sequence that spans 1-3 will return true
     * Passing 2 into a sequence that spans 3-3 will return false
     */
    boolean fallsWithin(ElementRange other);

    /**
     * Does any part of the sequence fall within the bounds of the passed lower bound and length?
     *
     * Passing 1-3 into a sequence that spans 3-4 will return true
     * Passing 1-5 into a sequence that spans 2-2 will return true
     */
    boolean intersection(int startIndex, int length);

}
