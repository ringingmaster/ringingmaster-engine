package org.ringingmaster.engine.parser.assignparse;

import org.junit.Test;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.notation.impl.NotationBuilder;
import org.ringingmaster.engine.parser.parse.Parse;
import org.ringingmaster.engine.touch.ObservableTouch;
import org.ringingmaster.engine.touch.checkingtype.CheckingType;

import static org.ringingmaster.engine.parser.AssertParse.assertParse;
import static org.ringingmaster.engine.parser.AssertParse.unparsed;
import static org.ringingmaster.engine.parser.AssertParse.valid;
import static org.ringingmaster.engine.parser.ParseType.CALLING_POSITION;
import static org.ringingmaster.engine.touch.TableType.TOUCH_TABLE;
import static org.ringingmaster.engine.touch.checkingtype.CheckingType.COURSE_BASED;
import static org.ringingmaster.engine.touch.tableaccess.DefinitionTableAccess.DEFINITION_COLUMN;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class AssignParseTypeCALLING_POSITIONTest {

    @Test
    public void callingPositionParsedInCallingPoitionArea() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), "WH");
        touch.setTouchCheckingType(COURSE_BASED);
        touch.setSpliced(true);

        Parse parse = new AssignParseType().apply(touch.get());
        assertParse(parse.allTouchCells().get(0, 0), valid(CALLING_POSITION), valid(CALLING_POSITION));
    }

    @Test
    public void callingPositionIgnoredInMainBody() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), "W");
        touch.setSpliced(true);

        Parse parse = new AssignParseType().apply(touch.get());
        assertParse(parse.allTouchCells().get(0, 0), unparsed());
    }

    @Test
    public void callingPositionUnparsedInSplice() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), "-");
        touch.addCharacters(TOUCH_TABLE,0,1,"W");
        touch.setSpliced(true);

        Parse parse = new AssignParseType().apply(touch.get());
        assertParse(parse.allTouchCells().get(0, 1), unparsed());
    }

    @Test
    public void callingPositionUnparsedInUnusedDefinition() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), "W");

        Parse parse = new AssignParseType().apply(touch.get());

        assertParse(parse.findDefinitionByShorthand("def1").get().get(0, DEFINITION_COLUMN), unparsed());
    }

    @Test
    public void callingPositionUnparsedInDefinitionUsedInMainBody() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), "def1");

        Parse parse = new AssignParseType().apply(touch.get());

        assertParse(parse.findDefinitionByShorthand("def1").get().get(0, DEFINITION_COLUMN), unparsed());
    }

    @Test
    public void callingPositionParsedInDefinitionUsedInSplice() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), "-");
        touch.addCharacters(TOUCH_TABLE,0,1, "def1");
        touch.setSpliced(true);

        Parse parse = new AssignParseType().apply(touch.get());

        assertParse(parse.findDefinitionByShorthand("def1").get().get(0, DEFINITION_COLUMN), unparsed());
    }

    @Test
    public void callingPositionParsedInDefinitionUsedInSpliceAnMainBody() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), "def1");
        touch.addCharacters(TOUCH_TABLE,0,1, "def1");
        touch.setSpliced(true);

        Parse parse = new AssignParseType().apply(touch.get());

        assertParse(parse.findDefinitionByShorthand("def1").get().get(0, DEFINITION_COLUMN), unparsed());
    }

    @Test
    public void ignoreOtherCharactersInCallingPositionCell() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), "bHd");
        touch.setTouchCheckingType(COURSE_BASED);

        Parse parse = new AssignParseType().apply(touch.get());
        assertParse(parse.allTouchCells().get(0, 0), unparsed(), valid(CALLING_POSITION), unparsed());
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

    private ObservableTouch buildSingleCellTouch(NotationBody notationBody, String characters) {
        ObservableTouch touch = new ObservableTouch();
        touch.setNumberOfBells(notationBody.getNumberOfWorkingBells());
        if (characters != null) {
            touch.addCharacters(TOUCH_TABLE, 0, 0, characters);
        }
        touch.addNotation(notationBody);
        touch.setTouchCheckingType(CheckingType.LEAD_BASED);
        touch.setSpliced(false);
        touch.addDefinition("def1", "W");
        return touch;
    }

}
