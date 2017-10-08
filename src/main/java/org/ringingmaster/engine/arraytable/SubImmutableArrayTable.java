package org.ringingmaster.engine.arraytable;

import com.google.common.collect.ImmutableTable;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkElementIndex;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * An implementation of ImmutableArrayTabler that acts much like List.subList()
 *
 * @author stevelake
 */
public class SubImmutableArrayTable<V> implements ImmutableArrayTable<V> {

    private final ImmutableArrayTable<V> delegate;
    private final int rowOffset;
    private final int rowSize;

    private final int columnOffset;
    private final int columnSize;

    SubImmutableArrayTable(ImmutableArrayTable<V> delegate, int fromRow, int toRow, int fromColumn, int toColumn) {
        this.delegate = checkNotNull(delegate);

        checkElementIndex(fromRow, delegate.getRowSize(), "FromRow");
        checkElementIndex(fromColumn, delegate.getColumnSize(), "FromCol");

        checkElementIndex(toRow, delegate.getRowSize(), "ToRow");
        checkElementIndex(toColumn, delegate.getColumnSize(), "ToCol");

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
    public V get(int rowIndex, int columnIndex) {
        checkElementIndex(rowIndex, rowSize, "Row");
        checkElementIndex(columnIndex, columnSize, "Col");

        return delegate.get(rowIndex + rowOffset, columnIndex + columnOffset);
    }

    @Override
    public ImmutableArrayTable<V> subTable(int fromRow, int toRow, int fromColumn, int toColumn) {
        return new SubImmutableArrayTable<V>(this, fromRow, toRow, fromColumn, toColumn);
    }

    @Override
    public ImmutableTable<Integer, Integer, V> getBackingTable() {
        return delegate.getBackingTable();
    }
}
