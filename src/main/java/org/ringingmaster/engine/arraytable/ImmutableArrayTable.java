package org.ringingmaster.engine.arraytable;

import com.google.common.collect.ImmutableTable;

import java.util.Iterator;

/**
 * An extension to the Guava Table to act like two fully populated ArrayLists on the
 * row and column dimensions.
 *
 * @author stevelake
 */
public interface ImmutableArrayTable<T> extends Iterable<BackingTableLocationAndValue<T>> {

    int getRowSize();
    int getColumnSize();

    T get(int rowIndex, int columnIndex);

    /**
     * @param fromRow low endpoint (inclusive) of the subTable row
     * @param toRow high endpoint (exclusive) of the subTable row
     * @param fromColumn low endpoint (inclusive) of the subTable column
     * @param toColumn high endpoint (exclusive) of the subTable column
     */
    ImmutableArrayTable<T> subTable(int fromRow, int toRow, int fromColumn, int toColumn);

    /**
     * Gets the underlying Table if there is one.
     */
    ImmutableTable<Integer, Integer, T> getBackingTable();

    Iterator<BackingTableLocationAndValue<T>> iterateByRowThenColumn();

    int getBackingRowIndex(int rowIndex);

    int getBackingColumnIndex(int columnIndex);
}
