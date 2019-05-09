package org.ringingmaster.engine.arraytable;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
public class ByRowThenColumnIterator<IT> implements Iterator<BackingTableLocationAndValue<IT>> {
    private final ImmutableArrayTable<IT> immutableArrayTable;
    private int columnIndex = -1;
    private int rowIndex;
    private boolean valid;

    ByRowThenColumnIterator(ImmutableArrayTable<IT> immutableArrayTable) {
        this.immutableArrayTable = immutableArrayTable;
        advance();
    }

    @Override
    public boolean hasNext() {
        return valid;
    }

    @Override
    public BackingTableLocationAndValue<IT> next() {
        if (!valid) {
            throw new NoSuchElementException();
        }
        IT cell = immutableArrayTable.get(rowIndex, columnIndex);
        int backingRowIndex = immutableArrayTable.getBackingRowIndex(rowIndex);
        int backingColumnIndex = immutableArrayTable.getBackingColumnIndex(columnIndex);
        BackingTableLocationAndValue<IT> backingTableLocationAndValue =
                new BackingTableLocationAndValue<>(cell, backingRowIndex, backingColumnIndex);

        advance();
        return backingTableLocationAndValue;
    }

    private void advance() {
        valid = true;

        if (immutableArrayTable.getColumnSize() > 0 && immutableArrayTable.getRowSize() > 0) {
            // increment column
            columnIndex++;
            if (columnIndex < immutableArrayTable.getColumnSize()) {
                return;
            }

            // wrap column and increment row
            columnIndex = 0;
            rowIndex++;
            if (rowIndex < immutableArrayTable.getRowSize()) {
                return;
            }
        }

        // iterator is exhausted
        valid = false;
    }

}
