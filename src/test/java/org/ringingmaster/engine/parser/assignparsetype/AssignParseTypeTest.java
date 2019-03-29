package org.ringingmaster.engine.parser.assignparsetype;

import org.junit.Test;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.notation.impl.NotationBuilder;
import org.ringingmaster.engine.parser.parse.Parse;
import org.ringingmaster.engine.touch.ObservableTouch;
import org.ringingmaster.engine.touch.checkingtype.CheckingType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.ringingmaster.engine.parser.AssertParse.assertParse;
import static org.ringingmaster.engine.parser.AssertParse.valid;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.CALL;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.PLAIN_LEAD;
import static org.ringingmaster.engine.touch.TableType.TOUCH_TABLE;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class AssignParseTypeTest {

    private final Logger log = LoggerFactory.getLogger(AssignParseTypeTest.class);

    @Test
    public void correctlyRetrievesAndParsesFromNotation() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), "-s");
        Parse parse = new AssignParseType().apply(touch.get());
        assertParse(parse.allTouchCells().get(0, 0), valid(CALL), valid(CALL));
    }

    @Test
    public void correctlyAllocatedOverlappingParsings() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), "Bob");
        touch.setPlainLeadToken("b");

        Parse parse = new AssignParseType().apply(touch.get());
        assertParse(parse.allTouchCells().get(0, 0), valid(3, CALL));
    }

    @Test
    public void correctlyAllocatedAdjacentParsings() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), "Bobb");
        touch.setPlainLeadToken("b");

        Parse parse = new AssignParseType().apply(touch.get());
        assertParse(parse.allTouchCells().get(0, 0), valid(3, CALL), valid(PLAIN_LEAD));
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
        touch.addDefinition("def1", "-P");
        return touch;
    }


}