package org.ringingmaster.engine.composition;

import org.junit.Test;
import org.ringingmaster.engine.composition.cell.EmptyCell;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.ringingmaster.engine.composition.TableType.COMPOSITION_TABLE;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
public class CellMutationTest {

    @Test
    public void hasCorrectDefault() throws Exception {

        MutableComposition composition = new MutableComposition();

        assertEquals(0, composition.get().allCompositionCells().getColumnSize());
        assertEquals(0, composition.get().allCompositionCells().getRowSize());
    }

    @Test
    public void addingCharactersToEmptyCellAddsCell() {
        MutableComposition composition = new MutableComposition();

        composition.addCharacters(COMPOSITION_TABLE, 0,0, "ABC");

        assertEquals(1, composition.get().allCompositionCells().getColumnSize());
        assertEquals(1, composition.get().allCompositionCells().getRowSize());
        assertEquals("ABC", composition.get().allCompositionCells().get(0,0).getCharacters());
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void addingCharactersOutsideRowBoundsThrows() {
        MutableComposition composition = new MutableComposition();
        composition.addCharacters(COMPOSITION_TABLE, 1,0, "ABC");
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void addingCharactersOutsideColumnBoundsThrows() {
        MutableComposition composition = new MutableComposition();
        composition.addCharacters(COMPOSITION_TABLE, 0,1, "ABC");
    }

    @Test
    public void addingCharacterToExistingCellAppends() {
        MutableComposition composition = new MutableComposition();
        composition.addCharacters(COMPOSITION_TABLE, 0,0, "ABC");

        composition.addCharacters(COMPOSITION_TABLE, 0,0, "123");
        assertEquals("ABC123", composition.get().allCompositionCells().get(0,0).getCharacters());
    }


    @Test
    public void insertingCharactersToEmptyCellAddsCell() {
        MutableComposition composition = new MutableComposition();

        composition.insertCharacters(COMPOSITION_TABLE, 0,0, 0,"ABC");

        assertEquals(1, composition.get().allCompositionCells().getColumnSize());
        assertEquals(1, composition.get().allCompositionCells().getRowSize());
        assertEquals("ABC", composition.get().allCompositionCells().get(0,0).getCharacters());
    }

    @Test
    public void insertingCharacterToExistingCellInserts() {
        MutableComposition composition = new MutableComposition();
        composition.addCharacters(COMPOSITION_TABLE, 0,0, "ABC");

        composition.insertCharacters(COMPOSITION_TABLE, 0,0, 1, "123");
        assertEquals("A123BC", composition.get().allCompositionCells().get(0,0).getCharacters());
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void insertingCharactersOutsideRowBoundsThrows() {
        MutableComposition composition = new MutableComposition();
        composition.insertCharacters(COMPOSITION_TABLE, 1,0, 0, "ABC");
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void insertingCharactersOutsideColumnBoundsThrows() {
        MutableComposition composition = new MutableComposition();
        composition.insertCharacters(COMPOSITION_TABLE, 0,1, 0, "ABC");
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void insertingCharactersOutsideCellBoundsThrows() {
        MutableComposition composition = new MutableComposition();
        composition.insertCharacters(COMPOSITION_TABLE, 0,0, 1, "ABC");
    }

    @Test
    public void removingSingleCharacterFromExistingCellDeletes() {
        MutableComposition composition = new MutableComposition();
        composition.addCharacters(COMPOSITION_TABLE, 0,0, "ABC");

        composition.removeCharacters(COMPOSITION_TABLE, 0, 0, 1,1);
        assertEquals("AC", composition.get().allCompositionCells().get(0,0).getCharacters());
    }

    @Test
    public void removingCharacterRangeFromExistingCellDeletes() {
        MutableComposition composition = new MutableComposition();
        composition.addCharacters(COMPOSITION_TABLE, 0,0, "ABCD");

        composition.removeCharacters(COMPOSITION_TABLE, 0, 0, 1,2);
        assertEquals("AD", composition.get().allCompositionCells().get(0,0).getCharacters());
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void removingCharactersOutsideRowBoundsThrows() {
        MutableComposition composition = new MutableComposition();
        composition.removeCharacters(COMPOSITION_TABLE, 1,0, 0, 1);
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void removingCharactersOutsideColumnBoundsThrows() {
        MutableComposition composition = new MutableComposition();
        composition.removeCharacters(COMPOSITION_TABLE, 0,1, 0, 1);
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void removingCharactersOutsideCellBoundsThrows() {
        MutableComposition composition = new MutableComposition();
        composition.addCharacters(COMPOSITION_TABLE, 0,0,"A");
        composition.removeCharacters(COMPOSITION_TABLE, 0,0, 1, 1);
    }

    @Test
    public void removingAllCharactersRemovesCell() {
        MutableComposition composition = new MutableComposition();
        composition.addCharacters(COMPOSITION_TABLE, 0,0, "ABC");
        composition.addCharacters(COMPOSITION_TABLE, 0,1, "PADDING");
        composition.addCharacters(COMPOSITION_TABLE, 1,0, "PADDING");

        composition.removeCharacters(COMPOSITION_TABLE, 0, 0, 0,3);
        assertEquals("", composition.get().allCompositionCells().get(0,0).getCharacters());
        assertTrue(composition.get().allCompositionCells().get(0,0) instanceof EmptyCell);
    }

    @Test
    public void removingAllItemsInColumnCausesColumnRemoval() {
        MutableComposition composition = new MutableComposition();
        composition.addCharacters(COMPOSITION_TABLE, 0,0, "0,0");
        composition.addCharacters(COMPOSITION_TABLE, 0,1, "0,1");
        composition.addCharacters(COMPOSITION_TABLE, 0,2, "0,2");
        composition.addCharacters(COMPOSITION_TABLE, 1,2, "1,2");
        assertEquals(3, composition.get().allCompositionCells().getColumnSize());

        composition.removeCharacters(COMPOSITION_TABLE, 0, 1, 0,3);
        assertEquals(2, composition.get().allCompositionCells().getColumnSize());
    }

    @Test
    public void removingAllItemsInRowCausesRowRemoval() {
        MutableComposition composition = new MutableComposition();
        composition.addCharacters(COMPOSITION_TABLE, 0,0, "0,0");
        composition.addCharacters(COMPOSITION_TABLE, 1,0, "1,0");
        composition.addCharacters(COMPOSITION_TABLE, 2,0, "2,0");
        composition.addCharacters(COMPOSITION_TABLE, 2,1, "2,1");
        assertEquals(3, composition.get().allCompositionCells().getRowSize());

        composition.removeCharacters(COMPOSITION_TABLE, 1, 0, 0,3);
        assertEquals(2, composition.get().allCompositionCells().getRowSize());
    }

}
