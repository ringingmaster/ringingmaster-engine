package org.ringingmaster.engine.arraytable;

import com.google.common.collect.ImmutableTable;

/**
 * An extension to the Guava Table to act like two ArrayLists on the row and column dimensions.
 *
 * @author stevelake
 */
public interface ImmutableArrayTable<V> {

    int getRowSize();
    int getColumnSize();

    V get(int rowIndex, int columnIndex);

    /**
     * @param fromRow low endpoint (inclusive) of the subTable row
     * @param toRow high endpoint (exclusive) of the subTable row
     * @param fromColumn low endpoint (inclusive) of the subTable column
     * @param toColumn high endpoint (exclusive) of the subTable column
     */
    ImmutableArrayTable<V> subTable(int fromRow, int toRow, int fromColumn, int toColumn);

    /**
     * Gets the underlying Table if there is one.
     */
    ImmutableTable<Integer, Integer, V> getBackingTable();
}
