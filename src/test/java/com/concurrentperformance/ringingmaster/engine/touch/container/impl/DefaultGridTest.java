package com.concurrentperformance.ringingmaster.engine.touch.container.impl;

import org.junit.Test;

import com.concurrentperformance.ringingmaster.engine.touch.container.Grid;
import com.concurrentperformance.ringingmaster.engine.touch.container.GridCellFactory;

import static org.junit.Assert.assertEquals;

/**
 * User: Stephen
 */
public class DefaultGridTest {

	public static final int FACTORY_DEFAULT = 0;
	GridCellFactory<Integer> factory = new GridCellFactory<Integer>() {
		@Override
		public Integer buildCell(int columnIndex, int rowIndex) {
			return FACTORY_DEFAULT;
		}
	};

	@Test
	public void canAddAndRetrieveFromCorrectLocation() {
		DefaultGrid<Integer> grid = new DefaultGrid<Integer>(factory, 2, 2);
		grid.setCell(1, 0, Integer.valueOf(2));
		grid.setCell(1, 1, Integer.valueOf(11));
		assertEquals(Integer.valueOf(2), grid.getCell(1, 0));
		assertEquals(Integer.valueOf(11), grid.getCell(1,1));
	}

	@Test
	public void canResizeLarger() {
		DefaultGrid<Integer> grid = new DefaultGrid<Integer>(factory, 2, 2);
		grid.setCell(1, 1, Integer.valueOf(11));
		grid.setRowCount(3);
		grid.setColumnCount(3);
		grid.setCell(2, 2, Integer.valueOf(22));
		assertEquals(Integer.valueOf(11), grid.getCell(1, 1));
		assertEquals(Integer.valueOf(22), grid.getCell(2, 2));
	}

	@Test
	public void canResizeSmaller() {
		DefaultGrid<Integer> grid = new DefaultGrid<Integer>(factory, 2, 2);
		grid.setCell(0, 0, Integer.valueOf(11));
		grid.setRowCount(1);
		grid.setColumnCount(1);
		assertEquals(Integer.valueOf(11), grid.getCell(0, 0));
	}

	@Test
	public void canGetSubGrid() {
		DefaultGrid<Integer> grid = new DefaultGrid<Integer>(factory, 3, 3);
		grid.setCell(2, 0, Integer.valueOf(20));
		grid.setCell(2, 1, Integer.valueOf(21));
		grid.setCell(2, 2, Integer.valueOf(22));
		Grid<Integer> sub = grid.unmodifiableSubGrid(2, 3, 1, 3);
		assertEquals(1,sub.getColumnCount());
		assertEquals(2,sub.getRowCount());
		assertEquals(Integer.valueOf(21), sub.getCell(0, 0));
		assertEquals(Integer.valueOf(22), sub.getCell(0,1));
	}

	@Test
	public void canIterate() {
		DefaultGrid<Integer> grid = new DefaultGrid<Integer>(factory, 2, 3);
		grid.setCell(0, 0, Integer.valueOf(1));
		grid.setCell(1, 0, Integer.valueOf(2));
		grid.setCell(0, 1, Integer.valueOf(3));
		grid.setCell(1, 1, Integer.valueOf(4));
		grid.setCell(0, 2, Integer.valueOf(5));
		grid.setCell(1, 2, Integer.valueOf(6));

		String result = "";
		for (Integer integer : grid) {
			result += integer.toString();
		}

		assertEquals("123456", result);
	}

	@Test
	public void canIterateSubGrid() {
		DefaultGrid<Integer> grid = new DefaultGrid<Integer>(factory, 4, 6);
		grid.setCell(0, 0, Integer.valueOf(1));
		grid.setCell(1, 0, Integer.valueOf(2));
		grid.setCell(0, 1, Integer.valueOf(3));
		grid.setCell(1, 1, Integer.valueOf(4));
		grid.setCell(0, 2, Integer.valueOf(5));
		grid.setCell(1, 2, Integer.valueOf(6));

		String result = "";
		for (Integer integer : grid.unmodifiableSubGrid(1, 2, 1, 3)) {
			result += integer.toString();
		}

		assertEquals("46", result);
	}

	@Test
	public void canGetRowInOrder() {
		DefaultGrid<Integer> grid = new DefaultGrid<>(factory, 4, 6);
		grid.setCell(0, 0, Integer.valueOf(1));
		grid.setCell(1, 0, Integer.valueOf(2));
		grid.setCell(0, 1, Integer.valueOf(3));
		grid.setCell(1, 1, Integer.valueOf(4));
		grid.setCell(0, 2, Integer.valueOf(5));
		grid.setCell(1, 2, Integer.valueOf(6));

		assertEquals(4, grid.row(4).size());
		assertEquals(3, grid.row(1).get(0).intValue());
		assertEquals(4, grid.row(1).get(1).intValue());
		assertEquals(0, grid.row(1).get(2).intValue());
		assertEquals(0, grid.row(1).get(3).intValue());
	}

	@Test
	public void canGetColumnInOrder() {
		DefaultGrid<Integer> grid = new DefaultGrid<Integer>(factory, 4, 6);
		grid.setCell(0, 0, Integer.valueOf(1));
		grid.setCell(1, 0, Integer.valueOf(2));
		grid.setCell(0, 1, Integer.valueOf(3));
		grid.setCell(1, 1, Integer.valueOf(4));
		grid.setCell(0, 2, Integer.valueOf(5));
		grid.setCell(1, 2, Integer.valueOf(6));

		assertEquals(6, grid.column(1).size());
		assertEquals(2, grid.column(1).get(0).intValue());
		assertEquals(4, grid.column(1).get(1).intValue());
		assertEquals(6, grid.column(1).get(2).intValue());
		assertEquals(0, grid.column(1).get(3).intValue());
		assertEquals(0, grid.column(1).get(4).intValue());
		assertEquals(0, grid.column(1).get(5).intValue());
	}


