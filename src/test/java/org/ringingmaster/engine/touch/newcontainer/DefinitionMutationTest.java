package org.ringingmaster.engine.touch.newcontainer;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;
import org.junit.Test;
import org.pcollections.PSet;
import org.ringingmaster.engine.touch.newcontainer.definition.Definition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class DefinitionMutationTest {

    @Test
    public void hasCorrectDefault() throws Exception {

        ObservableTouch touch = new ObservableTouch();

        assertEquals(ImmutableSet.of(), touch.get().getAllDefinitions());
    }

    @Test
    public void canAddDefinition() throws Exception {

        ObservableTouch touch = new ObservableTouch();
        touch.addDefinition("A", "SL");

        PSet<Definition> allDefinitions = touch.get().getAllDefinitions();
        assertEquals(1, allDefinitions.size());

        Definition definition = Iterators.getOnlyElement(allDefinitions.iterator());
        assertEquals("A", definition.getShorthand());
        assertEquals(2, definition.size());
        assertEquals('S', definition.getElement(0).getCharacter());
        assertFalse(definition.getElement(0).getVariance().isPresent());
        assertEquals('L', definition.getElement(1).getCharacter());
        assertFalse(definition.getElement(1).getVariance().isPresent());
    }

    @Test
    public void canRemoveDefinition() throws Exception {

        ObservableTouch touch = new ObservableTouch();
        touch.addDefinition("A", "2s");
        touch.addDefinition("B", "-p");
        touch.addDefinition("C", "pp");

        assertEquals(3, touch.get().getAllDefinitions().size());
        touch.removeDefinition("B");

        PSet<Definition> allDefinitions = touch.get().getAllDefinitions();
        assertEquals(2, allDefinitions.size());
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
