package org.ringingmaster.engine.arraytable;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

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

    @Test
    public void getDefaultForNonPopulatedCell() {
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
}