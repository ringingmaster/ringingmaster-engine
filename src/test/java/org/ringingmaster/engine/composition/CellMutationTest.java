package org.ringingmaster.engine.composition;

import org.junit.Test;
import org.ringingmaster.engine.composition.cell.EmptyCell;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.ringingmaster.engine.composition.TableType.MAIN_TABLE;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class CellMutationTest {

    @Test
    public void hasCorrectDefault() throws Exception {

        ObservableComposition composition = new ObservableComposition();

        assertEquals(0, composition.get().allCompositionCells().getColumnSize());
        assertEquals(0, composition.get().allCompositionCells().getRowSize());
    }

    @Test
    public void addingCharactersToEmptyCellAddsCell() {
        ObservableComposition composition = new ObservableComposition();

        composition.addCharacters(MAIN_TABLE, 0,0, "ABC");

        assertEquals(1, composition.get().allCompositionCells().getColumnSize());
        assertEquals(1, composition.get().allCompositionCells().getRowSize());
        assertEquals("ABC", composition.get().allCompositionCells().get(0,0).getCharacters());
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void addingCharactersOutsideRowBoundsThrows() {
        ObservableComposition composition = new ObservableComposition();
        composition.addCharacters(MAIN_TABLE, 1,0, "ABC");
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void addingCharactersOutsideColumnBoundsThrows() {
        ObservableComposition composition = new ObservableComposition();
        composition.addCharacters(MAIN_TABLE, 0,1, "ABC");
    }

    @Test
    public void addingCharacterToExistingCellAppends() {
        ObservableComposition composition = new ObservableComposition();
        composition.addCharacters(MAIN_TABLE, 0,0, "ABC");

        composition.addCharacters(MAIN_TABLE, 0,0, "123");
        assertEquals("ABC123", composition.get().allCompositionCells().get(0,0).getCharacters());
    }


    @Test
    public void insertingCharactersToEmptyCellAddsCell() {
        ObservableComposition composition = new ObservableComposition();

        composition.insertCharacters(MAIN_TABLE, 0,0, 0,"ABC");

        assertEquals(1, composition.get().allCompositionCells().getColumnSize());
        assertEquals(1, composition.get().allCompositionCells().getRowSize());
        assertEquals("ABC", composition.get().allCompositionCells().get(0,0).getCharacters());
    }

    @Test
    public void insertingCharacterToExistingCellInserts() {
        ObservableComposition composition = new ObservableComposition();
        composition.addCharacters(MAIN_TABLE, 0,0, "ABC");

        composition.insertCharacters(MAIN_TABLE, 0,0, 1, "123");
        assertEquals("A123BC", composition.get().allCompositionCells().get(0,0).getCharacters());
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void insertingCharactersOutsideRowBoundsThrows() {
        ObservableComposition composition = new ObservableComposition();
        composition.insertCharacters(MAIN_TABLE, 1,0, 0, "ABC");
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void insertingCharactersOutsideColumnBoundsThrows() {
        ObservableComposition composition = new ObservableComposition();
        composition.insertCharacters(MAIN_TABLE, 0,1, 0, "ABC");
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void insertingCharactersOutsideCellBoundsThrows() {
        ObservableComposition composition = new ObservableComposition();
        composition.insertCharacters(MAIN_TABLE, 0,0, 1, "ABC");
    }

    @Test
    public void removingSingleCharacterFromExistingCellDeletes() {
        ObservableComposition composition = new ObservableComposition();
        composition.addCharacters(MAIN_TABLE, 0,0, "ABC");

        composition.removeCharacters(MAIN_TABLE, 0, 0, 1,1);
        assertEquals("AC", composition.get().allCompositionCells().get(0,0).getCharacters());
    }

    @Test
    public void removingCharacterRangeFromExistingCellDeletes() {
        ObservableComposition composition = new ObservableComposition();
        composition.addCharacters(MAIN_TABLE, 0,0, "ABCD");

        composition.removeCharacters(MAIN_TABLE, 0, 0, 1,2);
        assertEquals("AD", composition.get().allCompositionCells().get(0,0).getCharacters());
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void removingCharactersOutsideRowBoundsThrows() {
        ObservableComposition composition = new ObservableComposition();
        composition.removeCharacters(MAIN_TABLE, 1,0, 0, 1);
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void removingCharactersOutsideColumnBoundsThrows() {
        ObservableComposition composition = new ObservableComposition();
        composition.removeCharacters(MAIN_TABLE, 0,1, 0, 1);
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void removingCharactersOutsideCellBoundsThrows() {
        ObservableComposition composition = new ObservableComposition();
        composition.addCharacters(MAIN_TABLE, 0,0,"A");
        composition.removeCharacters(MAIN_TABLE, 0,0, 1, 1);
    }

    @Test
    public void removingAllCharactersRemovesCell() {
        ObservableComposition composition = new ObservableComposition();
        composition.addCharacters(MAIN_TABLE, 0,0, "ABC");
        composition.addCharacters(MAIN_TABLE, 0,1, "PADDING");
        composition.addCharacters(MAIN_TABLE, 1,0, "PADDING");

        composition.removeCharacters(MAIN_TABLE, 0, 0, 0,3);
        assertEquals("", composition.get().allCompositionCells().get(0,0).getCharacters());
        assertTrue(composition.get().allCompositionCells().get(0,0) instanceof EmptyCell);
    }

    @Test
    public void removingAllItemsInColumnCausesColumnRemoval() {
        ObservableComposition composition = new ObservableComposition();
        composition.addCharacters(MAIN_TABLE, 0,0, "0,0");
        composition.addCharacters(MAIN_TABLE, 0,1, "0,1");
        composition.addCharacters(MAIN_TABLE, 0,2, "0,2");
        composition.addCharacters(MAIN_TABLE, 1,2, "1,2");
        assertEquals(3, composition.get().allCompositionCells().getColumnSize());

        composition.removeCharacters(MAIN_TABLE, 0, 1, 0,3);
        assertEquals(2, composition.get().allCompositionCells().getColumnSize());
    }

    @Test
    public void removingAllItemsInRowCausesRowRemoval() {
        ObservableComposition composition = new ObservableComposition();
        composition.addCharacters(MAIN_TABLE, 0,0, "0,0");
        composition.addCharacters(MAIN_TABLE, 1,0, "1,0");
        composition.addCharacters(MAIN_TABLE, 2,0, "2,0");
        composition.addCharacters(MAIN_TABLE, 2,1, "2,1");
        assertEquals(3, composition.get().allCompositionCells().getRowSize());

        composition.removeCharacters(MAIN_TABLE, 1, 0, 0,3);
        assertEquals(2, composition.get().allCompositionCells().getRowSize());
    }

}
