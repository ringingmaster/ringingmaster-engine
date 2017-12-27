package org.ringingmaster.engine.touch.container;

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
 * @author Lake
 */
public class ObservableTouchTest {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Test
    public void observerSeesChanges() throws Exception {

        ArgumentCaptor<Touch> argumentMatch1 = ArgumentCaptor.forClass(Touch.class);
        Consumer touchConsumer1 = mock(Consumer.class);

        ObservableTouch observableTouch = new ObservableTouch();
        observableTouch.observable().subscribe(touchConsumer1);

        observableTouch.setTitle("0");
        observableTouch.setTitle("1");
        verify(touchConsumer1, times(2)).accept(argumentMatch1.capture());
        assertEquals("0", argumentMatch1.getAllValues().get(0).getTitle());
        assertEquals("1", argumentMatch1.getAllValues().get(1).getTitle());

    }

    @Test
    public void lateJoinerGetsCurrentItem() throws Exception {

        ObservableTouch observableTouch = new ObservableTouch();
        observableTouch.setTitle("0");

        ArgumentCaptor<Touch> argumentMatch = ArgumentCaptor.forClass(Touch.class);
        Consumer touchConsumer = mock(Consumer.class);

        observableTouch.observable().subscribe(touchConsumer);
        verify(touchConsumer).accept(argumentMatch.capture());
    }
}