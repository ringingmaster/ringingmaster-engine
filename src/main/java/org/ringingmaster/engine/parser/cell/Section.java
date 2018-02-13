package org.ringingmaster.engine.parser.cell;


import com.google.errorprone.annotations.Immutable;
import org.ringingmaster.engine.parser.assignparsetype.ParseType;

/**
 * A Section represents a contiguous sequence of elements that have an assigned ParseType
 *
 * @author stevelake
 */
@Immutable
public interface Section extends ElementSequence {

    ParseType getParseType();
}
