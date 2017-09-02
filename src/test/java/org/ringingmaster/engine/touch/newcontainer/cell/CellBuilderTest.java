package org.ringingmaster.engine.touch.newcontainer.cell;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class CellBuilderTest {

    @Test
    public void canBuildEmptyCell() {
        Cell cell = new CellBuilder()
                .defaults()
                .build();

        assertNotNull(cell);
        assertEquals(0, cell.size());
    }

    @Test
    public void canBuildWithAddedCharacters() {
        Cell cell = new CellBuilder()
                .defaults()
                .insert(0, "ABC")
                .build();

        assertEquals("ABC", cell.getCharacters());
    }

    @Test
    public void canInsertCharacters() {

        Cell initialCell = new CellBuilder()
                .defaults()
                .insert(0, "ABC")
                .build();

        Cell cell = new CellBuilder()
                .prototypeOf(initialCell)
                .insert(1, "123")
                .build();

        assertEquals("A123BC", cell.getCharacters());
    }

    @Test
    public void canDeleteCharacters() {

        Cell initialCell = new CellBuilder()
                .defaults()
                .insert(0, "ABC")
                .build();

        Cell cell = new CellBuilder()
                .prototypeOf(initialCell)
                .delete(1, 1)
                .build();

        assertEquals("AC", cell.getCharacters());
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void insertingOutOfRangeThrows() {
        new CellBuilder()
                .defaults()
                .insert(1, "ABC")
                .build();
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void deletingOutOfRangeThrows() {
        new CellBuilder()
                .defaults()
                .delete(0, 1)
                .build();
    }
}