package org.ringingmaster.engine.newparser;

import io.reactivex.Observable;
import org.junit.Test;
import org.pcollections.HashTreePSet;
import org.pcollections.PSet;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.touch.newcontainer.ObservableTouch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class ParserFactoryTest {
    
    private final Logger log = LoggerFactory.getLogger(ParserFactoryTest.class);

    @Test
    public void doit() {

        ObservableTouch observableTouch = new ObservableTouch();

        Observable<Parse> observer = observableTouch.observable()
                .map(new Parser());

        observer.subscribe(parse -> log.info("1" + parse.toString()));
        observer.subscribe(parse -> log.info("2" + parse.toString()));

        observableTouch.setNumberOfBells(NumberOfBells.BELLS_8) ;
    }


    @Test
    public void pcollection() {

        PSet<String> set = HashTreePSet.empty();
        set = set.plus("something");
        System.out.println(set);
        System.out.println(set.plus("something else"));
        System.out.println(set);
        System.out.println(set.add("hdishadgjh"));
    }

}