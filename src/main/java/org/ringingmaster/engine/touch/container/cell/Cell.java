package org.ringingmaster.engine.touch.container.cell;

import com.google.errorprone.annotations.Immutable;
import org.ringingmaster.engine.touch.container.element.Element;

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
