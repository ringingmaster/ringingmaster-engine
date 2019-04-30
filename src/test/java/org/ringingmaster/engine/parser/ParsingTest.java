package org.ringingmaster.engine.parser;

import org.junit.Test;
import org.ringingmaster.engine.composition.MutableComposition;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
public class ParsingTest {

    private final Parser parser = new Parser();

    @Test
    public void parsingEmptyCompositionDoesNotThrow() {
        MutableComposition composition = new MutableComposition();

        parser.apply(composition.get());
    }

}
