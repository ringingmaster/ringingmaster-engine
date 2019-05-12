package org.ringingmaster.engine.parser;

import org.junit.Test;
import org.ringingmaster.engine.composition.MutableComposition;

import static org.ringingmaster.engine.composition.TableType.COMPOSITION_TABLE;
import static org.ringingmaster.engine.composition.TableType.DEFINITION_TABLE;
import static org.ringingmaster.engine.composition.compositiontype.CompositionType.LEAD_BASED;

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

    @Test
    public void parsingDefinitionShorthandWithoutDefinitionDoesNotThrow() {
        MutableComposition composition = new MutableComposition();
        composition.setCompositionType(LEAD_BASED);
        composition.addCharacters(COMPOSITION_TABLE, 0,0, "DEF_1");
        composition.addCharacters(DEFINITION_TABLE, 0,0, "DEF_1");

        parser.apply(composition.get());
    }

}
