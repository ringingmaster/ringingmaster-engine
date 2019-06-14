package org.ringingmaster.engine.arraytable;

import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.Supplier;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkElementIndex;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Steve Lake
 */
public class TableBackedImmutableArrayTable<T> implements ImmutableArrayTable<T> {

    private final ImmutableTable<Integer, Integer, T> backingTable;
    private final int columnSize;
    private final int rowSize;
    private final Supplier<T> emptyCellSupplier;


    public TableBackedImmutableArrayTable(Supplier<T> emptyCellSupplier) {
        this(ImmutableTable.of(), emptyCellSupplier);
    }

    public TableBackedImmutableArrayTable(Table<Integer, Integer, T> backingTable, Supplier<T> emptyCellSupplier) {
        this.backingTable = ImmutableTable.copyOf(checkNotNull(backingTable));

        checkArgument(backingTable.columnKeySet().stream()
                .mapToInt(value -> value+1)
                .min()
                .orElse(0) >= 0,
                "Column values must be greater than or equal to zero");
        checkArgument(backingTable.rowKeySet().stream()
                .mapToInt(value -> value+1)
                .min()
                .orElse(0) >= 0,
                "Row values must be greater than or equal to zero");

        this.columnSize = backingTable.columnKeySet().stream()
                .mapToInt(value -> value+1)
                .max()
                .orElse(0);
        this.rowSize = backingTable.rowKeySet().stream()
                .mapToInt(value -> value+1)
                .max()
                .orElse(0);

        this.emptyCellSupplier = checkNotNull(emptyCellSupplier);
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

        T cell = backingTable.get(rowIndex, columnIndex);
        if (cell != null) {
            return cell;
        }
        else {
            return emptyCellSupplier.get();
        }
    }

    @Override
    public Iterator<BackingTableLocationAndValue<T>> iterator() {
        return iterateByRowThenColumn();
    }

    @Override
    public ImmutableArrayTable<T> subTable(int fromRow, int toRow, int fromColumn, int toColumn) {
        return new SubImmutableArrayTable<T>(this, fromRow, toRow, fromColumn, toColumn);
    }

    @Override
    public ImmutableTable<Integer, Integer, T> getBackingTable() {
        return backingTable;
    }

    @Override
    public Iterator<BackingTableLocationAndValue<T>> iterateByRowThenColumn() {
        return new ByRowThenColumnIterator<T>(this);
    }

    @Override
    public int getBackingRowIndex(int rowIndex) {
        return rowIndex;
    }

    @Override
    public int getBackingColumnIndex(int columnIndex) {
        return columnIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TableBackedImmutableArrayTable)) return false;
        TableBackedImmutableArrayTable<?> that = (TableBackedImmutableArrayTable<?>) o;
        return getColumnSize() == that.getColumnSize() &&
                getRowSize() == that.getRowSize() &&
                Objects.equals(getBackingTable(), that.getBackingTable());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBackingTable(), getColumnSize(), getRowSize());
    }

    @Override
    public String toString() {
        return "TableBackedImmutableArrayTable{" +
                "backingTable=" + backingTable +
                ", columnSize=" + columnSize +
                ", rowSize=" + rowSize +
                '}';
    }
}
