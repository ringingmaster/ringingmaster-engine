package org.ringingmaster.engine.parsernew.assignparsetype;

import org.junit.Test;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.arraytable.ImmutableArrayTable;
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.notation.impl.NotationBuilder;
import org.ringingmaster.engine.parsernew.cell.ParsedCell;
import org.ringingmaster.engine.touch.newcontainer.ObservableTouch;
import org.ringingmaster.engine.touch.newcontainer.checkingtype.CheckingType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.ringingmaster.engine.parser.ParseType.CALL;
import static org.ringingmaster.engine.parsernew.assignparsetype.AssertParse.assertParse;
import static org.ringingmaster.engine.parsernew.assignparsetype.AssertParse.parsed;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class AssignParseTypeTest {

    private final Logger log = LoggerFactory.getLogger(AssignParseTypeTest.class);

    @Test
    public void correctlyRetrievesAndParsesFromNotation() {
        ImmutableArrayTable<ParsedCell> parsedCells = buildAndParseSingleCellTouch(buildPlainBobMinor(), "-s");
        assertParse(parsedCells.get(0,0), parsed(CALL),  parsed(CALL));
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
                .setSpliceIdentifier("p")
                .build();
    }

    private ImmutableArrayTable<ParsedCell> buildAndParseSingleCellTouch(NotationBody notationBody, String characters) {
        ObservableTouch touch = new ObservableTouch();
        touch.setNumberOfBells(notationBody.getNumberOfWorkingBells());
        touch.addCharacters(0, 0, characters);
        touch.addNotation(notationBody);
        touch.setTouchCheckingType(CheckingType.COURSE_BASED);
        return new AssignParseType().parse(touch.get());
    }


}