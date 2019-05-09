package org.ringingmaster.engine.parser.cell.grouping;


import com.google.errorprone.annotations.Immutable;
import org.ringingmaster.engine.parser.assignparsetype.ParseType;

/**
 * A Section represents a contiguous sequence of elements that have an assigned ParseType
 *
 * @author Steve Lake
 */
@Immutable
public interface Section extends ElementRange {

    ParseType getParseType();
}
