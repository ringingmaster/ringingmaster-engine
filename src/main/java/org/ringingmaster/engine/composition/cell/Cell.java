package org.ringingmaster.engine.composition.cell;

import com.google.errorprone.annotations.Immutable;

/**
 * TODO comments???
 *
 * @author stevelake
 */
@Immutable
public interface Cell {

    char getElement(int index);

    int getElementSize();

    String getCharacters();
}
