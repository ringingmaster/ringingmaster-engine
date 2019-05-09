package org.ringingmaster.engine.observable;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ObservableTest {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Test
    public void behaviourSubjectDownstreamMapsOnce() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        BehaviorSubject<String> subject = BehaviorSubject.createDefault("First");

        Observable<String> mapped = subject.map(s -> {
            log.info("....Do Map [{}]", s);
            return s + " MAPPED";
        })
                //.publish()
                .replay(1)
                .autoConnect(1);

        subject.subscribe(s -> log.info("Subscriber 1 [{}]",s));
        mapped.subscribe(s -> log.info("Subscriber 2 [{}]",s));
        mapped.subscribe(s -> log.info("Subscriber 3 [{}]",s));


        subject.onNext("Second");

        mapped.subscribe(s -> log.info("Subscriber 4 [{}]",s));

        subject.onNext("Third");

        mapped.subscribe(s -> log.info("Subscriber 5 [{}]",s));

    }
}
