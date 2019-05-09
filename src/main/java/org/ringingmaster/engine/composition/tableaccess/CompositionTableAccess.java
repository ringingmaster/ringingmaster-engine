package org.ringingmaster.engine.composition.tableaccess;

import org.ringingmaster.engine.arraytable.ImmutableArrayTable;
import org.ringingmaster.engine.composition.cell.Cell;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
public interface CompositionTableAccess<T extends Cell> {

    ImmutableArrayTable<T> allCompositionCells();
    ImmutableArrayTable<T> mainBodyCells();
    ImmutableArrayTable<T> callingPositionCells();
    ImmutableArrayTable<T> splicedCells();
}
