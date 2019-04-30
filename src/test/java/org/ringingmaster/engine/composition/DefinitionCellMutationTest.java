package org.ringingmaster.engine.composition;

import org.junit.Test;
import org.ringingmaster.engine.arraytable.ImmutableArrayTable;
import org.ringingmaster.engine.composition.cell.Cell;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.ringingmaster.engine.composition.TableType.DEFINITION_TABLE;
import static org.ringingmaster.engine.composition.tableaccess.DefinitionTableAccess.DEFINITION_COLUMN;
import static org.ringingmaster.engine.composition.tableaccess.DefinitionTableAccess.SHORTHAND_COLUMN;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class DefinitionCellMutationTest {

    @Test
    public void hasCorrectDefault()  {

        MutableComposition composition = new MutableComposition();

        assertEquals(0, composition.get().allDefinitionCells().getColumnSize());
        assertEquals(0, composition.get().allDefinitionCells().getRowSize());
    }

    @Test
    public void canAddDefinitionThroughAddMethod()  {

        MutableComposition composition = new MutableComposition();
        composition.addDefinition("A", "SL");

        final ImmutableArrayTable<Cell> cells = composition.get().allDefinitionCells();
        assertEquals(1, cells.getRowSize());
        assertEquals(2, cells.getColumnSize());

        Cell shorthandCell = cells.get(0,0);
        Cell definitionCell = cells.get(0,1);
        assertEquals("A", shorthandCell.getCharacters());
        assertEquals(2, definitionCell.size());
        assertEquals('S', definitionCell.get(0));
        assertEquals('L', definitionCell.get(1));
    }

    @Test
    public void canAddDefinitionThroughCellEditing()  {

        MutableComposition composition = new MutableComposition();
        composition.addCharacters(DEFINITION_TABLE,0, SHORTHAND_COLUMN,"A");
        composition.addCharacters(DEFINITION_TABLE,0, DEFINITION_COLUMN,"SL");

        final ImmutableArrayTable<Cell> cells = composition.get().allDefinitionCells();
        assertEquals(1, cells.getRowSize());
        assertEquals(2, cells.getColumnSize());

        Cell shorthandCell = cells.get(0,0);
        Cell definitionCell = cells.get(0,1);
        assertEquals("A", shorthandCell.getCharacters());
        assertEquals(2, definitionCell.size());
        assertEquals('S', definitionCell.get(0));
        assertEquals('L', definitionCell.get(1));
    }

    @Test
    public void addingDefinitionThroughAddMethodWithLeadingWhitespaceTrims()  {

        MutableComposition composition = new MutableComposition();
        composition.addDefinition(" A", "SL");

        assertTrue(composition.get().findDefinitionByShorthand("A").isPresent());
    }

    @Test
    public void addingDefinitionThroughAddMethodWithTrailingWhitespaceTrims()  {

        MutableComposition composition = new MutableComposition();
        composition.addDefinition("A ", "SL");

        assertTrue(composition.get().findDefinitionByShorthand("A").isPresent());
    }

    @Test
    public void addingDefinitionThroughAddMethodWithCentralWhitespaceDoesNotTrim()  {

        MutableComposition composition = new MutableComposition();
        composition.addDefinition("A B", "SL");

        assertTrue(composition.get().findDefinitionByShorthand("A B").isPresent());
    }

    @Test
    public void addingDefinitionThroughCellEditingWithLeadingWhitespaceTrims()  {

        MutableComposition composition = new MutableComposition();
        composition.addCharacters(DEFINITION_TABLE, 0, SHORTHAND_COLUMN, " A");

        assertTrue(composition.get().findDefinitionByShorthand("A").isPresent());
    }

    @Test
    public void addingDefinitionThroughCellEditingWithTrailingWhitespaceTrims()  {

        MutableComposition composition = new MutableComposition();
        composition.addCharacters(DEFINITION_TABLE, 0, SHORTHAND_COLUMN, "A ");

        assertTrue(composition.get().findDefinitionByShorthand("A").isPresent());
    }

    @Test
    public void addingDefinitionThroughCellEditingWithEmbeddedWhitespaceDoesNotTrim()  {

        MutableComposition composition = new MutableComposition();
        composition.addCharacters(DEFINITION_TABLE, 0, SHORTHAND_COLUMN, "A B");

        assertTrue(composition.get().findDefinitionByShorthand("A B").isPresent());
    }

    @Test
    public void canRemoveDefinitionThroughRemoveMethod()  {
        MutableComposition composition = new MutableComposition();
        composition.addDefinition("A", "aa");
        composition.addDefinition("B", "bb");
        composition.addDefinition("C", "cc");

        assertEquals(3, composition.get().allDefinitionCells().getRowSize());
        assertEquals(2, composition.get().allDefinitionCells().getColumnSize());
        composition.removeDefinition("B");

        assertEquals(2, composition.get().allDefinitionCells().getRowSize());
        assertTrue(composition.get().findDefinitionByShorthand("A").isPresent());
        assertFalse(composition.get().findDefinitionByShorthand("B").isPresent());
        assertTrue(composition.get().findDefinitionByShorthand("C").isPresent());
    }

    @Test
    public void canRemoveDefinitionThroughCellEditing()  {
        MutableComposition composition = new MutableComposition();
        composition.addDefinition("A", "aa");
        composition.addDefinition("B", "bb");
        composition.addDefinition("C", "cc");

        assertEquals(3, composition.get().allDefinitionCells().getRowSize());
        assertEquals(2, composition.get().allDefinitionCells().getColumnSize());
        composition.removeCharacters(DEFINITION_TABLE, 1, SHORTHAND_COLUMN,0,1);
        composition.removeCharacters(DEFINITION_TABLE, 1, DEFINITION_COLUMN,0,2);

        assertEquals(2, composition.get().allDefinitionCells().getRowSize());
        assertTrue(composition.get().findDefinitionByShorthand("A").isPresent());
        assertFalse(composition.get().findDefinitionByShorthand("B").isPresent());
        assertTrue(composition.get().findDefinitionByShorthand("C").isPresent());
    }

    @Test(expected = IllegalArgumentException.class)
    public void addingCharactersBeyondTwoColumnsThrows()  {
        MutableComposition composition = new MutableComposition();
        composition.addCharacters(DEFINITION_TABLE, 0,0,"A");
        composition.addCharacters(DEFINITION_TABLE, 0,1,"B");
        composition.addCharacters(DEFINITION_TABLE, 0,2,"C");
    }


    @Test(expected = IllegalArgumentException.class)
    public void addingDuplicateDefinitionThrows() {
        MutableComposition composition = new MutableComposition();
        composition.addDefinition("a", "p-p");
        composition.addDefinition("a", "sp-");
    }

}
