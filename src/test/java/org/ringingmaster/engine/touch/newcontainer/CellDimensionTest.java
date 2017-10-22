package org.ringingmaster.engine.touch.newcontainer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.arraytable.ImmutableArrayTable;
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.notation.impl.NotationBuilder;
import org.ringingmaster.engine.touch.newcontainer.cell.Cell;
import org.ringingmaster.engine.touch.newcontainer.checkingtype.CheckingType;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.ringingmaster.engine.touch.newcontainer.checkingtype.CheckingType.COURSE_BASED;
import static org.ringingmaster.engine.touch.newcontainer.checkingtype.CheckingType.LEAD_BASED;

/**
 * TODO comments???
 *
 * @author stevelake
 */
@RunWith(Parameterized.class)
public class CellDimensionTest {

    @Parameters
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][] {
                //R, C, CheckingType,   spliced, main, splice, callPos
                { 0, 0, LEAD_BASED,     true,    0,0,  0,0,    0,0 },   //0
                { 0, 0, COURSE_BASED,   true,    0,0,  0,0,    0,0 },
                { 0, 0, LEAD_BASED,     false,   0,0,  0,0,    0,0 },
                { 0, 0, COURSE_BASED,   false,   0,0,  0,0,    0,0 },

                { 1, 1, LEAD_BASED,     true,    1,1,  0,0,    0,0 },   //4
                { 1, 1, COURSE_BASED,   true,    1,1,  0,0,    0,0 },
                { 1, 1, LEAD_BASED,     false,   1,1,  0,0,    0,0 },
                { 1, 1, COURSE_BASED,   false,   1,1,  0,0,    0,0 },

                { 2, 2, LEAD_BASED,     true,    2,1,  2,1,    0,0 },   //8
                { 2, 2, COURSE_BASED,   true,    1,1,  1,1,    1,1 },
                { 2, 2, LEAD_BASED,     false,   2,2,  0,0,    0,0 },
                { 2, 2, COURSE_BASED,   false,   1,2,  0,0,    1,2 },

                { 3, 3, LEAD_BASED,     true,    3,2,  3,1,    0,0 },   //12
                { 3, 3, COURSE_BASED,   true,    2,2,  2,1,    1,2 },
                { 3, 3, LEAD_BASED,     false,   3,3,  0,0,    0,0 },
                { 3, 3, COURSE_BASED,   false,   2,3,  0,0,    1,3 },

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

    public static final NotationBody METHOD_A_6_BELL = buildNotation(NumberOfBells.BELLS_6, "METHOD A", "12");

    private static NotationBody buildNotation(NumberOfBells bells, String name, String notation1) {
        return NotationBuilder.getInstance()
                .setNumberOfWorkingBells(bells)
                .setName(name)
                .setUnfoldedNotationShorthand(notation1)
                .build();
    }

    @Test
    public void mainBodyTable() {

        ObservableTouch observableTouch = buildCells(rows, cols, checkingType, spliced);

        Touch touch = observableTouch.get();
        assertDimensions(expectedMainBodyRows, expectedMainBodyColumns, touch.mainBodyCells());
    }

    @Test
    public void splicedTable() {
        ObservableTouch observableTouch = buildCells(rows, cols, checkingType, spliced);
        Touch touch = observableTouch.get();
        assertDimensions(expectedSplicedRows, expectedSplicedColumns, touch.splicedCells());
    }

    @Test
    public void callPositionTable() {
        ObservableTouch observableTouch = buildCells(rows, cols, checkingType, spliced);
        Touch touch = observableTouch.get();
        assertDimensions(expectedCallPositionRows, expectedCallPositionColumns, touch.callPositionCells());
    }

    private void assertDimensions(int row, int col, ImmutableArrayTable<Cell> cells) {
        assertEquals(new Pair(row, col), new Pair(cells.getRowSize(), cells.getColumnSize()));
    }

    private ObservableTouch buildCells(int rows, int cols, CheckingType checkingType, boolean spliced) {
        ObservableTouch observableTouch = new ObservableTouch();
        observableTouch.setTouchCheckingType(checkingType);
        observableTouch.addNotation(METHOD_A_6_BELL);
        observableTouch.setSpliced(spliced);

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                observableTouch.addCharacters(row, col, row + "," + col);
            }
        }
        return observableTouch;
    }

    class Pair {
        final int one;
        final int two;

        public Pair(int one, int two) {
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
