package org.ringingmaster.engine.arraytable;

import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;

import java.util.function.Supplier;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkPositionIndex;

/**
 * @author stevelake
 */
public class TableBackedImmutableArrayTable<V> implements ImmutableArrayTable<V> {

    private final ImmutableTable<Integer, Integer, V> backingTable;
    private final int columnSize;
    private final int rowSize;
    private final Supplier<V> emptyCellSupplier;


    public TableBackedImmutableArrayTable(Supplier<V> emptyCellSupplier) {
        this(ImmutableTable.of(), emptyCellSupplier);
    }

    public TableBackedImmutableArrayTable(Table<Integer, Integer, V> backingTable, Supplier<V> emptyCellSupplier) {
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
    public V get(int rowIndex, int columnIndex) {
        checkPositionIndex(rowIndex, rowSize);
        checkPositionIndex(columnIndex, columnSize);

        V cell = backingTable.get(rowIndex, columnIndex);
        if (cell != null) {
            return cell;
        }
        else {
            return emptyCellSupplier.get();
        }
    }

    @Override
    public ImmutableArrayTable<V> subTable(int fromRow, int toRow, int fromColumn, int toColumn) {
        return new SubImmutableArrayTable<V>(this, fromRow, toRow, fromColumn, toColumn);
    }

    @Override
    public ImmutableTable<Integer, Integer, V> getBackingTable() {
        return backingTable;
    }
}
