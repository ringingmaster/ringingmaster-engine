package org.ringingmaster.engine.touch.container.tableaccess;

import org.ringingmaster.engine.arraytable.ImmutableArrayTable;
import org.ringingmaster.engine.touch.container.cell.Cell;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public interface TouchTableAccess<T extends Cell> {

    ImmutableArrayTable<T> allTouchCells();
    ImmutableArrayTable<T> mainBodyCells();
    ImmutableArrayTable<T> callPositionCells();
    ImmutableArrayTable<T> splicedCells();
}
