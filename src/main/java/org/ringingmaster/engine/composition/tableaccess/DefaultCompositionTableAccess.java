package org.ringingmaster.engine.composition.tableaccess;

import org.ringingmaster.engine.arraytable.ImmutableArrayTable;
import org.ringingmaster.engine.composition.cell.Cell;
import org.ringingmaster.engine.composition.compositiontype.CompositionType;

import javax.annotation.concurrent.Immutable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO comments???
 *
 * @author stevelake
 */
@Immutable
public class DefaultCompositionTableAccess<T extends Cell> implements CompositionTableAccess<T> {

    private final ImmutableArrayTable<T> cells;
    private final CompositionType compositionType;
    private final boolean isSpliced;

    public DefaultCompositionTableAccess(ImmutableArrayTable<T> cells, CompositionType compositionType, boolean isSpliced) {
        this.cells = checkNotNull(cells);
        this.compositionType = checkNotNull(compositionType);
        this.isSpliced = isSpliced;
    }

    @Override
    public ImmutableArrayTable<T> allCompositionCells() {
        return cells;
    }

    // Rule1 : Call position takes precedence over main body when not enough rows.
    // Rule2 : Main Body takes precedence over splice when not enough columns.

    @Override
    public ImmutableArrayTable<T> mainBodyCells() { //TODO pre-calculate???
        if (cells.getColumnSize() == 0 || cells.getRowSize() == 0 ) {
            return cells.subTable(0, 0, 0, 0);
        }
        if (cells.getRowSize() < 2 && compositionType == CompositionType.COURSE_BASED) {
            return cells.subTable(0, 0, 0, 0);
        }
        if (cells.getColumnSize() < 2 && isSpliced) {
            return cells.subTable(
                    (compositionType == CompositionType.COURSE_BASED ? 1:0),
                    cells.getRowSize(),
                    0,
                    cells.getColumnSize());
        }
        return cells.subTable(
                (compositionType == CompositionType.COURSE_BASED ? 1:0),
                cells.getRowSize(),
                0,
                cells.getColumnSize() - (isSpliced ? 1:0));
    }

    @Override
    public ImmutableArrayTable<T> callingPositionCells() { //TODO pre-calculate???
        if (compositionType != CompositionType.COURSE_BASED) {
            return cells.subTable(0, 0, 0, 0);
        }
        if (cells.getRowSize() == 0 ) {
            return cells.subTable(0, 0, 0, 0);
        }
        if (cells.getColumnSize() < 2 ) {
            return cells.subTable(0, 1, 0, cells.getColumnSize());
        }
        return cells.subTable(0, 1, 0, cells.getColumnSize() - (isSpliced ? 1:0));
    }

    @Override
    public ImmutableArrayTable<T> splicedCells() { //TODO pre-calculate???
        if (!isSpliced) {
            return cells.subTable(0, 0, 0, 0);
        }
        if (cells.getRowSize() < 2 && compositionType == CompositionType.COURSE_BASED) {
            return cells.subTable(0, 0, 0, 0);
        }
        if (cells.getColumnSize() < 2) {
            return cells.subTable(0, 0, 0, 0);
        }
        return cells.subTable(
                (compositionType == CompositionType.COURSE_BASED ? 1 : 0),
                cells.getRowSize(),
                cells.getColumnSize() - 1,
                cells.getColumnSize());
    }

    @Override
    public String toString() {
        return "DefaultCompositionTableAccess{" +
                "cells=" + cells +
                ", compositionType=" + compositionType +
                ", isSpliced=" + isSpliced +
                '}';
    }
}
