package org.ringingmaster.engine.grid;

import java.util.List;

/**
 * TODO comments???
 * User: Stephen
 */
@Deprecated
public interface Grid<T> extends Iterable<T> { //TODO move elsewhere

	int getColumnCount();
	void setColumnCount(int width);
	void removeColumn(int colIndex);
	void insertColumn(int colIndex);

	int getRowCount();
	void setRowCount(int height);
	void removeRow(int rowIndex);
	void insertRow(int rowIndex);

	T getCell(int columnIndex, int rowIndex);
	void setCell(int columnIndex, int rowIndex, T cell);

	List<T> column(int columnIndex);
	List<T> row(int rowIndex);

	Grid<T> unmodifiableSubGrid(int columnMin, int columnMax, int rowMin, int rowMax);

}
