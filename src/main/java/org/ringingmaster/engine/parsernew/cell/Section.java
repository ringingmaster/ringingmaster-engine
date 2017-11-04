package org.ringingmaster.engine.parsernew.cell;

import org.ringingmaster.engine.parser.ParseType;

/**
 * A Section represents a contiguous sequence of elements that have an assigned ParseType
 *
 * @author stevelake
 */
public interface Section {

    ParseType getParseType();

    int getElementStartIndex();
    int getElementLength();

    boolean fallsWithin(int start);
}
