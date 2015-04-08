package com.concurrentperformance.ringingmaster.engine.touch.container.impl;

import com.concurrentperformance.ringingmaster.engine.touch.container.Grid;
import com.concurrentperformance.ringingmaster.engine.touch.container.GridCellFactory;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;

import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkElementIndex;

/**
 * Default implementation of the {@link Grid} interface.
 * User: Stephen
 */
public class DefaultGrid<T> implements Grid<T> {

	private final GridCellFactory<T> factory;
	private final Table<Integer, Integer, T> cells = HashBasedTable.create();
	private int columnCount;
	private int rowCount;

	protected transient int modCount = 0;

	public DefaultGrid(GridCellFactory<T> factory, int columnCount, int rowCount) {
		this.factory = factory;
		setColumnCount(columnCount);
		setRowCount(rowCount);
	}

	@Override
	public int getColumnCount() {
		return columnCount;
	}

	@Override
	public void setColumnCount(int columnCount) {
		checkArgument(columnCount >= 0, "columnCount must be 1 or greater");
		modCount++;
		boolean addingColumns = (this.columnCount < columnCount);
		this.columnCount = columnCount;
		if (addingColumns) {
			fillInMissingCells();
		}
		else {
 			removeOutOfRangeColumns();
		}
	}

	@Override
	public void removeColumn(int colIndex) {
		checkElementIndex(colIndex, columnCount, "colIndex must be less than columnCount");
		removeColumnStructure(colIndex);
		colIndex++;
		for (;colIndex<columnCount;colIndex++) {
			for (int rowIndex=0;rowIndex<rowCount;rowIndex++) {
				T cell = getCell(colIndex, rowIndex);
				setCell(colIndex-1, rowIndex, cell);
			}
		}
		setColumnCount(columnCount-1);
	}

	@Override
	public void insertColumn(int atColIndex) {
		checkElementIndex(atColIndex, columnCount, "atColIndex must be less than rowCount");

		setColumnCount(columnCount + 1);

		for (int colIndex = columnCount-2;colIndex>=atColIndex;colIndex--) {
			for (int rowIndex=0;rowIndex<rowCount;rowIndex++) {
				T cell = getCell(colIndex, rowIndex);
				setCell(colIndex+1, rowIndex, cell);
			}
		}

		// blank out the new row.
		for (int rowIndex=0;rowIndex<rowCount;rowIndex++) {
			setCell(atColIndex, rowIndex, factory.buildCell(atColIndex, rowIndex));
		}
	}


	@Override
	public int getRowCount() {
		return rowCount;
	}

	@Override
	public void setRowCount(int rowCount) {
		checkArgument(rowCount >= 0, "rowCount must be 1 or greater");
		modCount++;
		boolean addingRows= (this.rowCount < rowCount);
		this.rowCount = rowCount;
		if (addingRows) {
			fillInMissingCells();
		}
		else {
			removeOutOfRangeRows();
		}
	}

	@Override
	public void removeRow(int rowIndex) {
		checkElementIndex(rowIndex, rowCount, "rowIndex must be less than rowCount");
		rowIndex++;
		for (;rowIndex<rowCount;rowIndex++) {
			for (int colIndex=0;colIndex<columnCount;colIndex++) {
				T cell = getCell(colIndex, rowIndex);
				setCell(colIndex, rowIndex-1, cell);
			}
		}
		setRowCount(rowCount-1);
	}

	@Override
	public void insertRow(final int atRowIndex) {
		checkElementIndex(atRowIndex, rowCount, "atRowIndex must be less than rowCount");

		setRowCount(rowCount+1);

		for (int rowIndex = rowCount-2 ;rowIndex>=atRowIndex;rowIndex--) {
			for (int colIndex=0;colIndex<columnCount;colIndex++) {
				T cell = getCell(colIndex, rowIndex);
				setCell(colIndex, rowIndex+1, cell);
			}
		}

		// blank out the new row.
		for (int colIndex=0;colIndex<columnCount;colIndex++) {
			setCell(colIndex, atRowIndex, factory.buildCell(colIndex, atRowIndex));
		}
	}

	@Override
	public void setCell(int columnIndex, int rowIndex, T cell) {
		checkValidPosition(columnIndex, rowIndex);
		cells.put(rowIndex, columnIndex, cell);
	}

