package org.ringingmaster.engine.parser.cell.grouping;

import com.google.common.collect.ImmutableList;
import com.google.errorprone.annotations.Immutable;
import org.ringingmaster.engine.parser.assignparsetype.ParseType;

import java.util.Optional;

/**
 * A Group spans multiple sections, where each section holds a ParseType.
 * A Group must be filled, and Sections must not overlap.
 * The Group overlays validity and an optional message
 *
 *
 * @author stevelake
 */
@Immutable
public interface Group extends ElementRange {

    ImmutableList<Section> getSections();

    boolean isValid();

    Optional<String> getMessage();

    // Helper methods

    ParseType getFirstSectionParseType();
}
