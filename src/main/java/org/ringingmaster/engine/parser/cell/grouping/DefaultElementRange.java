package org.ringingmaster.engine.parser.cell.grouping;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
class DefaultElementRange implements ElementRange {

    private final int startIndex;
    private final int length;

    /**
     * @param startIndex inclusive
     */
    DefaultElementRange(int startIndex, int length) {
        checkArgument(startIndex >= 0);
        checkArgument(length > 0);

        this.startIndex = startIndex;
        this.length = length;
    }

    @Override
    public int getStartIndex() {
        return startIndex;
    }

    @Override
    public int getLength() {
        return length;
    }

    @Override
    public int getEndIndex() {
        return startIndex + length;
    }

    @Override
    public boolean fallsWithin(int otherIndex) {
        return otherIndex >= startIndex &&
                otherIndex < (startIndex + length);
    }

    @Override
    public boolean fallsWithin(ElementRange other) {
        return fallsWithin(other.getStartIndex()) && fallsWithin(other.getEndIndex()-1);
    }

    @Override
    public boolean intersection(int startIndex, int length) {
        return !((this.startIndex                >= (startIndex + length    )) ||
                 (this.startIndex + this.length) <= (startIndex));

    }

    @Override
    public String toString() {
        return "ElementRange{" +
                "location=" + startIndex +
                "/" + length +
                '}';
    }

}
