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
import static org.ringingmaster.engine.parser.ParseType.CALLING_POSITION;
import static org.ringingmaster.engine.parser.ParseType.PLAIN_LEAD;
import static org.ringingmaster.engine.parser.ParseType.SPLICE;
import static org.ringingmaster.engine.parser.ParseType.WHITESPACE;
import static org.ringingmaster.engine.parsernew.assignparsetype.AssertParse.assertParse;
import static org.ringingmaster.engine.parsernew.assignparsetype.AssertParse.parsed;
import static org.ringingmaster.engine.parsernew.assignparsetype.AssertParse.unparsed;
import static org.ringingmaster.engine.touch.newcontainer.checkingtype.CheckingType.COURSE_BASED;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class AssignParseTypeTest {

    private final Logger log = LoggerFactory.getLogger(AssignParseTypeTest.class);

    @Test
    public void correctlyRetrievesAndParsesFromNotation() {
        ObservableTouch touch = buildAndParseSingleCellTouch(buildPlainBobMinor(), "-s");
        ImmutableArrayTable<ParsedCell> parsedCells = new AssignParseType().parse(touch.get());
        assertParse(parsedCells.get(0,0), parsed(CALL),  parsed(CALL));
    }

    @Test
	public void correctlyParsesSimpleWhitespace() {
        ObservableTouch touch = buildAndParseSingleCellTouch(buildPlainBobMinor(), "- Bob");
        ImmutableArrayTable<ParsedCell> parsedCells = new AssignParseType().parse(touch.get());
        assertParse(parsedCells.get(0,0), parsed(CALL),  parsed(WHITESPACE), parsed(3, CALL));
	}

    @Test
	public void correctlyParsesPlainLeadToken() {
        ObservableTouch touch = buildAndParseSingleCellTouch(buildPlainBobMinor(), "-p-");
        ImmutableArrayTable<ParsedCell> parsedCells = new AssignParseType().parse(touch.get());
        assertParse(parsedCells.get(0,0), parsed(CALL),  parsed(PLAIN_LEAD), parsed(CALL));
	}

    @Test
	public void correctlyParsesSpliceToken() {
        ObservableTouch touch = buildAndParseSingleCellTouch(buildPlainBobMinor(), "pp");
        touch.addCharacters(0,1,"-p-");
        touch.setSpliced(true);

        ImmutableArrayTable<ParsedCell> parsedCells = new AssignParseType().parse(touch.get());
        assertParse(parsedCells.get(0,1), unparsed(),  parsed(SPLICE), unparsed());
    }

	@Test
	public void correctlyParsesSimpleCallPosition() {
        ObservableTouch touch = buildAndParseSingleCellTouch(buildPlainBobMinor(), "W");
        touch.addCharacters(1,0,"<Padding to allow enough rows to ensure we have a call position space>");
        touch.setTouchCheckingType(COURSE_BASED);

        ImmutableArrayTable<ParsedCell> parsedCells = new AssignParseType().parse(touch.get());
        assertParse(parsedCells.get(0,0), parsed(CALLING_POSITION));
	}


	@Test
	public void ignoreOtherStuffInCallingPositionCell() {
        ObservableTouch touch = buildAndParseSingleCellTouch(buildPlainBobMinor(), "bHd");
        touch.addCharacters(1,0,"<Padding to allow enough rows to ensure we have a call position space>");
        touch.setTouchCheckingType(COURSE_BASED);

        ImmutableArrayTable<ParsedCell> parsedCells = new AssignParseType().parse(touch.get());
        assertParse(parsedCells.get(0,0), unparsed(),  parsed(CALLING_POSITION), unparsed());



//		assertParseType(touch.getCell_FOR_TEST_ONLY(0, 0), ParseType.UNPARSED, ParseType.CALLING_POSITION, ParseType.UNPARSED);
//		assertValid(touch.getCell_FOR_TEST_ONLY(0, 0), false, true, false);
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

    private ObservableTouch buildAndParseSingleCellTouch(NotationBody notationBody, String characters) {
        ObservableTouch touch = new ObservableTouch();
        touch.setNumberOfBells(notationBody.getNumberOfWorkingBells());
        touch.addCharacters(0, 0, characters);
        touch.addNotation(notationBody);
        touch.setTouchCheckingType(CheckingType.LEAD_BASED);
        touch.setSpliced(false);
        return touch;
    }


}