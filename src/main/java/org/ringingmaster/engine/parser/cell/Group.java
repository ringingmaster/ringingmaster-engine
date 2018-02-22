package org.ringingmaster.engine.parser.cell;

import com.google.common.collect.ImmutableList;
import com.google.errorprone.annotations.Immutable;
import org.ringingmaster.engine.parser.assignparsetype.ParseType;

import java.util.Optional;

/**
 * A Group spans multiple sections, where each section holds a ParseType.
 *
 * @author stevelake
 */
@Immutable
public interface Group extends ElementSequence {

    ImmutableList<Section> getSections();

    boolean isValid();

    Optional<String> getMessage();

    // Helper methods

    ParseType getFirstSectionParseType();
}
