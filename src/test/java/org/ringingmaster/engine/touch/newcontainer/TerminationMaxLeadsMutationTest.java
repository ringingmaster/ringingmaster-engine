package org.ringingmaster.engine.touch.newcontainer;

import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.ringingmaster.engine.touch.newcontainer.ObservableTouch.TERMINATION_MAX_LEADS_MAX;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class TerminationMaxLeadsMutationTest {

    @Test
    public void hasCorrectDefault() {

        ObservableTouch touch = new ObservableTouch();

        assertEquals(Optional.empty(), touch.get().getTerminationMaxLeads());
    }

    @Test
    public void changeIsReflected(){

        ObservableTouch touch = new ObservableTouch();

        touch.setTerminationMaxLeads(10);
        assertEquals(Optional.of(10), touch.get().getTerminationMaxLeads());

        touch.setTerminationMaxLeads(TERMINATION_MAX_LEADS_MAX);
        assertEquals(Optional.of(TERMINATION_MAX_LEADS_MAX), touch.get().getTerminationMaxLeads());

        touch.setTerminationMaxLeads(1);
        assertEquals(Optional.of(1), touch.get().getTerminationMaxLeads());

        touch.removeTerminationMaxLeads();
        assertEquals(Optional.empty(), touch.get().getTerminationMaxLeads());
    }

    @Test (expected = IllegalArgumentException.class)
    public void setting0OrLowerThrows() throws Exception {

        ObservableTouch touch = new ObservableTouch();
        touch.setTerminationMaxLeads(0);
    }

    @Test (expected = IllegalArgumentException.class)
    public void settingAboveMaxThrows() throws Exception {

        ObservableTouch touch = new ObservableTouch();
        touch.setTerminationMaxLeads(TERMINATION_MAX_LEADS_MAX +1);
    }

}
