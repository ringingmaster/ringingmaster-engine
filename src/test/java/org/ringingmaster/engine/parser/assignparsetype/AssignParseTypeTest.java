package org.ringingmaster.engine.parser.assignparsetype;

import org.junit.Test;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.composition.MutableComposition;
import org.ringingmaster.engine.notation.Notation;
import org.ringingmaster.engine.notation.NotationBuilder;
import org.ringingmaster.engine.parser.parse.Parse;
import org.ringingmaster.engine.composition.compositiontype.CompositionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.ringingmaster.engine.parser.AssertParse.assertParse;
import static org.ringingmaster.engine.parser.AssertParse.valid;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.CALL;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.PLAIN_LEAD;
import static org.ringingmaster.engine.composition.TableType.MAIN_TABLE;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class AssignParseTypeTest {

    private final Logger log = LoggerFactory.getLogger(AssignParseTypeTest.class);

    @Test
    public void correctlyRetrievesAndParsesFromNotation() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "-s");
        Parse parse = new AssignParseType().apply(composition.get());
        assertParse(parse.allCompositionCells().get(0, 0), valid(CALL), valid(CALL));
    }

    @Test
    public void correctlyAllocatedOverlappingParsings() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "Bob");
        composition.setPlainLeadToken("b");

        Parse parse = new AssignParseType().apply(composition.get());
        assertParse(parse.allCompositionCells().get(0, 0), valid(3, CALL));
    }

    @Test
    public void correctlyAllocatedAdjacentParsings() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "Bobb");
        composition.setPlainLeadToken("b");

        Parse parse = new AssignParseType().apply(composition.get());
        assertParse(parse.allCompositionCells().get(0, 0), valid(3, CALL), valid(PLAIN_LEAD));
    }

    private Notation buildPlainBobMinor() {
        return NotationBuilder.getInstance()
                .setNumberOfWorkingBells(NumberOfBells.BELLS_6)
                .setName("Plain Bob")
                .setFoldedPalindromeNotationShorthand("x16x16x16", "12")
                .addCall("Bob", "-", "14", true)
                .addCall("Single", "s", "1234", false)
                .addCallInitiationRow(7)
                .addMethodCallingPosition("W", 7, 1)
                .addMethodCallingPosition("H", 7, 2)
                .setSpliceIdentifier("P")
                .build();
    }

    private MutableComposition buildSingleCellComposition(Notation notation, String characters) {
        MutableComposition composition = new MutableComposition();
        composition.setNumberOfBells(notation.getNumberOfWorkingBells());
        if (characters != null) {
            composition.addCharacters(MAIN_TABLE, 0, 0, characters);
        }
        composition.addNotation(notation);
        composition.setCompositionType(CompositionType.LEAD_BASED);
        composition.setSpliced(false);
        composition.addDefinition("def1", "-P");
        return composition;
    }


}