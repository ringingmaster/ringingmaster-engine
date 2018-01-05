package org.ringingmaster.engine.parser.cell;

import java.util.Comparator;

public interface ElementSequence {

    Comparator<ElementSequence> BY_START_INDEX = Comparator.comparingInt(ElementSequence::getElementStartIndex);

    int getElementStartIndex();
    int getElementLength();

    boolean fallsWithin(int start);

}
