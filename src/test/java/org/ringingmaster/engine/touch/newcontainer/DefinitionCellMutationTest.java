package org.ringingmaster.engine.touch.newcontainer;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;
import org.junit.Test;
import org.pcollections.PSet;
import org.ringingmaster.engine.touch.newcontainer.definition.DefinitionCell;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class DefinitionCellMutationTest {

    @Test
    public void hasCorrectDefault() throws Exception {

        ObservableTouch touch = new ObservableTouch();

        assertEquals(ImmutableSet.of(), touch.get().getAllDefinitions());
    }

    @Test
    public void canAddDefinition() throws Exception {

        ObservableTouch touch = new ObservableTouch();
        touch.addDefinition("A", "SL");

        PSet<DefinitionCell> allDefinitionCells = touch.get().getAllDefinitions();
        assertEquals(1, allDefinitionCells.size());

        DefinitionCell definitionCell = Iterators.getOnlyElement(allDefinitionCells.iterator());
        assertEquals("A", definitionCell.getShorthand());
        assertEquals(2, definitionCell.getElementSize());
        assertEquals("S", definitionCell.getElement(0).getCharacter());
        assertFalse(definitionCell.getElement(0).getVariance().isPresent());
        assertEquals("L", definitionCell.getElement(1).getCharacter());
        assertFalse(definitionCell.getElement(1).getVariance().isPresent());
    }

    @Test
    public void canRemoveDefinition() throws Exception {

        ObservableTouch touch = new ObservableTouch();
        touch.addDefinition("A", "2s");
        touch.addDefinition("B", "-p");
        touch.addDefinition("C", "pp");

        assertEquals(3, touch.get().getAllDefinitions().size());
        touch.removeDefinition("B");

        PSet<DefinitionCell> allDefinitionCells = touch.get().getAllDefinitions();
        assertEquals(2, allDefinitionCells.size());
        assertTrue(touch.get().findDefinitionByShorthand("A").isPresent());
        assertFalse(touch.get().findDefinitionByShorthand("B").isPresent());
        assertTrue(touch.get().findDefinitionByShorthand("C").isPresent());
    }


    @Test(expected = IllegalArgumentException.class)
    public void addingDuplicateDefinitionThrows() {
        ObservableTouch touch = new ObservableTouch();
        touch.addDefinition("a", "p-p");
        touch.addDefinition("a", "sp-");
    }

}
