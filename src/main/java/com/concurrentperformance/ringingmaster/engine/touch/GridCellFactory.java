package com.concurrentperformance.ringingmaster.engine.touch;

/**
 * TODO comments???
 * User: Stephen
 */
public interface GridCellFactory<T> {

	T buildCell(int columnIndex, int rowIndex);
}
