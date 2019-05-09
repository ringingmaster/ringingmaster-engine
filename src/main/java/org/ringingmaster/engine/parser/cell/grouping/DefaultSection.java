package org.ringingmaster.engine.parser.cell.grouping;

import org.ringingmaster.engine.parser.assignparsetype.ParseType;

import javax.annotation.concurrent.Immutable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
@Immutable
class DefaultSection implements Section {

    private final ElementRange elementRangeDelegate;
    private final ParseType parseType;

    /**
     * @param startIndex inclusive
     */
    DefaultSection(int startIndex, int length, ParseType parseType) {
        elementRangeDelegate = new DefaultElementRange(startIndex, length);;
        this.parseType = checkNotNull(parseType);
    }

    @Override
    public ParseType getParseType() {
        return parseType;
    }

    @Override
    public int getStartIndex() {
        return elementRangeDelegate.getStartIndex();
    }

    @Override
    public int getLength() {
        return elementRangeDelegate.getLength();
    }

    @Override
    public int getEndIndex() {
        return elementRangeDelegate.getEndIndex();
    }

    @Override
    public boolean fallsWithin(int otherIndex) {
        return elementRangeDelegate.fallsWithin(otherIndex);
    }

    @Override
    public boolean fallsWithin(ElementRange other) {
        return elementRangeDelegate.fallsWithin(other);
    }

    @Override
    public boolean intersection(int startIndex, int length) {
        return elementRangeDelegate.intersection(startIndex, length);
    }

    @Override
    public String toString() {
        return "Section{" +
                getStartIndex() +
                "/" + getLength() +
                "," + parseType +
                '}';
    }
}