package org.ringingmaster.engine.touch.newcontainer;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class CellMutationTest {

    @Test
    public void hasCorrectDefault() throws Exception {

        ObservableTouch touch = new ObservableTouch();

        assertEquals(0, touch.get().getColumnCount());
        assertEquals(0, touch.get().getRowCount());
    }

    @Test
    public void addingCellChangesRowCount() {
        ObservableTouch touch = new ObservableTouch();

        //TODO touch.
    }

}
