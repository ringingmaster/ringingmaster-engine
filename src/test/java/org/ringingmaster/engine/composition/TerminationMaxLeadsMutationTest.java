package org.ringingmaster.engine.composition;

import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.ringingmaster.engine.composition.ObservableComposition.TERMINATION_MAX_LEADS_MAX;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class TerminationMaxLeadsMutationTest {

    @Test
    public void hasCorrectDefault() {

        ObservableComposition composition = new ObservableComposition();

        assertEquals(Optional.empty(), composition.get().getTerminationMaxLeads());
    }

    @Test
    public void changeIsReflected(){

        ObservableComposition composition = new ObservableComposition();

        composition.setTerminationMaxLeads(10);
        assertEquals(Optional.of(10), composition.get().getTerminationMaxLeads());

        composition.setTerminationMaxLeads(TERMINATION_MAX_LEADS_MAX);
        assertEquals(Optional.of(TERMINATION_MAX_LEADS_MAX), composition.get().getTerminationMaxLeads());

        composition.setTerminationMaxLeads(1);
        assertEquals(Optional.of(1), composition.get().getTerminationMaxLeads());

        composition.removeTerminationMaxLeads();
        assertEquals(Optional.empty(), composition.get().getTerminationMaxLeads());
    }

    @Test (expected = IllegalArgumentException.class)
    public void setting0OrLowerThrows() throws Exception {

        ObservableComposition composition = new ObservableComposition();
        composition.setTerminationMaxLeads(0);
    }

    @Test (expected = IllegalArgumentException.class)
    public void settingAboveMaxThrows() throws Exception {

        ObservableComposition composition = new ObservableComposition();
        composition.setTerminationMaxLeads(TERMINATION_MAX_LEADS_MAX +1);
    }

}
