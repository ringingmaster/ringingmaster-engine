package org.ringingmaster.engine.parser.assignparsetype;

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
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.CALL;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.MULTIPLIER_GROUP_CLOSE;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.MULTIPLIER_GROUP_OPEN;
import static org.ringingmaster.engine.touch.TableType.TOUCH_TABLE;
import static org.ringingmaster.engine.touch.checkingtype.CheckingType.COURSE_BASED;
import static org.ringingmaster.engine.touch.tableaccess.DefinitionTableAccess.DEFINITION_COLUMN;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class AssignParseTypeGROUPTest {

    @Test
    public void groupIgnoredInCallingPoitionArea() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), "()");
        touch.setCheckingType(COURSE_BASED);
        touch.setSpliced(true);

        Parse parse = new AssignParseType().apply(touch.get());
        assertParse(parse.allTouchCells().get(0, 0), unparsed(2));
    }

    @Test
    public void groupParsedInMainBody() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), "()");
        touch.setSpliced(true);

        Parse parse = new AssignParseType().apply(touch.get());
        assertParse(parse.allTouchCells().get(0, 0), valid(MULTIPLIER_GROUP_OPEN), valid(MULTIPLIER_GROUP_CLOSE));
    }

    @Test
    public void groupUnparsedInSplice() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), "-");
        touch.addCharacters(TOUCH_TABLE,0,1,"()");
        touch.setSpliced(true);

        Parse parse = new AssignParseType().apply(touch.get());
        assertParse(parse.allTouchCells().get(0, 1), valid(MULTIPLIER_GROUP_OPEN), valid(MULTIPLIER_GROUP_CLOSE));
    }

    @Test
    public void groupParsedInUnusedDefinition() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), "-");

        Parse parse = new AssignParseType().apply(touch.get());

        assertParse(parse.findDefinitionByShorthand("def1").get().get(0, DEFINITION_COLUMN), valid(MULTIPLIER_GROUP_OPEN), valid(MULTIPLIER_GROUP_CLOSE));
    }

    @Test
    public void groupParsedInDefinitionUsedInMainBody() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), "def1");

        Parse parse = new AssignParseType().apply(touch.get());

        assertParse(parse.findDefinitionByShorthand("def1").get().get(0, DEFINITION_COLUMN), valid(MULTIPLIER_GROUP_OPEN), valid(MULTIPLIER_GROUP_CLOSE));
    }

    @Test
    public void groupUnparsedInDefinitionUsedInSplice() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), "-");
        touch.addCharacters(TOUCH_TABLE,0,1, "def1");
        touch.setSpliced(true);

        Parse parse = new AssignParseType().apply(touch.get());

        assertParse(parse.findDefinitionByShorthand("def1").get().get(0, DEFINITION_COLUMN), valid(MULTIPLIER_GROUP_OPEN), valid(MULTIPLIER_GROUP_CLOSE));
    }

    @Test
    public void groupParsedInDefinitionUsedInSpliceAnMainBody() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), "def1");
        touch.addCharacters(TOUCH_TABLE,0,1, "def1");
        touch.setSpliced(true);

        Parse parse = new AssignParseType().apply(touch.get());

        assertParse(parse.findDefinitionByShorthand("def1").get().get(0, DEFINITION_COLUMN), valid(MULTIPLIER_GROUP_OPEN), valid(MULTIPLIER_GROUP_CLOSE));
    }

    @Test
    public void correctlyIdentifiesGroup() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), "(-)s");
        Parse parse = new AssignParseType().apply(touch.get());
        assertParse(parse.allTouchCells().get(0,0), valid(MULTIPLIER_GROUP_OPEN), valid(CALL), valid(MULTIPLIER_GROUP_CLOSE), valid(CALL));
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
        touch.setCheckingType(CheckingType.LEAD_BASED);
        touch.setSpliced(false);
        touch.addDefinition("def1", "()");
        return touch;
    }

}
