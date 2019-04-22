package org.ringingmaster.engine.parser.splice;

import org.junit.Test;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.composition.ObservableComposition;
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.notation.impl.NotationBuilder;
import org.ringingmaster.engine.parser.parse.Parse;
import org.ringingmaster.engine.parser.assignparsetype.AssignParseType;
import org.ringingmaster.engine.composition.TableType;
import org.ringingmaster.engine.composition.compositiontype.CompositionType;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.ringingmaster.engine.parser.AssertParse.assertParse;
import static org.ringingmaster.engine.parser.AssertParse.invalid;
import static org.ringingmaster.engine.parser.AssertParse.unparsed;
import static org.ringingmaster.engine.parser.AssertParse.valid;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.CALL;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.DEFINITION;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.SPLICE;
import static org.ringingmaster.engine.composition.TableType.MAIN_TABLE;
import static org.ringingmaster.engine.composition.compositiontype.CompositionType.COURSE_BASED;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class ValidateInUseCallAvailableInEveryMethodWhenSplicedTest {

    @Test
    public void parsingEmptyParseReturnsEmptyParse() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor());

        Parse result = new AssignParseType()
                .andThen(new ValidateInUseCallAvailableInEveryMethodWhenSpliced())
                .apply(composition.get());

        assertEquals(0, result.allCompositionCells().getRowSize());
        assertEquals(0, result.allCompositionCells().getColumnSize());
    }

    @Test
    public void parsingAllCellTypesReturnsOriginals() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor());
        composition.setCheckingType(COURSE_BASED);

        composition.addCharacters(MAIN_TABLE, 0,0, "CALL_POSITION");
        composition.addCharacters(MAIN_TABLE, 1,0, "MAIN_BODY");
        composition.addCharacters(MAIN_TABLE, 1,1, "SPLICE");
        composition.addCharacters(MAIN_TABLE, 2,0, "CALL");// To force the Parse to be replaced
        composition.addCharacters(MAIN_TABLE, 2,1, "CALL");// To force the Parse to be replaced

        Parse result = new AssignParseType()
                .andThen(new ValidateInUseCallAvailableInEveryMethodWhenSpliced())
                .apply(composition.get());

        assertEquals(3, result.allCompositionCells().getRowSize());
        assertEquals(2, result.allCompositionCells().getColumnSize());
        assertEquals("CALL_POSITION", result.allCompositionCells().get(0,0).getCharacters());
        assertEquals("MAIN_BODY", result.allCompositionCells().get(1,0).getCharacters());
        assertEquals("SPLICE", result.allCompositionCells().get(1,1).getCharacters());
    }

    @Test
    public void nonSplicedIgnoresUnusedProblematicNotation() {
        final NotationBody plainBobMinor = buildPlainBobMinor();
        ObservableComposition composition = buildSingleCellComposition(plainBobMinor);
        composition.setSpliced(false);
        composition.addNotation(buildLittleBobMinorWithNoCalls().build());
        composition.setNonSplicedActiveNotation(plainBobMinor);
        composition.addCharacters(TableType.MAIN_TABLE, 0,0, "Bob");
        composition.addCharacters(TableType.MAIN_TABLE, 0,1, "PL");

        Parse result = new AssignParseType()
                .andThen(new ValidateInUseCallAvailableInEveryMethodWhenSpliced())
                .apply(composition.get());

        assertParse(result.allCompositionCells().get(0,0), valid(3, CALL));
        assertParse(result.allCompositionCells().get(0,1), unparsed(2));
    }

    @Test
    public void splicedInvalidatesCallNameWhenCallNameNotAvailableInAllInUseNotations() {
        final NotationBody plainBobMinor = buildPlainBobMinor();
        ObservableComposition composition = buildSingleCellComposition(plainBobMinor);
        composition.addNotation(buildLittleBobMinorWithNoCalls().build());
        composition.addCharacters(TableType.MAIN_TABLE, 0,0, "Bob");
        composition.addCharacters(TableType.MAIN_TABLE, 0,1, "PL");

        Parse result = new AssignParseType()
                .andThen(new ValidateInUseCallAvailableInEveryMethodWhenSpliced())
                .apply(composition.get());

        assertParse(result.allCompositionCells().get(0,0), invalid(3, CALL));
        assertParse(result.allCompositionCells().get(0,1), valid(SPLICE), valid(SPLICE));
    }

    @Test
    public void splicedInvalidatesCallShorthandWhenCallShorthandNotAvailableInAllInUseNotations() {
        final NotationBody plainBobMinor = buildPlainBobMinor();
        ObservableComposition composition = buildSingleCellComposition(plainBobMinor);
        composition.addNotation(buildLittleBobMinorWithNoCalls().build());
        composition.addCharacters(TableType.MAIN_TABLE, 0,0, "-");
        composition.addCharacters(TableType.MAIN_TABLE, 0,1, "PL");

        Parse result = new AssignParseType()
                .andThen(new ValidateInUseCallAvailableInEveryMethodWhenSpliced())
                .apply(composition.get());

        assertParse(result.allCompositionCells().get(0,0), invalid(CALL));
        assertParse(result.allCompositionCells().get(0,1), valid(SPLICE), valid(SPLICE));
    }

    @Test
    public void splicedUsesCallNameWhenCallNameNotAvailableInAllInUnusedNotations() {
        final NotationBody plainBobMinor = buildPlainBobMinor();
        ObservableComposition composition = buildSingleCellComposition(plainBobMinor);
        composition.addNotation(buildLittleBobMinorWithNoCalls().build());
        composition.addCharacters(TableType.MAIN_TABLE, 0,0, "Bob");
        composition.addCharacters(TableType.MAIN_TABLE, 0,1, "P");

        Parse result = new AssignParseType()
                .andThen(new ValidateInUseCallAvailableInEveryMethodWhenSpliced())
                .apply(composition.get());

        assertParse(result.allCompositionCells().get(0,0), valid(3, CALL));
        assertParse(result.allCompositionCells().get(0,1), valid(SPLICE));
    }

    @Test
    public void splicedUsesCallShorthandWhenCallShorthandNotAvailableInAllInUnusedNotations() {
        final NotationBody plainBobMinor = buildPlainBobMinor();
        ObservableComposition composition = buildSingleCellComposition(plainBobMinor);
        composition.addNotation(buildLittleBobMinorWithNoCalls().build());
        composition.addCharacters(TableType.MAIN_TABLE, 0,0, "-");
        composition.addCharacters(TableType.MAIN_TABLE, 0,1, "P");

        Parse result = new AssignParseType()
                .andThen(new ValidateInUseCallAvailableInEveryMethodWhenSpliced())
                .apply(composition.get());

        assertParse(result.allCompositionCells().get(0,0), valid(CALL));
        assertParse(result.allCompositionCells().get(0,1), valid(SPLICE));
    }


    @Test
    public void splicedInvalidatesCallNameWhenCallNameNotAvailableInOnlyUsedNotation() {
        final NotationBody plainBobMinor = buildPlainBobMinor();
        ObservableComposition composition = buildSingleCellComposition(plainBobMinor);
        composition.addNotation(buildLittleBobMinorWithNoCalls().build());
        composition.addCharacters(TableType.MAIN_TABLE, 0,0, "Bob");
        composition.addCharacters(TableType.MAIN_TABLE, 0,1, "L");

        Parse result = new AssignParseType()
                .andThen(new ValidateInUseCallAvailableInEveryMethodWhenSpliced())
                .apply(composition.get());

        assertParse(result.allCompositionCells().get(0,0), invalid(3, CALL));
        assertParse(result.allCompositionCells().get(0,1), valid(SPLICE));
    }

    @Test
    public void splicedInvalidatesCallShorthandWhenCallShorthandNotAvailableInOnlyUsedNotation() {
        final NotationBody plainBobMinor = buildPlainBobMinor();
        ObservableComposition composition = buildSingleCellComposition(plainBobMinor);
        composition.addNotation(buildLittleBobMinorWithNoCalls().build());
        composition.addCharacters(TableType.MAIN_TABLE, 0,0, "-");
        composition.addCharacters(TableType.MAIN_TABLE, 0,1, "L");

        Parse result = new AssignParseType()
                .andThen(new ValidateInUseCallAvailableInEveryMethodWhenSpliced())
                .apply(composition.get());

        assertParse(result.allCompositionCells().get(0,0), invalid(CALL));
        assertParse(result.allCompositionCells().get(0,1), valid(SPLICE));
    }

    @Test
    public void splicedInvalidatesCallNameWhenCallShorthandNotAvailableInNotationWithTooManyBells() {
        final NotationBody plainBobMinor = buildPlainBobMinor();
        ObservableComposition composition = buildSingleCellComposition(plainBobMinor);
        composition.addNotation(buildCambridgeMajorNoCalls());
        composition.addCharacters(TableType.MAIN_TABLE, 0,0, "Bob");
        composition.addCharacters(TableType.MAIN_TABLE, 0,1, "P");

        Parse result = new AssignParseType()
                .andThen(new ValidateInUseCallAvailableInEveryMethodWhenSpliced())
                .apply(composition.get());

        assertParse(result.allCompositionCells().get(0,0), valid(3, CALL));
        assertParse(result.allCompositionCells().get(0,1), valid(SPLICE));
    }

    @Test
    public void splicedInvalidatesCallShorthandWhenCallShorthandNotAvailableInNotationWithTooManyBells() {
        final NotationBody plainBobMinor = buildPlainBobMinor();
        ObservableComposition composition = buildSingleCellComposition(plainBobMinor);
        composition.addNotation(buildCambridgeMajorNoCalls());
        composition.addCharacters(TableType.MAIN_TABLE, 0,0, "-");
        composition.addCharacters(TableType.MAIN_TABLE, 0,1, "P");

        Parse result = new AssignParseType()
                .andThen(new ValidateInUseCallAvailableInEveryMethodWhenSpliced())
                .apply(composition.get());

        assertParse(result.allCompositionCells().get(0,0), valid(CALL));
        assertParse(result.allCompositionCells().get(0,1), valid(SPLICE));
    }

    @Test
    public void splicedInvalidatesCallNameWhenSpliceWithoutCallUsedInDefinition() {
        final NotationBody plainBobMinor = buildPlainBobMinor();
        ObservableComposition composition = buildSingleCellComposition(plainBobMinor);
        composition.addNotation(buildLittleBobMinorWithNoCalls().build());
        composition.addCharacters(TableType.MAIN_TABLE, 0,0, "Bob");
        composition.addCharacters(TableType.MAIN_TABLE, 0,1, "DEF1P");
        composition.addDefinition("DEF1", "L");

        Parse result = new AssignParseType()
                .andThen(new ValidateInUseCallAvailableInEveryMethodWhenSpliced())
                .apply(composition.get());

        assertParse(result.allCompositionCells().get(0,0), invalid(3, CALL));
        assertParse(result.allCompositionCells().get(0,1), valid(4, DEFINITION), valid(SPLICE));
        assertParse(result.findDefinitionByShorthand("DEF1").get().get(0,1), valid(SPLICE));
    }

    @Test
    public void splicedInvalidatesCallShorthandWhenSpliceWithoutCallUsedInDefinition() {
        final NotationBody plainBobMinor = buildPlainBobMinor();
        ObservableComposition composition = buildSingleCellComposition(plainBobMinor);
        composition.addNotation(buildLittleBobMinorWithNoCalls().build());
        composition.addCharacters(TableType.MAIN_TABLE, 0,0, "-");
        composition.addCharacters(TableType.MAIN_TABLE, 0,1, "DEF1P");
        composition.addDefinition("DEF1", "L");

        Parse result = new AssignParseType()
                .andThen(new ValidateInUseCallAvailableInEveryMethodWhenSpliced())
                .apply(composition.get());

        assertParse(result.allCompositionCells().get(0,0), invalid(CALL));
        assertParse(result.allCompositionCells().get(0,1), valid(4, DEFINITION), valid(SPLICE));
        assertParse(result.findDefinitionByShorthand("DEF1").get().get(0,1), valid(SPLICE));
    }

    @Test
    public void splicedInvalidatesCallShorthandInDefinition() {
        final NotationBody plainBobMinor = buildPlainBobMinor();
        ObservableComposition composition = buildSingleCellComposition(plainBobMinor);
        composition.addNotation(buildLittleBobMinorWithNoCalls().build());
        composition.addCharacters(TableType.MAIN_TABLE, 0,0, "DEF1");
        composition.addCharacters(TableType.MAIN_TABLE, 0,1, "PL");
        composition.addDefinition("DEF1", "-");

        Parse result = new AssignParseType()
                .andThen(new ValidateInUseCallAvailableInEveryMethodWhenSpliced())
                .apply(composition.get());

        assertParse(result.allCompositionCells().get(0,0), valid(4, DEFINITION));
        assertParse(result.allCompositionCells().get(0,1), valid(SPLICE), valid(SPLICE));
        assertParse(result.findDefinitionByShorthand("DEF1").get().get(0,1), invalid(CALL));
    }

    @Test
    public void splicedInvalidatesCallNameInDefinition() {
        final NotationBody plainBobMinor = buildPlainBobMinor();
        ObservableComposition composition = buildSingleCellComposition(plainBobMinor);
        composition.addNotation(buildLittleBobMinorWithNoCalls().build());
        composition.addCharacters(TableType.MAIN_TABLE, 0,0, "DEF1");
        composition.addCharacters(TableType.MAIN_TABLE, 0,1, "PL");
        composition.addDefinition("DEF1", "Bob");

        Parse result = new AssignParseType()
                .andThen(new ValidateInUseCallAvailableInEveryMethodWhenSpliced())
                .apply(composition.get());

        assertParse(result.allCompositionCells().get(0,0), valid(4, DEFINITION));
        assertParse(result.allCompositionCells().get(0,1), valid(SPLICE), valid(SPLICE));
        assertParse(result.findDefinitionByShorthand("DEF1").get().get(0,1), invalid(3, CALL));
    }

    @Test
    public void callShorthandDefinedInAllMethodsIsValid() {
        final NotationBody plainBobMinor = buildPlainBobMinor();
        ObservableComposition composition = buildSingleCellComposition(plainBobMinor);
        composition.addNotation(buildDoublePlainBobMinor());
        composition.addCharacters(TableType.MAIN_TABLE, 0,0, "-");
        composition.addCharacters(TableType.MAIN_TABLE, 0,1, "PD");

        Parse result = new AssignParseType()
                .andThen(new ValidateInUseCallAvailableInEveryMethodWhenSpliced())
                .apply(composition.get());

        assertParse(result.allCompositionCells().get(0,0), valid( CALL));
        assertParse(result.allCompositionCells().get(0,1), valid(SPLICE), valid(SPLICE));
    }

    private NotationBody buildPlainBobMinor() {
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

    private NotationBody buildDoublePlainBobMinor() {
        return NotationBuilder.getInstance()
                .setNumberOfWorkingBells(NumberOfBells.BELLS_6)
                .setName("Double Bob")
                .setFoldedPalindromeNotationShorthand("X16X16X56", "12")
                .addCall("Bob", "-", "14", true)
                .addCall("Single", "s", "1234", false)
                .addCallInitiationRow(7)
                .addMethodCallingPosition("W", 7, 1)
                .addMethodCallingPosition("H", 7, 2)
                .setSpliceIdentifier("D")
                .build();
    }

    private NotationBody buildCambridgeMajorNoCalls() {
        return NotationBuilder.getInstance()
                .setNumberOfWorkingBells(NumberOfBells.BELLS_8)
                .setName("Cambridge")
                .setFoldedPalindromeNotationShorthand("x38.x.14.x.258.x.36.x.14.x.58.x.16.x.78", "12")
                .addCall("Call", "c", "14", true)
                .addCallInitiationRow(18)
                .addMethodCallingPosition("W", 18, 1)
                .addMethodCallingPosition("H", 18, 2)
                .setSpliceIdentifier("C")
                .build();
    }

    private NotationBuilder buildLittleBobMinorWithNoCalls() {
        return NotationBuilder.getInstance()
                .setNumberOfWorkingBells(NumberOfBells.BELLS_6)
                .setName("Little Bob")
                .setFoldedPalindromeNotationShorthand("x16x14", "12")
                .addCallInitiationRow(7)
                .addMethodCallingPosition("W", 7, 1)
                .addMethodCallingPosition("H", 7, 2)
                .setSpliceIdentifier("L");
    }

    private ObservableComposition buildSingleCellComposition(NotationBody... notations) {
        ObservableComposition composition = new ObservableComposition();
        composition.setNumberOfBells(notations[0].getNumberOfWorkingBells());
        Arrays.stream(notations).forEach(composition::addNotation);
        composition.setCheckingType(CompositionType.LEAD_BASED);
        composition.setSpliced(true);
        composition.addDefinition("CALL", "-1-");
        composition.addDefinition("SPLICE", "P");
        return composition;
    }

}