package org.ringingmaster.engine.parsernew.cell;

import net.jcip.annotations.Immutable;
import org.ringingmaster.engine.parser.ParseType;

/**
 * TODO comments???
 *
 * @author stevelake
 */
@Immutable
class DefaultSection implements Section {

    private final int elementStartIndex;
    private final int elementLength;
    private final ParseType parseType;

    DefaultSection(int elementStartIndex, int elementLength, ParseType parseType) {
        this.elementStartIndex = elementStartIndex;
        this.elementLength = elementLength;
        this.parseType = parseType;
    }

    @Override
    public int getElementStartIndex() {
        return elementStartIndex;
    }

    @Override
    public int getElementLength() {
        return elementLength;
    }

    @Override
    public ParseType getParseType() {
        return parseType;
    }

    @Override
    public boolean fallsWithin(int elementIndex) {
        return elementIndex >= elementStartIndex &&
                elementIndex < elementStartIndex + elementLength;
    }

    @Override
    public String toString() {
        return "DefaultSection{" +
                "elementStartIndex=" + elementStartIndex +
                ", elementLength=" + elementLength +
                ", parseType=" + parseType +
                '}';
    }
}