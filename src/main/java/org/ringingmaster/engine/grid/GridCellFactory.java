package org.ringingmaster.engine.grid;

/**
 * TODO comments???
 * User: Stephen
 */
public interface GridCellFactory<T> {

	T buildCell(int columnIndex, int rowIndex);
}
