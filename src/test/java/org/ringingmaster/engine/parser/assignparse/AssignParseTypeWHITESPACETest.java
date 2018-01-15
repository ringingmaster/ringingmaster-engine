package org.ringingmaster.engine.parser.assignparse;

import org.junit.Test;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.notation.impl.NotationBuilder;
import org.ringingmaster.engine.parser.Parse;
import org.ringingmaster.engine.touch.ObservableTouch;
import org.ringingmaster.engine.touch.TableType;
import org.ringingmaster.engine.touch.checkingtype.CheckingType;

import static org.ringingmaster.engine.parser.AssertParse.assertParse;
import static org.ringingmaster.engine.parser.AssertParse.unparsed;
import static org.ringingmaster.engine.parser.AssertParse.valid;
import static org.ringingmaster.engine.parser.ParseType.CALL;
import static org.ringingmaster.engine.parser.ParseType.CALLING_POSITION;
import static org.ringingmaster.engine.parser.ParseType.DEFINITION;
import static org.ringingmaster.engine.parser.ParseType.SPLICE;
import static org.ringingmaster.engine.parser.ParseType.WHITESPACE;
import static org.ringingmaster.engine.touch.TableType.DEFINITION_TABLE;
import static org.ringingmaster.engine.touch.TableType.TOUCH_TABLE;
import static org.ringingmaster.engine.touch.tableaccess.DefinitionTableAccess.DEFINITION_COLUMN;
import static org.ringingmaster.engine.touch.tableaccess.DefinitionTableAccess.SHORTHAND_COLUMN;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class AssignParseTypeWHITESPACETest {

    @Test
    public void correctlyParsesWhitespaceInCallingArea() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), "W H");
        touch.setTouchCheckingType(CheckingType.COURSE_BASED);

        Parse parse = new AssignParseType().apply(touch.get());

        assertParse(parse.allTouchCells().get(0, 0), valid(CALLING_POSITION), valid(WHITESPACE), valid(CALLING_POSITION));
    }

    @Test
    public void correctlyParsesWhitespaceInMainBody() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), "- Bob");

        Parse parse = new AssignParseType().apply(touch.get());

        assertParse(parse.allTouchCells().get(0, 0), valid(CALL), valid(WHITESPACE), valid(3, CALL));
    }

    @Test
    public void correctlyParsesWhitespaceInSplice() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), "-");
        touch.addCharacters(TableType.TOUCH_TABLE,0,1,"P P ");
        touch.setSpliced(true);

        Parse parse = new AssignParseType().apply(touch.get());

        assertParse(parse.allTouchCells().get(0, 1), valid(SPLICE), valid(WHITESPACE), valid(SPLICE), valid(WHITESPACE));
    }

    @Test
    public void correctlyParsesWhitespaceInUnusedDefinition() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), "-");

        Parse parse = new AssignParseType().apply(touch.get());

        assertParse(parse.findDefinitionByShorthand("def1").get().get(0, DEFINITION_COLUMN), valid(CALL), valid(WHITESPACE), valid(CALL));
    }

    @Test
    public void correctlyParsesWhitespaceInDefinitionUsedInMainBody() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), "def1");

        Parse parse = new AssignParseType().apply(touch.get());

        assertParse(parse.findDefinitionByShorthand("def1").get().get(0, DEFINITION_COLUMN), valid(CALL), valid(WHITESPACE), valid(CALL));
    }

    @Test
    public void correctlyParsesWhitespaceInDefinitionUsedInSplice() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), "-");
        touch.addCharacters(TOUCH_TABLE,0,1, "def1");
        touch.setSpliced(true);

        Parse parse = new AssignParseType().apply(touch.get());

        assertParse(parse.findDefinitionByShorthand("def1").get().get(0, DEFINITION_COLUMN), unparsed(), valid(WHITESPACE), unparsed());
    }

    @Test
    public void correctlyParsesWhitespaceInDefinitionUsedInMainBodySplice() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), "def1");
        touch.addCharacters(TOUCH_TABLE,0,1, "def1");
        touch.setSpliced(true);

        Parse parse = new AssignParseType().apply(touch.get());

        assertParse(parse.findDefinitionByShorthand("def1").get().get(0, DEFINITION_COLUMN), valid(CALL), valid(WHITESPACE), valid(CALL));
    }

    @Test
    public void correctlyParsesWhitespaceInDefinitionShorthand() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), "-");
        touch.addCharacters(DEFINITION_TABLE,1,0, " de f2 ");

        Parse parse = new AssignParseType().apply(touch.get());

        assertParse(parse.findDefinitionByShorthand("de f2").get().get(0, SHORTHAND_COLUMN), valid(WHITESPACE), valid(5, DEFINITION), valid(WHITESPACE));
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
        touch.addDefinition("def1", "- -");
        return touch;
    }

}
