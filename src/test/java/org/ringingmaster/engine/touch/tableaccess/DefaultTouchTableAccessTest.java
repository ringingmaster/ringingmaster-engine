package org.ringingmaster.engine.touch.tableaccess;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.arraytable.ImmutableArrayTable;
import org.ringingmaster.engine.arraytable.TableBackedImmutableArrayTable;
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.notation.impl.NotationBuilder;
import org.ringingmaster.engine.touch.cell.Cell;
import org.ringingmaster.engine.touch.cell.CellBuilder;
import org.ringingmaster.engine.touch.cell.EmptyCell;
import org.ringingmaster.engine.touch.checkingtype.CheckingType;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.ringingmaster.engine.touch.checkingtype.CheckingType.COURSE_BASED;
import static org.ringingmaster.engine.touch.checkingtype.CheckingType.LEAD_BASED;

/**
 * TODO comments???
 *
 * @author stevelake
 */
@RunWith(Parameterized.class)
public class DefaultTouchTableAccessTest {

    @Parameters
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][] {

                //R, C, CheckingType,   spliced,   mainSize, spliceSize, callPosSize, mainRoot,  spliceRoot, callPosRoot
                { 0, 0, LEAD_BASED,     true,      0,0,      0,0,        0,0,         null,      null,       null},   //0
                { 0, 0, COURSE_BASED,   true,      0,0,      0,0,        0,0,         null,      null,       null},
                { 0, 0, LEAD_BASED,     false,     0,0,      0,0,        0,0,         null,      null,       null},
                { 0, 0, COURSE_BASED,   false,     0,0,      0,0,        0,0,         null,      null,       null},

                { 1, 1, LEAD_BASED,     true,      1,1,      0,0,        0,0,         "0,0",     null,       null},   //4
                { 1, 1, COURSE_BASED,   true,      0,0,      0,0,        1,1,         null,      null,       "0,0"},
                { 1, 1, LEAD_BASED,     false,     1,1,      0,0,        0,0,         "0,0",     null,       null},
                { 1, 1, COURSE_BASED,   false,     0,0,      0,0,        1,1,         null,      null,       "0,0"},

                { 2, 1, LEAD_BASED,     true,      2,1,      0,0,        0,0,         "0,0",     null,       null},   //8
                { 2, 1, COURSE_BASED,   true,      1,1,      0,0,        1,1,         "1,0",     null,       "0,0"},
                { 2, 1, LEAD_BASED,     false,     2,1,      0,0,        0,0,         "0,0",     null,       null},
                { 2, 1, COURSE_BASED,   false,     1,1,      0,0,        1,1,         "1,0",     null,       "0,0"},

                { 1, 2, LEAD_BASED,     true,      1,1,      1,1,        0,0,         "0,0",     "0,1",       null},   //12
                { 1, 2, COURSE_BASED,   true,      0,0,      0,0,        1,1,         null,      null,       "0,0"},
                { 1, 2, LEAD_BASED,     false,     1,2,      0,0,        0,0,         "0,0",     null,       null},
                { 1, 2, COURSE_BASED,   false,     0,0,      0,0,        1,2,         null,      null,       "0,0"},

                { 2, 2, LEAD_BASED,     true,      2,1,      2,1,        0,0,         "0,0",     "0,1",      null},   //16
                { 2, 2, COURSE_BASED,   true,      1,1,      1,1,        1,1,         "1,0",     "1,1",      "0,0"},
                { 2, 2, LEAD_BASED,     false,     2,2,      0,0,        0,0,         "0,0",     null,       null},
                { 2, 2, COURSE_BASED,   false,     1,2,      0,0,        1,2,         "1,0",     null,       "0,0"},

                { 3, 3, LEAD_BASED,     true,      3,2,      3,1,        0,0,         "0,0",     "0,2",      null},   //20
                { 3, 3, COURSE_BASED,   true,      2,2,      2,1,        1,2,         "1,0",     "1,2",      "0,0"},
                { 3, 3, LEAD_BASED,     false,     3,3,      0,0,        0,0,         "0,0",     null,       null},
                { 3, 3, COURSE_BASED,   false,     2,3,      0,0,        1,3,         "1,0",     null,       "0,0"}
        });
        }

    @Parameter(0)
    public int rows;
    @Parameter(1)
    public int cols;
    @Parameter(2)
    public CheckingType checkingType;
    @Parameter(3)
    public boolean spliced;


    @Parameter(4)
    public int expectedMainBodyRows;
    @Parameter(5)
    public int expectedMainBodyColumns;

    @Parameter(6)
    public int expectedSplicedRows;
    @Parameter(7)
    public int expectedSplicedColumns;

    @Parameter(8)
    public int expectedCallPositionRows;
    @Parameter(9)
    public int expectedCallPositionColumns;

    @Parameter(10)
    public String expectedMainRoot;
    @Parameter(11)
    public String expectedSplicedRoot;
    @Parameter(12)
    public String expectedCallPositionRoot;


    private static NotationBody buildNotation(NumberOfBells bells, String name, String notation1) {
        return NotationBuilder.getInstance()
                .setNumberOfWorkingBells(bells)
                .setName(name)
                .setUnfoldedNotationShorthand(notation1)
                .build();
    }

    @Test
    public void mainBodyTableDimensions() {
        DefaultTouchTableAccess<Cell> defaultTouchTableAccess = buildCells1(rows, cols, checkingType, spliced);
        assertDimensions(expectedMainBodyRows, expectedMainBodyColumns, defaultTouchTableAccess.mainBodyCells());
    }

    @Test
    public void mainBodyRoot() {
        DefaultTouchTableAccess<Cell> defaultTouchTableAccess = buildCells1(rows, cols, checkingType, spliced);
        assertRoot(expectedMainRoot, defaultTouchTableAccess.mainBodyCells());
    }

    @Test
    public void spliceTableDimensions() {
        DefaultTouchTableAccess<Cell> defaultTouchTableAccess = buildCells1(rows, cols, checkingType, spliced);
        assertDimensions(expectedSplicedRows, expectedSplicedColumns, defaultTouchTableAccess.splicedCells());
    }

    @Test
    public void spliceRoot() {
        DefaultTouchTableAccess<Cell> defaultTouchTableAccess = buildCells1(rows, cols, checkingType, spliced);
        assertRoot(expectedSplicedRoot, defaultTouchTableAccess.splicedCells());
    }

    @Test
    public void callPositionTableDimensions() {
        DefaultTouchTableAccess<Cell> defaultTouchTableAccess = buildCells1(rows, cols, checkingType, spliced);
        assertDimensions(expectedCallPositionRows, expectedCallPositionColumns, defaultTouchTableAccess.callPositionCells());
    }

    @Test
    public void callPositionRoot() {
        DefaultTouchTableAccess<Cell> defaultTouchTableAccess = buildCells1(rows, cols, checkingType, spliced);
        assertRoot(expectedCallPositionRoot, defaultTouchTableAccess.callPositionCells());
    }

    private void assertDimensions(int expectedRowSize, int expectedColumnSize, ImmutableArrayTable<Cell> cells) {
        assertEquals(new Pair(expectedRowSize, expectedColumnSize), new Pair(cells.getRowSize(), cells.getColumnSize()));
    }

    private void assertRoot(String expected, ImmutableArrayTable<Cell> cells) {
        if (expected == null) {
            assertEquals("Row Size",0, cells.getRowSize());
            assertEquals("Col Size", 0, cells.getColumnSize());
        }
        else {
            assertEquals(expected, cells.get(0,0).getCharacters());
        }
    }

    private DefaultTouchTableAccess<Cell> buildCells1(int rows, int cols, CheckingType checkingType, boolean spliced) {
        Table<Integer, Integer, Cell> cells = HashBasedTable.create();

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Cell cell = new CellBuilder()
                        .defaults()
                        .insert(0, row + "," + col)
                        .build();

                cells.put(row, col, cell);
            }
        }

        return new DefaultTouchTableAccess<>(new TableBackedImmutableArrayTable<>(cells, EmptyCell::new), checkingType, spliced);
    }

    class Pair {
        final int one;
        final int two;

        Pair(int one, int two) {
            this.one = one;
            this.two = two;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Pair pair = (Pair) o;

            if (one != pair.one) return false;
            return two == pair.two;
        }

        @Override
        public int hashCode() {
            int result = one;
            result = 31 * result + two;
            return result;
        }

        @Override
        public String toString() {
            return one + "," + two;
        }
    }

}