	@Override
	public T getCell(int columnIndex, int rowIndex) {
		checkValidPosition(columnIndex, rowIndex);
		T cell = cells.get(rowIndex, columnIndex);
		return cell;
	}

	@Override
	public List<T> column(int columnIndex) {
		List<T> column = Lists.newArrayList();
		for (int rowIndex=0;rowIndex<rowCount;rowIndex++) {
			column.add(getCell(columnIndex, rowIndex));
		}
		return column;
	}

	@Override
	public List<T> row(int rowIndex) {
		List<T> row = Lists.newArrayList();
		for (int columnIndex=0;columnIndex<columnCount;columnIndex++) {
			row.add(getCell(columnIndex, rowIndex));
		}
		return row;
	}

	private void fillInMissingCells() {
		for (int columnIndex = 0;columnIndex<getColumnCount();columnIndex++) {
			for(int rowIndex = 0;rowIndex<getRowCount();rowIndex++) {
				if (getCell(columnIndex,rowIndex) == null) {
					T newCell = factory.buildCell(columnIndex, rowIndex);
					cells.put(rowIndex, columnIndex, newCell);
				}
			}
		}
	}

	private void removeOutOfRangeColumns() {
		HashSet<Integer> columnKeys = Sets.newHashSet(cells.columnKeySet());
		for (Integer columnKey : columnKeys) {
			if (columnKey >= getColumnCount()) {
				removeColumnStructure(columnKey);
			}
		}
	}

	private void removeColumnStructure(int columnKey) {
		HashSet<Integer> rowKeysForRemoval = Sets.newHashSet(cells.column(columnKey).keySet());
		for (Integer rowKey : rowKeysForRemoval) {
			cells.remove(rowKey, columnKey);
		}
	}

	private void removeOutOfRangeRows() {
		HashSet<Integer> rowKeys = Sets.newHashSet(cells.rowKeySet());
		for (Integer rowKey : rowKeys) {
			if (rowKey >= getRowCount()) {
				removeRow(rowKey);
			}
		}
	}

	private void removeRow(Integer rowKey) {
		HashSet<Integer> columnKeysForRemoval = Sets.newHashSet(cells.row(rowKey).keySet());
		for (Integer columnKey : columnKeysForRemoval) {
			cells.remove(rowKey, columnKey);
		}
	}

	protected void checkValidPosition(int columnIndex, int rowIndex) {
		checkElementIndex(columnIndex, getColumnCount(), "Column index");
		checkElementIndex(rowIndex, getRowCount(), "Row index");
	}

	/**
	 * @param fromColumnIndex low endpoint (inclusive) of the unmodifiableSubGrid columns
	 * @param toColumnIndex high endpoint (exclusive) of the unmodifiableSubGrid columns
	 * @param fromRowIndex low endpoint (inclusive) of the unmodifiableSubGrid columns
	 * @param toRowIndex high endpoint (exclusive) of the unmodifiableSubGrid columns
	 * @return
	 */
	@Override
	public Grid<T> unmodifiableSubGrid(int fromColumnIndex, int toColumnIndex, int fromRowIndex, int toRowIndex) {
		subGridRangeCheck(fromColumnIndex, toColumnIndex, getColumnCount());
		subGridRangeCheck(fromRowIndex, toRowIndex, getRowCount());
		return new UnmodifiableSubGrid(this, fromColumnIndex, toColumnIndex, fromRowIndex, toRowIndex);
	}

	static void subGridRangeCheck(int fromIndex, int toIndex, int size) {
		if (fromIndex < 0)
			throw new IndexOutOfBoundsException("fromIndex = " + fromIndex);
		if (toIndex > size)
			throw new IndexOutOfBoundsException("toIndex = " + toIndex);
		if (fromIndex > toIndex)
			throw new IllegalArgumentException("fromIndex(" + fromIndex +
					") > toIndex(" + toIndex + ")");
	}


	private final class UnmodifiableSubGrid implements Grid<T> {

		private final DefaultGrid<T> parent;
		private final int columnOffset;
		private final int columnCount;
		private final int rowOffset;
		private final int rowCount;
		private final int modCount;


		private UnmodifiableSubGrid(DefaultGrid<T> parent, int fromColumnIndex, int toColumnIndex, int fromRowIndex, int toRowIndex) {
			this.parent = parent;
			this.columnOffset = fromColumnIndex;
			this.columnCount = toColumnIndex - fromColumnIndex;
			this.rowOffset = fromRowIndex;
			this.rowCount = toRowIndex - fromRowIndex;
			this.modCount = DefaultGrid.this.modCount;
		}

