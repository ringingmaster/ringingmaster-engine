package org.ringingmaster.engine.parser.call;

import com.google.common.collect.Iterables;
import org.junit.Test;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.composition.MutableComposition;
import org.ringingmaster.engine.notation.Notation;
import org.ringingmaster.engine.notation.NotationBuilder;
import org.ringingmaster.engine.parser.assignparsetype.AssignParseType;
import org.ringingmaster.engine.parser.parse.Parse;
import org.ringingmaster.engine.composition.compositiontype.CompositionType;

import static org.ringingmaster.engine.parser.AssertParse.assertParse;
import static org.ringingmaster.engine.parser.AssertParse.invalid;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.DEFAULT_CALL_MULTIPLIER;
import static org.ringingmaster.engine.composition.TableType.COMPOSITION_TABLE;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
public class ValidateDefaultCallMultiplierFullyDefinedTest {

    @Test
    public void defaultCallNotDefinedInAllMethodsInSplicedSetsInvalid() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "2");
        composition.addCharacters(COMPOSITION_TABLE, 0, 1, "DUMMY");
        composition.addNotation(buildLittleBobMinorWithNoDefaultCall());
        composition.setSpliced(true);

        Parse parse = new AssignParseType()
                .andThen(new ValidateDefaultCallMultiplierFullyDefined())
                .apply(composition.get());

        assertParse(parse.allCompositionCells().get(0,0), invalid(DEFAULT_CALL_MULTIPLIER));
    }

    @Test
    public void defaultCallNotDefinedInChosenMethodSetsInvalid() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "2");
        composition.removeNotation(Iterables.getOnlyElement(composition.get().getAllNotations()));

        Parse parse = new AssignParseType()
                .andThen(new ValidateDefaultCallMultiplierFullyDefined())
                .apply(composition.get());

        assertParse(parse.allCompositionCells().get(0,0), invalid( DEFAULT_CALL_MULTIPLIER, "No default call defined"));
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
                .setSpliceIdentifier("p")
                .build();
    }

    private Notation buildLittleBobMinorWithNoDefaultCall() {
        return NotationBuilder.getInstance()
                .setNumberOfWorkingBells(NumberOfBells.BELLS_6)
                .setName("Little Bob")
                .setFoldedPalindromeNotationShorthand("x16x14", "12")
                .addCallInitiationRow(7)
                .addMethodCallingPosition("W", 7, 1)
                .addMethodCallingPosition("H", 7, 2)
                .setSpliceIdentifier("l")
                .build();
    }

    private MutableComposition buildSingleCellComposition(Notation notation, String characters) {
        MutableComposition composition = new MutableComposition();
        composition.setNumberOfBells(notation.getNumberOfWorkingBells());
        if (characters != null) {
            composition.addCharacters(COMPOSITION_TABLE, 0, 0, characters);
        }
        composition.addNotation(notation);
        composition.setCompositionType(CompositionType.LEAD_BASED);
        composition.setSpliced(false);
        composition.addDefinition("def1", "-P");
        composition.addDefinition("def2", "2");
        return composition;
    }


}