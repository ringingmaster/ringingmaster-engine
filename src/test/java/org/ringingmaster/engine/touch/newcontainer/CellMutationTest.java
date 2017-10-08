package org.ringingmaster.engine.touch.newcontainer;

import org.junit.Test;
import org.ringingmaster.engine.touch.newcontainer.cell.EmptyCell;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class CellMutationTest {

    @Test
    public void hasCorrectDefault() throws Exception {

        ObservableTouch touch = new ObservableTouch();

        assertEquals(0, touch.get().cells().getColumnSize());
        assertEquals(0, touch.get().cells().getRowSize());
    }

    @Test
    public void addingCharactersToEmptyCellAddsCell() {
        ObservableTouch touch = new ObservableTouch();

        touch.addCharacters(0,0, "ABC");

        assertEquals(1, touch.get().cells().getColumnSize());
        assertEquals(1, touch.get().cells().getRowSize());
        assertEquals("ABC", touch.get().cells().get(0,0).getCharacters());
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void addingCharactersOutsideRowBoundsThrows() {
        ObservableTouch touch = new ObservableTouch();
        touch.addCharacters(1,0, "ABC");
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void addingCharactersOutsideColumnBoundsThrows() {
        ObservableTouch touch = new ObservableTouch();
        touch.addCharacters(0,1, "ABC");
    }

    @Test
    public void addingCharacterToExistingCellAppends() {
        ObservableTouch touch = new ObservableTouch();
        touch.addCharacters(0,0, "ABC");

        touch.addCharacters(0,0, "123");
        assertEquals("ABC123", touch.get().cells().get(0,0).getCharacters());
    }


    @Test
    public void insertingCharactersToEmptyCellAddsCell() {
        ObservableTouch touch = new ObservableTouch();

        touch.insertCharacters(0,0, 0,"ABC");

        assertEquals(1, touch.get().cells().getColumnSize());
        assertEquals(1, touch.get().cells().getRowSize());
        assertEquals("ABC", touch.get().cells().get(0,0).getCharacters());
    }

    @Test
    public void insertingCharacterToExistingCellInserts() {
        ObservableTouch touch = new ObservableTouch();
        touch.addCharacters(0,0, "ABC");

        touch.insertCharacters(0,0, 1, "123");
        assertEquals("A123BC", touch.get().cells().get(0,0).getCharacters());
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void insertingCharactersOutsideRowBoundsThrows() {
        ObservableTouch touch = new ObservableTouch();
        touch.insertCharacters(1,0, 0, "ABC");
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void insertingCharactersOutsideColumnBoundsThrows() {
        ObservableTouch touch = new ObservableTouch();
        touch.insertCharacters(0,1, 0, "ABC");
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void insertingCharactersOutsideCellBoundsThrows() {
        ObservableTouch touch = new ObservableTouch();
        touch.insertCharacters(0,0, 1, "ABC");
    }

    @Test
    public void removingSingleCharacterFromExistingCellDeletes() {
        ObservableTouch touch = new ObservableTouch();
        touch.addCharacters(0,0, "ABC");

        touch.removeCharacters(0, 0, 1,1);
        assertEquals("AC", touch.get().cells().get(0,0).getCharacters());
    }

    @Test
    public void removingCharacterRangeFromExistingCellDeletes() {
        ObservableTouch touch = new ObservableTouch();
        touch.addCharacters(0,0, "ABCD");

        touch.removeCharacters(0, 0, 1,2);
        assertEquals("AD", touch.get().cells().get(0,0).getCharacters());
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void removingCharactersOutsideRowBoundsThrows() {
        ObservableTouch touch = new ObservableTouch();
        touch.removeCharacters(1,0, 0, 1);
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void removingCharactersOutsideColumnBoundsThrows() {
        ObservableTouch touch = new ObservableTouch();
        touch.removeCharacters(0,1, 0, 1);
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void removingCharactersOutsideCellBoundsThrows() {
        ObservableTouch touch = new ObservableTouch();
        touch.addCharacters(0,0,"A");
        touch.removeCharacters(0,0, 1, 1);
    }

    @Test
    public void removingAllCharactersRemovesCell() {
        ObservableTouch touch = new ObservableTouch();
        touch.addCharacters(0,0, "ABC");

        touch.removeCharacters(0, 0, 0,3);
        assertEquals("", touch.get().cells().get(0,0).getCharacters());
        assertTrue(touch.get().cells().get(0,0) instanceof EmptyCell);
    }

    @Test
    public void removingAllItemsInColumnCausesColumnRemoval() {
        ObservableTouch touch = new ObservableTouch();
        touch.addCharacters(0,0, "0,0");
        touch.addCharacters(0,1, "0,1");
        touch.addCharacters(0,2, "0,2");
        touch.addCharacters(1,2, "1,2");
        assertEquals(3, touch.get().cells().getColumnSize());

        touch.removeCharacters(0, 1, 0,3);
        assertEquals(2, touch.get().cells().getColumnSize());
    }

    @Test
    public void removingAllItemsInRowCausesRowRemoval() {
        ObservableTouch touch = new ObservableTouch();
        touch.addCharacters(0,0, "0,0");
        touch.addCharacters(1,0, "1,0");
        touch.addCharacters(2,0, "2,0");
        touch.addCharacters(2,1, "2,1");
        assertEquals(3, touch.get().cells().getRowSize());

        touch.removeCharacters(1, 0, 0,3);
        assertEquals(2, touch.get().cells().getRowSize());
    }

}
