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
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.VARIANCE_CLOSE;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.VARIANCE_DETAIL;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.VARIANCE_OPEN;
import static org.ringingmaster.engine.touch.TableType.TOUCH_TABLE;
import static org.ringingmaster.engine.touch.checkingtype.CheckingType.COURSE_BASED;
import static org.ringingmaster.engine.touch.tableaccess.DefinitionTableAccess.DEFINITION_COLUMN;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class AssignParseTypeVARIANCETest {

    @Test
    public void varianceIgnoredInCallingPoitionArea() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), "[-o]");
        touch.setCheckingType(COURSE_BASED);
        touch.setSpliced(true);

        Parse parse = new AssignParseType().apply(touch.get());
        assertParse(parse.allTouchCells().get(0, 0), unparsed(4));
    }

    @Test
    public void varianceParsedInMainBody() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), "[-o]");
        touch.setSpliced(true);

        Parse parse = new AssignParseType().apply(touch.get());
        assertParse(parse.allTouchCells().get(0, 0), valid(VARIANCE_OPEN), valid(2, VARIANCE_DETAIL), valid(VARIANCE_CLOSE));
    }

    @Test
    public void varianceUnparsedInSplice() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), "-");
        touch.addCharacters(TOUCH_TABLE,0,1,"[]");
        touch.setSpliced(true);

        Parse parse = new AssignParseType().apply(touch.get());
        assertParse(parse.allTouchCells().get(0, 1), unparsed(2));
    }

    @Test
    public void varianceParsedInUnusedDefinition() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), "-");

        Parse parse = new AssignParseType().apply(touch.get());

        assertParse(parse.findDefinitionByShorthand("def1").get().get(0, DEFINITION_COLUMN), valid(VARIANCE_OPEN), valid(2, VARIANCE_DETAIL), valid(VARIANCE_CLOSE));
    }

    @Test
    public void varianceParsedInDefinitionUsedInMainBody() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), "def1");

        Parse parse = new AssignParseType().apply(touch.get());

        assertParse(parse.findDefinitionByShorthand("def1").get().get(0, DEFINITION_COLUMN), valid(VARIANCE_OPEN), valid(2, VARIANCE_DETAIL), valid(VARIANCE_CLOSE));
    }

    @Test
    public void varianceUnparsedInDefinitionUsedInSplice() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), "-");
        touch.addCharacters(TOUCH_TABLE,0,1, "def1");
        touch.setSpliced(true);

        Parse parse = new AssignParseType().apply(touch.get());

        assertParse(parse.findDefinitionByShorthand("def1").get().get(0, DEFINITION_COLUMN), unparsed(4));
    }

    @Test
    public void varianceParsedInDefinitionUsedInSpliceAnMainBody() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), "def1");
        touch.addCharacters(TOUCH_TABLE,0,1, "def1");
        touch.setSpliced(true);

        Parse parse = new AssignParseType().apply(touch.get());

        assertParse(parse.findDefinitionByShorthand("def1").get().get(0, DEFINITION_COLUMN), valid(VARIANCE_OPEN), valid(2, VARIANCE_DETAIL), valid(VARIANCE_CLOSE));
    }

    @Test
    public void correctlyIdentifiesVariance() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), "[-o-]s");
        Parse parse = new AssignParseType().apply(touch.get());
        assertParse(parse.allTouchCells().get(0,0), valid(VARIANCE_OPEN), valid(2, VARIANCE_DETAIL), valid(CALL), valid(VARIANCE_CLOSE), valid(CALL));
    }

    @Test
    public void identifiesVarianceTypeWhenNoContent() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), "[-2]");
        Parse parse = new AssignParseType().apply(touch.get());
        assertParse(parse.allTouchCells().get(0,0), valid(VARIANCE_OPEN), valid(2, VARIANCE_DETAIL), valid(VARIANCE_CLOSE));
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
        touch.addDefinition("def1", "[-o]");
        return touch;
    }

}
