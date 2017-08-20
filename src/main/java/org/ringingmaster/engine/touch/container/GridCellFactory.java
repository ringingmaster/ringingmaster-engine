package org.ringingmaster.engine.touch.container;

/**
 * TODO comments???
 * User: Stephen
 */
public interface GridCellFactory<T> {

	T buildCell(int columnIndex, int rowIndex);
}
