package org.ringingmaster.engine.touch.tableaccess;

import org.ringingmaster.engine.arraytable.ImmutableArrayTable;
import org.ringingmaster.engine.touch.cell.Cell;
import org.ringingmaster.engine.touch.checkingtype.CheckingType;

import javax.annotation.concurrent.Immutable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO comments???
 *
 * @author stevelake
 */
@Immutable
public class DefaultTouchTableAccess<T extends Cell> implements TouchTableAccess<T> {

    private final ImmutableArrayTable<T> cells;
    private final CheckingType checkingType;
    private final boolean isSpliced;

    public DefaultTouchTableAccess(ImmutableArrayTable<T> cells, CheckingType checkingType, boolean isSpliced) {
        this.cells = checkNotNull(cells);
        this.checkingType = checkNotNull(checkingType);
        this.isSpliced = isSpliced;
    }

    @Override
    public ImmutableArrayTable<T> allTouchCells() {
        return cells;
    }

    // Rule1 : Call position takes precedence over main body when not enough rows.
    // Rule2 : Main Body takes precedence over splice when not enough columns.

    @Override
    public ImmutableArrayTable<T> mainBodyCells() { //TODO pre-calculate???
        if (cells.getColumnSize() == 0 || cells.getRowSize() == 0 ) {
            return cells.subTable(0, 0, 0, 0);
        }
        if (cells.getRowSize() < 2 && checkingType == CheckingType.COURSE_BASED) {
            return cells.subTable(0, 0, 0, 0);
        }
        if (cells.getColumnSize() < 2 && isSpliced) {
            return cells.subTable(
                    (checkingType == CheckingType.COURSE_BASED ? 1:0),
                    cells.getRowSize(),
                    0,
                    cells.getColumnSize());
        }
        return cells.subTable(
                (checkingType == CheckingType.COURSE_BASED ? 1:0),
                cells.getRowSize(),
                0,
                cells.getColumnSize() - (isSpliced ? 1:0));
    }

    @Override
    public ImmutableArrayTable<T> callPositionCells() { //TODO pre-calculate???
        if (checkingType != CheckingType.COURSE_BASED) {
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
        if (cells.getRowSize() < 2 && checkingType == CheckingType.COURSE_BASED) {
            return cells.subTable(0, 0, 0, 0);
        }
        if (cells.getColumnSize() < 2) {
            return cells.subTable(0, 0, 0, 0);
        }
        return cells.subTable(
                (checkingType == CheckingType.COURSE_BASED ? 1 : 0),
                cells.getRowSize(),
                cells.getColumnSize() - 1,
                cells.getColumnSize());
    }

    @Override
    public String toString() {
        return "DefaultTouchTableAccess{" +
                "cells=" + cells +
                ", checkingType=" + checkingType +
                ", isSpliced=" + isSpliced +
                '}';
    }
}
