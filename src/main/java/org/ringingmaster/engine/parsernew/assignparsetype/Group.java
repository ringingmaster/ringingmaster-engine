package org.ringingmaster.engine.parsernew.assignparsetype;

import java.util.List;

/**
 * A Group spans multiple sections.
 *
 * @author stevelake
 */
public interface Group {

    List<Section> getSections();

    int getElementStartIndex();
    int getElementLength();

    boolean fallsWithin(int start);

}
