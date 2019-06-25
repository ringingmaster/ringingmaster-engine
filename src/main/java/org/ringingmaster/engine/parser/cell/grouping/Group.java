package org.ringingmaster.engine.parser.cell.grouping;

import com.google.common.collect.ImmutableList;
import com.google.errorprone.annotations.Immutable;
import org.ringingmaster.engine.parser.assignparsetype.ParseType;

/**
 * A Group spans multiple sections, where each section holds a ParseType.
 * A Group must be filled, and Sections must not overlap.
 * The Group overlays validity and an optional message
 *
 *
 * @author Steve Lake
 */
@Immutable
public interface Group extends ElementRange {

    ImmutableList<Section> getSections();

    boolean isValid();

    ImmutableList<String> getMessages();

    // Helper methods

    ParseType getFirstSectionParseType();
}
