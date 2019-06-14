package org.ringingmaster.engine.composition.cell;

import javax.annotation.concurrent.Immutable;
import java.util.Objects;

/**
 * TODO Comments
 *
 * @author Steve Lake
 */
@Immutable
class DefaultCell implements Cell {

    private final String characters;

    public DefaultCell(String characters) {
        this.characters = characters;
    }

    @Override
    public char get(int index) {
        return characters.charAt(index);
    }

    @Override
    public int size() {
        return characters.length();
    }

    @Override
    public String getCharacters() {
        return characters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DefaultCell)) return false;
        DefaultCell that = (DefaultCell) o;
        return Objects.equals(getCharacters(), that.getCharacters());
    }

    @Override
    public int hashCode() {
        return getCharacters().hashCode();
    }

    @Override
    public String toString() {
        return "Cell{" +
                "elements=" + getCharacters() +
                '}';
    }
}