	@Test
	public void canRemoveColumn() {
		DefaultGrid<Integer> grid = new DefaultGrid<>(factory, 3, 2);
		grid.setCell(0, 0, Integer.valueOf(1));
		grid.setCell(1, 0, Integer.valueOf(2));
		grid.setCell(2, 0, Integer.valueOf(3));
		grid.setCell(0, 1, Integer.valueOf(4));
		grid.setCell(1, 1, Integer.valueOf(5));
		grid.setCell(2, 1, Integer.valueOf(6));

		assertEquals(Integer.valueOf(2), grid.getCell(1, 0));
		assertEquals(Integer.valueOf(5), grid.getCell(1, 1));

		grid.removeColumn(1);
		assertEquals(2, grid.getColumnCount());
		assertEquals(Integer.valueOf(3), grid.getCell(1, 0));
		assertEquals(Integer.valueOf(6), grid.getCell(1, 1));
	}

	@Test
	public void canRemoveRow() {
		DefaultGrid<Integer> grid = new DefaultGrid<>(factory, 3, 2);
		grid.setCell(0, 0, Integer.valueOf(1));
		grid.setCell(1, 0, Integer.valueOf(2));
		grid.setCell(2, 0, Integer.valueOf(3));
		grid.setCell(0, 1, Integer.valueOf(4));
		grid.setCell(1, 1, Integer.valueOf(5));
		grid.setCell(2, 1, Integer.valueOf(6));

		assertEquals(Integer.valueOf(4), grid.getCell(0, 1));
		assertEquals(Integer.valueOf(5), grid.getCell(1, 1));
		assertEquals(Integer.valueOf(6), grid.getCell(2, 1));

		grid.removeRow(0);
		assertEquals(1, grid.getRowCount());
		assertEquals(Integer.valueOf(4), grid.getCell(0, 0));
		assertEquals(Integer.valueOf(5), grid.getCell(1, 0));
		assertEquals(Integer.valueOf(6), grid.getCell(2, 0));
	}

	@Test
	public void canInsertRow() {
		DefaultGrid<Integer> grid = new DefaultGrid<>(factory, 3, 3);
		grid.setCell(0, 0, Integer.valueOf(1));
		grid.setCell(1, 0, Integer.valueOf(2));
		grid.setCell(2, 0, Integer.valueOf(3));
		grid.setCell(0, 1, Integer.valueOf(4));
		grid.setCell(1, 1, Integer.valueOf(5));
		grid.setCell(2, 1, Integer.valueOf(6));
		grid.setCell(0, 2, Integer.valueOf(7));
		grid.setCell(1, 2, Integer.valueOf(8));
		grid.setCell(2, 2, Integer.valueOf(9));

		grid.insertRow(1);
		assertEquals(4, grid.getRowCount());
		assertEquals(Integer.valueOf(1), grid.getCell(0, 0));
		assertEquals(Integer.valueOf(2), grid.getCell(1, 0));
		assertEquals(Integer.valueOf(3), grid.getCell(2, 0));

		assertEquals(Integer.valueOf(FACTORY_DEFAULT), grid.getCell(0, 1));
		assertEquals(Integer.valueOf(FACTORY_DEFAULT), grid.getCell(1, 1));
		assertEquals(Integer.valueOf(FACTORY_DEFAULT), grid.getCell(2, 1));

		assertEquals(Integer.valueOf(4), grid.getCell(0, 2));
		assertEquals(Integer.valueOf(5), grid.getCell(1, 2));
		assertEquals(Integer.valueOf(6), grid.getCell(2, 2));

		assertEquals(Integer.valueOf(7), grid.getCell(0, 3));
		assertEquals(Integer.valueOf(8), grid.getCell(1, 3));
		assertEquals(Integer.valueOf(9), grid.getCell(2, 3));

	}

	@Test
	public void canInsertColumn() {
		DefaultGrid<Integer> grid = new DefaultGrid<>(factory, 3, 2);
		grid.setCell(0, 0, Integer.valueOf(1));
		grid.setCell(0, 1, Integer.valueOf(2));
		grid.setCell(1, 0, Integer.valueOf(3));
		grid.setCell(1, 1, Integer.valueOf(4));
		grid.setCell(2, 0, Integer.valueOf(5));
		grid.setCell(2, 1, Integer.valueOf(6));

		grid.insertColumn(1);
		assertEquals(4, grid.getColumnCount());
		assertEquals(Integer.valueOf(1), grid.getCell(0, 0));
		assertEquals(Integer.valueOf(2), grid.getCell(0, 1));

		assertEquals(Integer.valueOf(FACTORY_DEFAULT), grid.getCell(1, 0));
		assertEquals(Integer.valueOf(FACTORY_DEFAULT), grid.getCell(1, 1));

		assertEquals(Integer.valueOf(3), grid.getCell(2, 0));
		assertEquals(Integer.valueOf(4), grid.getCell(2, 1));

		assertEquals(Integer.valueOf(5), grid.getCell(3, 0));
		assertEquals(Integer.valueOf(6), grid.getCell(3, 1));
	}
}