package org.ringingmaster.engine.parsernew.cell;

import com.google.common.collect.ImmutableList;
import com.google.errorprone.annotations.Immutable;

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
}
