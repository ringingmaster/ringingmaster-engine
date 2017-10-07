package org.ringingmaster.engine.parsernew.cell;

import com.google.common.collect.ImmutableList;
import net.jcip.annotations.Immutable;

/**
 * TODO comments???
 *
 * @author stevelake
 */
@Immutable
class DefaultGroup implements Group {

    private final int elementStartIndex;
    private final int elementLength;
    private final ImmutableList<Section> sections;


    DefaultGroup(int elementStartIndex, int elementLength, Section section) {
        this.elementStartIndex = elementStartIndex;
        this.elementLength = elementLength;
        this.sections = ImmutableList.of(section);
    }

    @Override
    public ImmutableList<Section> getSections() {
        return sections;
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
    public boolean fallsWithin(int elementIndex) {
        return elementIndex >= elementStartIndex &&
                elementIndex < elementStartIndex + elementLength;
    }

    @Override
    public String toString() {
        return "DefaultGroup{" +
                "elementStartIndex=" + elementStartIndex +
                ", elementLength=" + elementLength +
                ", sections=" + sections +
                '}';
    }
}
