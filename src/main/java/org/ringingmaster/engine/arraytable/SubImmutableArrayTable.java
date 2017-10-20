package org.ringingmaster.engine.arraytable;

import com.google.common.collect.ImmutableTable;

import java.util.Iterator;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkElementIndex;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkPositionIndex;

/**
 * An implementation of ImmutableArrayTabler that acts much like List.subList()
 *
 * @author stevelake
 */
public class SubImmutableArrayTable<T> implements ImmutableArrayTable<T> {

    private final ImmutableArrayTable<T> delegate;
    private final int rowOffset;
    private final int rowSize;

    private final int columnOffset;
    private final int columnSize;

    SubImmutableArrayTable(ImmutableArrayTable<T> delegate, int fromRow, int toRow, int fromColumn, int toColumn) {
        this.delegate = checkNotNull(delegate);

        checkElementIndex(fromRow, delegate.getRowSize(), "FromRow");
        checkElementIndex(fromColumn, delegate.getColumnSize(), "FromCol");

        checkPositionIndex(toRow, delegate.getRowSize(), "ToRow");
        checkPositionIndex(toColumn, delegate.getColumnSize(), "ToCol");

        checkArgument(toRow >= fromRow);
        checkArgument(toColumn >= fromColumn);

        this.rowOffset = fromRow;
        this.columnOffset = fromColumn;

        this.rowSize = toRow - fromRow;
        this.columnSize = toColumn - fromColumn;
    }

    @Override
    public int getRowSize() {
        return rowSize;
    }

    @Override
    public int getColumnSize() {
        return columnSize;
    }

    @Override
    public T get(int rowIndex, int columnIndex) {
        checkElementIndex(rowIndex, rowSize, "Row");
        checkElementIndex(columnIndex, columnSize, "Col");

        return delegate.get(rowIndex + rowOffset, columnIndex + columnOffset);
    }

    @Override
    public ImmutableArrayTable<T> subTable(int fromRow, int toRow, int fromColumn, int toColumn) {
        return new SubImmutableArrayTable<T>(this, fromRow, toRow, fromColumn, toColumn);
    }

    @Override
    public Iterator<BackingTableLocationAndValue<T>> iterator() {
        return iterateByRowThenColumn();
    }

    @Override
    public ImmutableTable<Integer, Integer, T> getBackingTable() {
        return delegate.getBackingTable();
    }

    @Override
    public Iterator<BackingTableLocationAndValue<T>> iterateByRowThenColumn() {
        return new ByRowThenColumnIterator<T>(this);
    }

    @Override
    public int getBackingRowIndex(int rowIndex) {
        return delegate.getBackingRowIndex(rowIndex + rowOffset);
    }

    @Override
    public int getBackingColumnIndex(int columnIndex) {
        return delegate.getBackingColumnIndex(columnIndex + columnOffset);
    }
}
