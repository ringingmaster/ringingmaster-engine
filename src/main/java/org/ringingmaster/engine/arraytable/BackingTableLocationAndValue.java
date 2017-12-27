package org.ringingmaster.engine.arraytable;

import javax.annotation.concurrent.Immutable;

/**
 * TODO comments???
 *
 * @author stevelake
 */
@Immutable
public class BackingTableLocationAndValue<T> {

    private final T value;
    private final int row;
    private final int col;


    public BackingTableLocationAndValue(T value, int row, int col) {
        this.value = value;
        this.row = row;
        this.col = col;
    }

    public T getValue() {
        return value;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    @Override
    public String toString() {
        return "BackingTableLocationAndValue{" +
                "value=" + value +
                ", row,col=" + row + "," + col +
                '}';
    }
}
