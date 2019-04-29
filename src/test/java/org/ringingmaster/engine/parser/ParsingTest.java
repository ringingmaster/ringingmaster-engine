package org.ringingmaster.engine.parser;

import org.junit.Test;
import org.ringingmaster.engine.composition.ObservableComposition;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
public class ParsingTest {

    private final Parser parser = new Parser();

    @Test
    public void parsingEmptyCompositionDoesNotThrow() {
        ObservableComposition composition = new ObservableComposition();

        parser.apply(composition.get());
    }

}
