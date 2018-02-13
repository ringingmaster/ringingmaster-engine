package org.ringingmaster.engine.parser.assignparse;

import org.junit.Test;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.notation.impl.NotationBuilder;
import org.ringingmaster.engine.parser.parse.Parse;
import org.ringingmaster.engine.touch.ObservableTouch;
import org.ringingmaster.engine.touch.TableType;
import org.ringingmaster.engine.touch.checkingtype.CheckingType;

import static org.ringingmaster.engine.parser.AssertParse.assertParse;
import static org.ringingmaster.engine.parser.AssertParse.unparsed;
import static org.ringingmaster.engine.parser.AssertParse.valid;
import static org.ringingmaster.engine.parser.ParseType.CALL;
import static org.ringingmaster.engine.parser.ParseType.DEFINITION;
import static org.ringingmaster.engine.parser.ParseType.SPLICE;
import static org.ringingmaster.engine.touch.TableType.TOUCH_TABLE;
import static org.ringingmaster.engine.touch.checkingtype.CheckingType.COURSE_BASED;
import static org.ringingmaster.engine.touch.tableaccess.DefinitionTableAccess.DEFINITION_COLUMN;
import static org.ringingmaster.engine.touch.tableaccess.DefinitionTableAccess.SHORTHAND_COLUMN;

/**
 * @author stevelake
 */
public class AssignParseTypeDEFINITIONTest {

    @Test
    public void definitionUnparsedInCallingPoitionArea() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), "def1");
        touch.setTouchCheckingType(COURSE_BASED);
        touch.setSpliced(true);

        Parse parse = new AssignParseType().apply(touch.get());
        assertParse(parse.allTouchCells().get(0, 0), unparsed(4));
    }

    @Test
    public void definitionParsedInMainBody() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), "def1");

        Parse parse = new AssignParseType().apply(touch.get());
        assertParse(parse.allTouchCells().get(0, 0), valid(4, DEFINITION));
    }

    @Test
    public void definitionParsedInSplice() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), "-");
        touch.addCharacters(TOUCH_TABLE,0,1,"def1");
        touch.setSpliced(true);

        Parse parse = new AssignParseType().apply(touch.get());
        assertParse(parse.allTouchCells().get(0, 1), valid(4, DEFINITION));
    }

    @Test
    public void correctlyParsesDefinitionTokenInMainBody() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), "-def1-");

        Parse parse = new AssignParseType().apply(touch.get());
        assertParse(parse.allTouchCells().get(0, 0), valid(CALL), valid(4, DEFINITION), valid(CALL));
    }

    @Test
    public void correctlyParsesDefinitionTokenInSplice() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), "-");
        touch.setSpliced(true);
        touch.addCharacters(TOUCH_TABLE, 0, 1, "Pdef1P");

        Parse parse = new AssignParseType().apply(touch.get());
        assertParse(parse.allTouchCells().get(0, 1), valid(SPLICE), valid(4, DEFINITION), valid(SPLICE));
    }

    @Test
    public void definitionShorthandsParsedAsDefinitions() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), null);
        touch.addDefinition("def2","s");

        Parse parse = new AssignParseType().apply(touch.get());
        assertParse(parse.findDefinitionByShorthand("def1").get().get(0, SHORTHAND_COLUMN), valid(4, DEFINITION));
        assertParse(parse.findDefinitionByShorthand("def2").get().get(0, SHORTHAND_COLUMN), valid(4, DEFINITION));
    }

    @Test
    public void unusedDefinitionParsedAsMainBody() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), null);

        Parse parse = new AssignParseType().apply(touch.get());
        assertParse(parse.findDefinitionByShorthand("def1").get().get(0, DEFINITION_COLUMN), valid(CALL), unparsed());
    }

    @Test
    public void definitionUsedInMainBodyAreaParsedAsMainBody() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), "def1 def2");

        Parse parse = new AssignParseType().apply(touch.get());
        assertParse(parse.findDefinitionByShorthand("def1").get().get(0, DEFINITION_COLUMN), valid(CALL), unparsed());
    }

    @Test
    public void definitionUsedInSpiceAreaParsedAsSpice() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), "s");
        touch.addCharacters(TOUCH_TABLE, 0,1,"def1 def2");
        touch.setSpliced(true);

        Parse parse = new AssignParseType().apply(touch.get());
        assertParse(parse.findDefinitionByShorthand("def1").get().get(0, DEFINITION_COLUMN), unparsed(), valid(SPLICE));
    }

    @Test
    public void definitionUsedInMainAndSpiceAreaParsedAsMainBody() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), "def1");
        touch.addCharacters(TOUCH_TABLE, 0,1,"def1");
        touch.setSpliced(true);

        Parse parse = new AssignParseType().apply(touch.get());
        assertParse(parse.findDefinitionByShorthand("def1").get().get(0, DEFINITION_COLUMN), valid(CALL), unparsed());
    }

    @Test
    public void transativeDefinitionInMainBodyParsedAsMainBody() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), "def3");
        touch.addDefinition("def3", "def2");
        touch.addDefinition("def2", "def1");

        Parse parse = new AssignParseType().apply(touch.get());
        assertParse(parse.findDefinitionByShorthand("def3").get().get(0, DEFINITION_COLUMN), valid(4, DEFINITION));
        assertParse(parse.findDefinitionByShorthand("def2").get().get(0, DEFINITION_COLUMN), valid(4, DEFINITION));
        assertParse(parse.findDefinitionByShorthand("def1").get().get(0, DEFINITION_COLUMN), valid(CALL), unparsed());
    }

    @Test
    public void transativeDefinitionInSplicedParsedAsSplice() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), "-");
        touch.addCharacters(TableType.TOUCH_TABLE,0,1,"def3");
        touch.setSpliced(true);
        touch.addDefinition("def3", "def2");
        touch.addDefinition("def2", "def1");

        Parse parse = new AssignParseType().apply(touch.get());

        assertParse(parse.findDefinitionByShorthand("def3").get().get(0, DEFINITION_COLUMN), valid(4, DEFINITION));
        assertParse(parse.findDefinitionByShorthand("def2").get().get(0, DEFINITION_COLUMN), valid(4, DEFINITION));
        assertParse(parse.findDefinitionByShorthand("def1").get().get(0, DEFINITION_COLUMN), unparsed(), valid(SPLICE));
    }

    @Test
    public void transativeDefinitionInSplicedAndMainBodyParsedAsMainBody() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), "def33");
        touch.addCharacters(TableType.TOUCH_TABLE,0,1,"def3");
        touch.setSpliced(true);
        touch.addDefinition("def3", "def2");
        touch.addDefinition("def2", "def1");

        Parse parse = new AssignParseType().apply(touch.get());

        assertParse(parse.findDefinitionByShorthand("def3").get().get(0, DEFINITION_COLUMN), valid(4, DEFINITION));
        assertParse(parse.findDefinitionByShorthand("def2").get().get(0, DEFINITION_COLUMN), valid(4, DEFINITION));
        assertParse(parse.findDefinitionByShorthand("def1").get().get(0, DEFINITION_COLUMN), valid(CALL), unparsed());
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
        touch.addDefinition("def1", "-P");
        return touch;
    }

}
