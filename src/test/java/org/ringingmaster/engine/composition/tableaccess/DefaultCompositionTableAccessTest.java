package org.ringingmaster.engine.composition.tableaccess;

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
import org.ringingmaster.engine.notation.Notation;
import org.ringingmaster.engine.notation.NotationBuilder;
import org.ringingmaster.engine.composition.cell.Cell;
import org.ringingmaster.engine.composition.cell.CellBuilder;
import org.ringingmaster.engine.composition.cell.EmptyCell;
import org.ringingmaster.engine.composition.compositiontype.CompositionType;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.ringingmaster.engine.composition.compositiontype.CompositionType.COURSE_BASED;
import static org.ringingmaster.engine.composition.compositiontype.CompositionType.LEAD_BASED;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
@RunWith(Parameterized.class)
public class DefaultCompositionTableAccessTest {

    @Parameters
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][] {

                //R, C, CompositionType,   spliced,   mainSize, spliceSize, callPosSize, mainRoot,  spliceRoot, callPosRoot
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
    public CompositionType compositionType;
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
    public int expectedCallingPositionRows;
    @Parameter(9)
    public int expectedCallingPositionColumns;

    @Parameter(10)
    public String expectedMainRoot;
    @Parameter(11)
    public String expectedSplicedRoot;
    @Parameter(12)
    public String expectedCallingPositionRoot;


    private static Notation buildNotation(NumberOfBells bells, String name, String notation1) {
        return NotationBuilder.getInstance()
                .setNumberOfWorkingBells(bells)
                .setName(name)
                .setUnfoldedNotationShorthand(notation1)
                .build();
    }

    @Test
    public void mainBodyTableDimensions() {
        DefaultCompositionTableAccess<Cell> defaultCompositionTableAccess = buildCells1(rows, cols, compositionType, spliced);
        assertDimensions(expectedMainBodyRows, expectedMainBodyColumns, defaultCompositionTableAccess.mainBodyCells());
    }

    @Test
    public void mainBodyRoot() {
        DefaultCompositionTableAccess<Cell> defaultCompositionTableAccess = buildCells1(rows, cols, compositionType, spliced);
        assertRoot(expectedMainRoot, defaultCompositionTableAccess.mainBodyCells());
    }

    @Test
    public void spliceTableDimensions() {
        DefaultCompositionTableAccess<Cell> defaultCompositionTableAccess = buildCells1(rows, cols, compositionType, spliced);
        assertDimensions(expectedSplicedRows, expectedSplicedColumns, defaultCompositionTableAccess.splicedCells());
    }

    @Test
    public void spliceRoot() {
        DefaultCompositionTableAccess<Cell> defaultCompositionTableAccess = buildCells1(rows, cols, compositionType, spliced);
        assertRoot(expectedSplicedRoot, defaultCompositionTableAccess.splicedCells());
    }

    @Test
    public void callingPositionTableDimensions() {
        DefaultCompositionTableAccess<Cell> defaultCompositionTableAccess = buildCells1(rows, cols, compositionType, spliced);
        assertDimensions(expectedCallingPositionRows, expectedCallingPositionColumns, defaultCompositionTableAccess.callingPositionCells());
    }

    @Test
    public void callingPositionRoot() {
        DefaultCompositionTableAccess<Cell> defaultCompositionTableAccess = buildCells1(rows, cols, compositionType, spliced);
        assertRoot(expectedCallingPositionRoot, defaultCompositionTableAccess.callingPositionCells());
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

    private DefaultCompositionTableAccess<Cell> buildCells1(int rows, int cols, CompositionType compositionType, boolean spliced) {
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

        return new DefaultCompositionTableAccess<>(new TableBackedImmutableArrayTable<>(cells, EmptyCell::new), compositionType, spliced);
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
