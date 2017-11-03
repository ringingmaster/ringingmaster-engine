package org.ringingmaster.engine.touch.newcontainer.cellmanipulation;

import org.ringingmaster.engine.arraytable.ImmutableArrayTable;
import org.ringingmaster.engine.touch.newcontainer.cell.Cell;
import org.ringingmaster.engine.touch.newcontainer.checkingtype.CheckingType;

import javax.annotation.concurrent.Immutable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO comments???
 *
 * @author stevelake
 */
@Immutable
public class CellManipulation<T extends Cell> {

    private final ImmutableArrayTable<T> cells;
    private final CheckingType checkingType;
    private final boolean isSpliced;

    public CellManipulation(ImmutableArrayTable<T> cells, CheckingType checkingType, boolean isSpliced) {
        this.cells = checkNotNull(cells);
        this.checkingType = checkNotNull(checkingType);
        this.isSpliced = isSpliced;
    }

    public ImmutableArrayTable<T> allCells() {
        return cells;
    }

    public ImmutableArrayTable<T> mainBodyCells() { //TODO pre-calculate???
        return cells.subTable(
                ((checkingType == CheckingType.COURSE_BASED && cells.getRowSize() > 1) ? 1 : 0),
                cells.getRowSize(),
                0,
                ((isSpliced && cells.getColumnSize() > 1) ? (cells.getColumnSize() - 1) : cells.getColumnSize()));
    }

    public ImmutableArrayTable<T> callPositionCells() { //TODO pre-calculate???
        if (checkingType != CheckingType.COURSE_BASED) {
            return cells.subTable(0, 0, 0, 0);
        }
        if (cells.getRowSize() < 2) {
            return cells.subTable(0, 0, 0, 0);
        }
        return cells.subTable(
                0,
                1,
                0,
                cells.getColumnSize() - (isSpliced ? 1:0));
    }

    public ImmutableArrayTable<T> splicedCells() { //TODO pre-calculate???
        if (!isSpliced) {
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
}
