package org.ringingmaster.engine.parsernew.assignparsetype;

import org.ringingmaster.engine.parser.ParseType;

/**
 * A Section represents a contiguous sequence of elements that have a n assigned ParseType
 *
 * @author stevelake
 */
public interface Section {

    ParseType getParseType();

    int getElementStartIndex();
    int getElementLength();

    boolean fallsWithin(int start);
}
