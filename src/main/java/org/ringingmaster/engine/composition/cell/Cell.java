package org.ringingmaster.engine.composition.cell;

import com.google.errorprone.annotations.Immutable;
import org.ringingmaster.engine.composition.element.Element;

/**
 * TODO comments???
 *
 * @author stevelake
 */
@Immutable
public interface Cell {

    Element getElement(int index);

    int getElementSize();

    String getCharacters();
}
