package org.ringingmaster.engine.arraytable;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class TableBackedImmutableArrayTableTest {

    @Test
    public void emptyTableReturnsZeroSizeForRowAndColumn() {
        ImmutableArrayTable<String> immutableArrayTable = new TableBackedImmutableArrayTable<>(() -> "");
        assertEquals(0, immutableArrayTable.getColumnSize());
        assertEquals(0, immutableArrayTable.getRowSize());
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenRowItemLessThanZeroThrows() {
        HashBasedTable<Integer, Integer, String> table = HashBasedTable.create();
        table.put(-10,0,"");

        new TableBackedImmutableArrayTable<>(table, () -> "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenColumnItemLessThanZeroThrows() {
        HashBasedTable<Integer, Integer, String> table = HashBasedTable.create();
        table.put(0, -10,"");

        new TableBackedImmutableArrayTable<>(table, () -> "");
    }

    @Test
    public void whenRowAndColumnItemGreaterThanZeroRowCountSetCorrectly() {
        HashBasedTable<Integer, Integer, String> backingTable = HashBasedTable.create();
        backingTable.put(10,20,"");

        TableBackedImmutableArrayTable<String> arrayTable = new TableBackedImmutableArrayTable<>(backingTable, () -> "");

        assertEquals(11, arrayTable.getRowSize());
        assertEquals(21, arrayTable.getColumnSize());
    }

    @Test
    public void canGetCellAt00() {
        HashBasedTable<Integer, Integer, String> backingTable = HashBasedTable.create();
        backingTable.put(0,0,"VALUE");

        TableBackedImmutableArrayTable<String> arrayTable = new TableBackedImmutableArrayTable<>(backingTable, () -> "");

        assertEquals("VALUE", arrayTable.get(0,0));
    }

    @Test
    public void canGetCellAtNon00() {
        HashBasedTable<Integer, Integer, String> backingTable = HashBasedTable.create();
        backingTable.put(1,1,"VALUE");

        TableBackedImmutableArrayTable<String> arrayTable = new TableBackedImmutableArrayTable<>(backingTable, () -> "");

        assertEquals("VALUE", arrayTable.get(1,1));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void gettingOutOfBoundsCellAt00Throws() {
        HashBasedTable<Integer, Integer, String> backingTable = HashBasedTable.create();

        TableBackedImmutableArrayTable<String> arrayTable = new TableBackedImmutableArrayTable<>(backingTable, () -> "");

        arrayTable.get(0,0);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void gettingOutOfBoundsCellAt01Throws() {
        HashBasedTable<Integer, Integer, String> backingTable = HashBasedTable.create();
        backingTable.put(0,0,"VALUE");

        TableBackedImmutableArrayTable<String> arrayTable = new TableBackedImmutableArrayTable<>(backingTable, () -> "");

        arrayTable.get(0,1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void gettingOutOfBoundsCellAt10Throws() {
        HashBasedTable<Integer, Integer, String> backingTable = HashBasedTable.create();
        backingTable.put(0,0,"VALUE");

        TableBackedImmutableArrayTable<String> arrayTable = new TableBackedImmutableArrayTable<>(backingTable, () -> "");

        arrayTable.get(1,0);
    }

    @Test
    public void getDefaultForInBoundsNonPopulatedCell() {
        HashBasedTable<Integer, Integer, String> backingTable = HashBasedTable.create();
        backingTable.put(1,1,"VALUE");

        TableBackedImmutableArrayTable<String> arrayTable = new TableBackedImmutableArrayTable<>(backingTable, () -> "DEFAULT");

        assertEquals("DEFAULT", arrayTable.get(0,0));
    }

    @Test
    public void originalBackingTableReturned() {
        HashBasedTable<Integer, Integer, String> backingTable = HashBasedTable.create();
        backingTable.put(1,1,"VALUE");

        TableBackedImmutableArrayTable<String> arrayTable = new TableBackedImmutableArrayTable<>(backingTable, () -> "DEFAULT");

        assertEquals((Table)backingTable, (Table)arrayTable.getBackingTable());
    }

    @Test
    public void iteratorReturnsCellsInCorrectOrder() {
        HashBasedTable<Integer, Integer, String> backingTable = HashBasedTable.create();
        backingTable.put(0,0,"0,0");
        backingTable.put(0,1,"0,1");
        backingTable.put(1,0,"1,0");
        backingTable.put(1,1,"1,1");

        TableBackedImmutableArrayTable<String> arrayTable = new TableBackedImmutableArrayTable<>(backingTable, () -> "DEFAULT");

        Iterator<BackingTableLocationAndValue<String>> locationAndValueIterator = arrayTable.iterateByRowThenColumn();

        assertRowColVal(locationAndValueIterator.next(), 0, 0, "0,0");
        assertRowColVal(locationAndValueIterator.next(), 0, 1, "0,1");
        assertRowColVal(locationAndValueIterator.next(), 1, 0, "1,0");
        assertRowColVal(locationAndValueIterator.next(), 1, 1, "1,1");

        assertFalse(locationAndValueIterator.hasNext());
    }

    private <T> void assertRowColVal(BackingTableLocationAndValue<T> next, int row, int col, T val) {

        assertEquals(row, next.getRow());
        assertEquals(col, next.getCol());
        assertEquals(val, next.getValue());
    }
}