		@Override
		public int getColumnCount() {
			return columnCount;
		}

		@Override
		public void setColumnCount(int width) {
			throw new UnsupportedOperationException("Not supported on a sub grid.");
		}

		@Override
		public void removeColumn(int colIndex) {
			throw new UnsupportedOperationException("Not supported on a sub grid.");
 		}

		@Override
		public void insertColumn(int colIndex) {
			throw new UnsupportedOperationException("Not supported on a sub grid.");
		}

		@Override
		public int getRowCount() {
			return rowCount;
		}

		@Override
		public void setRowCount(int height) {
			throw new UnsupportedOperationException("Not supported on a sub grid.");
		}

		@Override
		public void removeRow(int rowIndex) {
			throw new UnsupportedOperationException("Not supported on a sub grid.");
		}

		@Override
		public void insertRow(int rowIndex) {
			throw new UnsupportedOperationException("Not supported on a sub grid.");
		}

		@Override
		public T getCell(int columnIndex, int rowIndex) {
			checkForComodification();
			return parent.getCell(columnIndex + columnOffset, rowIndex + rowOffset);
		}

		@Override
		public void setCell(int columnIndex, int rowIndex, T cell) {
			throw new UnsupportedOperationException("Not supported on a sub grid.");
		}

		@Override
		public List<T> column(int columnIndex) {
			throw new UnsupportedOperationException("Not supported on a sub grid. Could be though.");
		}

		@Override
		public List<T> row(int rowIndex) {
			throw new UnsupportedOperationException("Not supported on a sub grid. Could be though.");
		}

		private void checkForComodification() {
			if (parent.modCount != modCount)
				throw new ConcurrentModificationException();
		}

		@Override
		public Grid<T> unmodifiableSubGrid(int columnMin, int columnMax, int rowMin, int rowMax) {
			throw new UnsupportedOperationException("Not supported on a sub grid.");
		}
		@Override
		public Iterator<T> iterator() {
			return new GridIterator<>(this);
		}

		@Override
		public String toString() {
			StringBuffer buf = new StringBuffer();
			buf.append("UnmodifiableSubGrid{size=(").append(columnCount).append(", ").append(rowCount).append(") {");
			for (int rowIndex=0;rowIndex<getRowCount();rowIndex++) {
				buf.append("{");
				for (int columnIndex=0;columnIndex<getColumnCount();columnIndex++) {
					buf.append(getCell(columnIndex, rowIndex));
					if (columnIndex != getColumnCount()-1) buf.append(", ");
				}
				buf.append("}");
			}
			buf.append("}}");
			return buf.toString();
		}
	}

	@Override
	public Iterator<T> iterator() {
		return new GridIterator<>(this);
	}

	private static class GridIterator<IT> implements Iterator<IT> {
		private final Grid<IT> grid;

		private int columnIndex = -1;
		private int rowIndex;
		private boolean valid;

		public GridIterator(Grid<IT> grid) {
			this.grid = grid;
			advance();
		}

		@Override
		public boolean hasNext() {
			return valid;
		}

		@Override
		public IT next() {
			if (!valid) {
				throw new NoSuchElementException();
			}
			IT cell = grid.getCell(columnIndex, rowIndex);
			advance();
			return cell;
		}

		private void advance() {
			valid = true;

			if (grid.getColumnCount() > 0 && grid.getRowCount() > 0) {
				// increment column
				columnIndex++;
				if (columnIndex < grid.getColumnCount()) {
					return;
				}

				// wrap column and increment row
				columnIndex = 0;
				rowIndex++;
				if (rowIndex < grid.getRowCount()) {
					return;
				}
			}

			// iterator is exhausted
			valid = false;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("DefaultTouch.iterator() does not support remove()");
		}
	}


	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("DefaultGrid{size=(").append(columnCount).append(", ").append(rowCount).append(") {");
		for (int rowIndex=0;rowIndex<getRowCount();rowIndex++) {
			buf.append("{");
			for (int columnIndex=0;columnIndex<getColumnCount();columnIndex++) {
				buf.append(cells.get(rowIndex, columnIndex));
				if (columnIndex != getColumnCount()-1) buf.append(", ");
			}
			buf.append("}");
		}
		buf.append("}}");
		return buf.toString();
	}
}
