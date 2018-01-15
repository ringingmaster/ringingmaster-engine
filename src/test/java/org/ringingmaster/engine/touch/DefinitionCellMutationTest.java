package org.ringingmaster.engine.touch;

import org.junit.Test;
import org.ringingmaster.engine.arraytable.ImmutableArrayTable;
import org.ringingmaster.engine.touch.cell.Cell;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.ringingmaster.engine.touch.TableType.DEFINITION_TABLE;
import static org.ringingmaster.engine.touch.tableaccess.DefinitionTableAccess.DEFINITION_COLUMN;
import static org.ringingmaster.engine.touch.tableaccess.DefinitionTableAccess.SHORTHAND_COLUMN;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class DefinitionCellMutationTest {

    @Test
    public void hasCorrectDefault()  {

        ObservableTouch touch = new ObservableTouch();

        assertEquals(0, touch.get().allDefinitionCells().getColumnSize());
        assertEquals(0, touch.get().allDefinitionCells().getRowSize());
    }

    @Test
    public void canAddDefinitionThroughAddMethod()  {

        ObservableTouch touch = new ObservableTouch();
        touch.addDefinition("A", "SL");

        final ImmutableArrayTable<Cell> cells = touch.get().allDefinitionCells();
        assertEquals(1, cells.getRowSize());
        assertEquals(2, cells.getColumnSize());

        Cell shorthandCell = cells.get(0,0);
        Cell definitionCell = cells.get(0,1);
        assertEquals("A", shorthandCell.getCharacters());
        assertEquals(2, definitionCell.getElementSize());
        assertEquals("S", definitionCell.getElement(0).getCharacter());
        assertFalse(definitionCell.getElement(0).getVariance().isPresent());
        assertEquals("L", definitionCell.getElement(1).getCharacter());
        assertFalse(definitionCell.getElement(1).getVariance().isPresent());
    }

    @Test
    public void canAddDefinitionThroughCellEditing()  {

        ObservableTouch touch = new ObservableTouch();
        touch.addCharacters(DEFINITION_TABLE,0, SHORTHAND_COLUMN,"A");
        touch.addCharacters(DEFINITION_TABLE,0, DEFINITION_COLUMN,"SL");

        final ImmutableArrayTable<Cell> cells = touch.get().allDefinitionCells();
        assertEquals(1, cells.getRowSize());
        assertEquals(2, cells.getColumnSize());

        Cell shorthandCell = cells.get(0,0);
        Cell definitionCell = cells.get(0,1);
        assertEquals("A", shorthandCell.getCharacters());
        assertEquals(2, definitionCell.getElementSize());
        assertEquals("S", definitionCell.getElement(0).getCharacter());
        assertFalse(definitionCell.getElement(0).getVariance().isPresent());
        assertEquals("L", definitionCell.getElement(1).getCharacter());
        assertFalse(definitionCell.getElement(1).getVariance().isPresent());
    }

    @Test
    public void addingDefinitionThroughAddMethodWithLeadingWhitespaceTrims()  {

        ObservableTouch touch = new ObservableTouch();
        touch.addDefinition(" A", "SL");

        assertTrue(touch.get().findDefinitionByShorthand("A").isPresent());
    }

    @Test
    public void addingDefinitionThroughAddMethodWithTrailingWhitespaceTrims()  {

        ObservableTouch touch = new ObservableTouch();
        touch.addDefinition("A ", "SL");

        assertTrue(touch.get().findDefinitionByShorthand("A").isPresent());
    }

    @Test
    public void addingDefinitionThroughAddMethodWithCentralWhitespaceDoesNotTrim()  {

        ObservableTouch touch = new ObservableTouch();
        touch.addDefinition("A B", "SL");

        assertTrue(touch.get().findDefinitionByShorthand("A B").isPresent());
    }

    @Test
    public void addingDefinitionThroughCellEditingWithLeadingWhitespaceTrims()  {

        ObservableTouch touch = new ObservableTouch();
        touch.addCharacters(DEFINITION_TABLE, 0, SHORTHAND_COLUMN, " A");

        assertTrue(touch.get().findDefinitionByShorthand("A").isPresent());
    }

    @Test
    public void addingDefinitionThroughCellEditingWithTrailingWhitespaceTrims()  {

        ObservableTouch touch = new ObservableTouch();
        touch.addCharacters(DEFINITION_TABLE, 0, SHORTHAND_COLUMN, "A ");

        assertTrue(touch.get().findDefinitionByShorthand("A").isPresent());
    }

    @Test
    public void addingDefinitionThroughCellEditingWithEmbeddedWhitespaceDoesNotTrim()  {

        ObservableTouch touch = new ObservableTouch();
        touch.addCharacters(DEFINITION_TABLE, 0, SHORTHAND_COLUMN, "A B");

        assertTrue(touch.get().findDefinitionByShorthand("A B").isPresent());
    }

    @Test
    public void canRemoveDefinitionThroughRemoveMethod()  {
        ObservableTouch touch = new ObservableTouch();
        touch.addDefinition("A", "aa");
        touch.addDefinition("B", "bb");
        touch.addDefinition("C", "cc");

        assertEquals(3, touch.get().allDefinitionCells().getRowSize());
        assertEquals(2, touch.get().allDefinitionCells().getColumnSize());
        touch.removeDefinition("B");

        assertEquals(2, touch.get().allDefinitionCells().getRowSize());
        assertTrue(touch.get().findDefinitionByShorthand("A").isPresent());
        assertFalse(touch.get().findDefinitionByShorthand("B").isPresent());
        assertTrue(touch.get().findDefinitionByShorthand("C").isPresent());
    }

    @Test
    public void canRemoveDefinitionThroughCellEditing()  {
        ObservableTouch touch = new ObservableTouch();
        touch.addDefinition("A", "aa");
        touch.addDefinition("B", "bb");
        touch.addDefinition("C", "cc");

        assertEquals(3, touch.get().allDefinitionCells().getRowSize());
        assertEquals(2, touch.get().allDefinitionCells().getColumnSize());
        touch.removeCharacters(DEFINITION_TABLE, 1, SHORTHAND_COLUMN,0,1);
        touch.removeCharacters(DEFINITION_TABLE, 1, DEFINITION_COLUMN,0,2);

        assertEquals(2, touch.get().allDefinitionCells().getRowSize());
        assertTrue(touch.get().findDefinitionByShorthand("A").isPresent());
        assertFalse(touch.get().findDefinitionByShorthand("B").isPresent());
        assertTrue(touch.get().findDefinitionByShorthand("C").isPresent());
    }

    @Test(expected = IllegalArgumentException.class)
    public void addingCharactersBeyondTwoColumnsThrows()  {
        ObservableTouch touch = new ObservableTouch();
        touch.addCharacters(DEFINITION_TABLE, 0,0,"A");
        touch.addCharacters(DEFINITION_TABLE, 0,1,"B");
        touch.addCharacters(DEFINITION_TABLE, 0,2,"C");
    }


    @Test(expected = IllegalArgumentException.class)
    public void addingDuplicateDefinitionThrows() {
        ObservableTouch touch = new ObservableTouch();
        touch.addDefinition("a", "p-p");
        touch.addDefinition("a", "sp-");
    }

}
