package org.ringingmaster.engine.arraytable;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.junit.Test;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class SubImmutableArrayTableTest {

    private ImmutableArrayTable<String> arrayTable;

    {
        HashBasedTable<Integer, Integer, String> backingTable = HashBasedTable.create();
        for (int row=0;row<10;row++) {
            for (int col=0;col<10;col++) {
                backingTable.put(row, col, String.format("%d,%d", row, col));
            }
        }
        arrayTable = new TableBackedImmutableArrayTable<>(backingTable, () -> "");
    }


    @Test
    public void subTableCanBeCreatedWithZeroDimensions() {
        ImmutableArrayTable<String> subTable = this.arrayTable.subTable(0, 0, 0, 0);

        assertEquals(0, subTable.getRowSize());
        assertEquals(0, subTable.getColumnSize());
    }

    @Test
    public void subTableCanBeCreatedWithOriginalDimensions() {
        ImmutableArrayTable<String> subTable = this.arrayTable.subTable(0, 10, 0, 10);

        assertEquals(arrayTable.getRowSize(), subTable.getRowSize());
        assertEquals(arrayTable.getColumnSize(), subTable.getColumnSize());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void limitCheckingAtMaxFromRowCorrect() {
        this.arrayTable.subTable(10, 1, 1, 1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void limitCheckingAtMaxToRowCorrect() {
        this.arrayTable.subTable(1, 11, 1, 1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void limitCheckingAtMaxFromColCorrect() {
        this.arrayTable.subTable(1, 1, 10, 1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void limitCheckingAtMaxToColCorrect() {
        this.arrayTable.subTable(1, 1, 1, 11);
    }

    @Test(expected = IllegalArgumentException.class)
    public void limitCheckingAtCrossOverRowCorrect() {
        this.arrayTable.subTable(2, 1, 1, 5);
    }
    @Test(expected = IllegalArgumentException.class)
    public void limitCheckingAtCrossOverColCorrect() {
        this.arrayTable.subTable(1, 5, 2, 1);
    }

    @Test
    public void subTableHasCorrectLimits() {
        ImmutableArrayTable<String> subTable = this.arrayTable.subTable(0, 1, 0, 1);

        assertEquals(1, subTable.getRowSize());
        assertEquals(1, subTable.getColumnSize());

        assertEquals("0,0", subTable.get(0,0));
    }

    @Test
    public void offsetGetsCorrectCell() {
        ImmutableArrayTable<String> subTable = this.arrayTable.subTable(1, 2, 1, 2);

        assertEquals(1, subTable.getRowSize());
        assertEquals(1, subTable.getColumnSize());

        assertEquals("1,1", subTable.get(0,0));
    }

    @Test
    public void originalBackingTableReturned() {
        HashBasedTable<Integer, Integer, String> backingTable = HashBasedTable.create();
        backingTable.put(1,1,"VALUE");

        TableBackedImmutableArrayTable<String> arrayTable = new TableBackedImmutableArrayTable<>(backingTable, () -> "DEFAULT");
        ImmutableArrayTable<String> subTable = arrayTable.subTable(0, 1, 0, 1);

        assertEquals((Table)backingTable, (Table)subTable.getBackingTable());
    }

    @Test
    public void subTableASubTable() {
        ImmutableArrayTable<String> subTable = this.arrayTable.subTable(2, 8, 2, 8);
        ImmutableArrayTable<String> subSubTable = subTable.subTable(2, 3, 3, 4);

        assertEquals(1, subSubTable.getRowSize());
        assertEquals(1, subSubTable.getColumnSize());

        assertEquals("4,5", subSubTable.get(0,0));
    }


    @Test
    public void iteratorReturnsCellsInCorrectOrder() {
        HashBasedTable<Integer, Integer, String> backingTable = HashBasedTable.create();
        backingTable.put(0,0,"0,0");
        backingTable.put(0,1,"0,1");
        backingTable.put(1,0,"1,0");
        backingTable.put(1,1,"1,1");

        TableBackedImmutableArrayTable<String> arrayTable = new TableBackedImmutableArrayTable<>(backingTable, () -> "DEFAULT");
        ImmutableArrayTable<String> subTable = arrayTable.subTable(1, 2, 1, 2);

        Iterator<BackingTableLocationAndValue<String>> locationAndValueIterator = subTable.iterateByRowThenColumn();

        assertRowColVal(locationAndValueIterator.next(), 1, 1, "1,1");

        assertFalse(locationAndValueIterator.hasNext());
    }

    @Test(expected = NoSuchElementException.class)
    public void emptyIteratorNextThrows() {
        HashBasedTable<Integer, Integer, String> backingTable = HashBasedTable.create();

        TableBackedImmutableArrayTable<String> arrayTable = new TableBackedImmutableArrayTable<>(backingTable, () -> "DEFAULT");

        arrayTable.iterateByRowThenColumn().next();
    }

    private <T> void assertRowColVal(BackingTableLocationAndValue<T> next, int row, int col, T val) {

        assertEquals(row, next.getRow());
        assertEquals(col, next.getCol());
        assertEquals(val, next.getValue());
    }

}