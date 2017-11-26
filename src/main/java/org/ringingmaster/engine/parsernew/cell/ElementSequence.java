package org.ringingmaster.engine.parsernew.cell;

import java.util.Comparator;

public interface ElementSequence {

    Comparator<ElementSequence> BY_START_POSITION = Comparator.comparingInt(ElementSequence::getElementStartIndex);

    int getElementStartIndex();
    int getElementLength();

    boolean fallsWithin(int start);

}