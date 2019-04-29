package org.ringingmaster.engine.composition.cell;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class EmptyCell implements Cell{

    public static EmptyCell INSTANCE = new EmptyCell();

    @Override
    public char get(int index) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public String getCharacters() {
        return "";
    }

    @Override
    public String toString() {
        return "EmptyCell";
    }
}
