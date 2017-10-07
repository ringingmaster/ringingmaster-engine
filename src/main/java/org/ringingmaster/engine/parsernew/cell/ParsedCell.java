package org.ringingmaster.engine.parsernew.cell;

import net.jcip.annotations.Immutable;

import java.util.Optional;

/**
 * TODO comments???
 *
 * @author stevelake
 */
@Immutable
public interface ParsedCell {

    int getElementSize();

    Optional<Section> getSectionAtElementIndex(int elementIndex);

    Optional<Group> getWordAtElementIndex(int elementIndex);

}
