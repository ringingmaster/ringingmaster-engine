package org.ringingmaster.engine.grid;

/**
 * TODO comments???
 * User: Stephen
 */
@Deprecated
public interface GridCellFactory<T> {

	T buildCell(int columnIndex, int rowIndex);
}
