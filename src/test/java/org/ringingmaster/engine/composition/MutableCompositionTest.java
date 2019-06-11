package org.ringingmaster.engine.composition;

import io.reactivex.functions.Consumer;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * TODO Comments
 *
 * @author Steve Lake
 */
public class MutableCompositionTest {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Test
    public void observerSeesChanges() throws Exception {

        ArgumentCaptor<Composition> argumentMatch1 = ArgumentCaptor.forClass(Composition.class);
        Consumer compositionConsumer1 = mock(Consumer.class);

        MutableComposition mutableComposition = new MutableComposition();
        mutableComposition.observable().subscribe(compositionConsumer1);

        mutableComposition.setTitle("0");
        mutableComposition.setTitle("1");
        verify(compositionConsumer1, times(3)).accept(argumentMatch1.capture());
        assertEquals("UNNAMED", argumentMatch1.getAllValues().get(0).getLoggingTag());
        assertEquals("0", argumentMatch1.getAllValues().get(1).getLoggingTag());
        assertEquals("1", argumentMatch1.getAllValues().get(2).getLoggingTag());

    }

    @Test
    public void lateJoinerGetsCurrentItem() throws Exception {

        MutableComposition mutableComposition = new MutableComposition();
        mutableComposition.setTitle("0");

        ArgumentCaptor<Composition> argumentMatch = ArgumentCaptor.forClass(Composition.class);
        Consumer compositionConsumer = mock(Consumer.class);

        mutableComposition.observable().subscribe(compositionConsumer);
        verify(compositionConsumer).accept(argumentMatch.capture());
    }
}