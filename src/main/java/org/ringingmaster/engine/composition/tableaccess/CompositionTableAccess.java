package org.ringingmaster.engine.composition.tableaccess;

import org.ringingmaster.engine.arraytable.ImmutableArrayTable;
import org.ringingmaster.engine.composition.cell.Cell;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public interface CompositionTableAccess<T extends Cell> {

    ImmutableArrayTable<T> allCompositionCells();
    ImmutableArrayTable<T> mainBodyCells();
    ImmutableArrayTable<T> callPositionCells();
    ImmutableArrayTable<T> splicedCells();
}
