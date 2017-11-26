package org.ringingmaster.engine.parsernew.assignparsetype;

import org.junit.Test;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.notation.impl.NotationBuilder;
import org.ringingmaster.engine.parsernew.Parse;
import org.ringingmaster.engine.parsernew.cell.ParsedCell;
import org.ringingmaster.engine.parsernew.multiplecallpositionsinonecell.MultipleCallPositionsInOneCell;
import org.ringingmaster.engine.touch.newcontainer.ObservableTouch;
import org.ringingmaster.engine.touch.newcontainer.checkingtype.CheckingType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertFalse;
import static org.ringingmaster.engine.parser.ParseType.CALL;
import static org.ringingmaster.engine.parser.ParseType.CALLING_POSITION;
import static org.ringingmaster.engine.parser.ParseType.DEFINITION;
import static org.ringingmaster.engine.parser.ParseType.GROUP_CLOSE;
import static org.ringingmaster.engine.parser.ParseType.GROUP_OPEN;
import static org.ringingmaster.engine.parser.ParseType.PLAIN_LEAD;
import static org.ringingmaster.engine.parser.ParseType.SPLICE;
import static org.ringingmaster.engine.parser.ParseType.WHITESPACE;
import static org.ringingmaster.engine.parsernew.AssertParse.assertParse;
import static org.ringingmaster.engine.parsernew.AssertParse.unparsed;
import static org.ringingmaster.engine.parsernew.AssertParse.valid;
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
        Parse parse = new AssignParseType().parse(touch.get());
        assertParse(parse.allCells().get(0, 0), valid(CALL), valid(CALL));
    }

    @Test
    public void correctlyParsesSimpleWhitespace() {
        ObservableTouch touch = buildAndParseSingleCellTouch(buildPlainBobMinor(), "- Bob");
        Parse parse = new AssignParseType().parse(touch.get());
        assertParse(parse.allCells().get(0, 0), valid(CALL), valid(WHITESPACE), valid(3, CALL));
    }

    @Test
    public void correctlyParsesPlainLeadToken() {
        ObservableTouch touch = buildAndParseSingleCellTouch(buildPlainBobMinor(), "-p-");
        Parse parse = new AssignParseType().parse(touch.get());
        assertParse(parse.allCells().get(0, 0), valid(CALL), valid(PLAIN_LEAD), valid(CALL));
    }

    @Test
    public void correctlyParsesSpliceToken() {
        ObservableTouch touch = buildAndParseSingleCellTouch(buildPlainBobMinor(), "pp");
        touch.addCharacters(0, 1, "-p-");
        touch.setSpliced(true);

        Parse parse = new AssignParseType().parse(touch.get());
        assertParse(parse.allCells().get(0, 1), unparsed(), valid(SPLICE), unparsed());
    }

    @Test
    public void correctlyParsesSimpleCallPosition() {
        ObservableTouch touch = buildAndParseSingleCellTouch(buildPlainBobMinor(), "W");
        touch.setTouchCheckingType(COURSE_BASED);

        Parse parse = new AssignParseType().parse(touch.get());
        assertParse(parse.allCells().get(0, 0), valid(CALLING_POSITION));
    }


    @Test
    public void ignoreOtherCharactersInCallingPositionCell() {
        ObservableTouch touch = buildAndParseSingleCellTouch(buildPlainBobMinor(), "bHd");
        touch.setTouchCheckingType(COURSE_BASED);

        Parse parse = new AssignParseType().parse(touch.get());
        assertParse(parse.allCells().get(0, 0), unparsed(), valid(CALLING_POSITION), unparsed());
    }


    @Test
    public void correctlyParsesDefinitionTokenInMainBody() {
        ObservableTouch touch = buildAndParseSingleCellTouch(buildPlainBobMinor(), "-def1-");

        Parse parse = new AssignParseType().parse(touch.get());
        assertParse(parse.allCells().get(0, 0), valid(CALL), valid(4, DEFINITION), valid(CALL));
    }

    @Test
    public void correctlyParsesDefinitionTokenInSplice() {
        ObservableTouch touch = buildAndParseSingleCellTouch(buildPlainBobMinor(), "-");
        touch.setSpliced(true);
        touch.addCharacters(0, 1, "pdef1p");

        Parse parse = new AssignParseType().parse(touch.get());
        assertParse(parse.allCells().get(0, 1), valid(SPLICE), valid(4, DEFINITION), valid(SPLICE));
    }

    @Test
    public void correctlyAllocatedOverlappingParsings() {
        ObservableTouch touch = buildAndParseSingleCellTouch(buildPlainBobMinor(), "Bob");
        touch.setPlainLeadToken("b");

        Parse parse = new AssignParseType().parse(touch.get());
        assertParse(parse.allCells().get(0, 0), valid(3, CALL));
    }

    @Test
    public void correctlyAllocatedAdjacentParsings() {
        ObservableTouch touch = buildAndParseSingleCellTouch(buildPlainBobMinor(), "Bobb");
        touch.setPlainLeadToken("b");

        Parse parse = new AssignParseType().parse(touch.get());
        assertParse(parse.allCells().get(0, 0), valid(3, CALL), valid(PLAIN_LEAD));
    }

    @Test
    public void correctlyIdentifiesGroup() {
        ObservableTouch touch = buildAndParseSingleCellTouch(buildPlainBobMinor(), "(-)s");

        Parse parse = new AssignParseType().parse(touch.get());
        assertParse(parse.allCells().get(0, 0), valid(3, GROUP_OPEN), valid(CALL), valid(GROUP_CLOSE), valid(CALL));
    }

    //TODO need lots of these type of tests for all the different combinations.
    @Test
    public void mainBodyWithCallingPOsitionIsIgnored() {
        ObservableTouch touch = buildAndParseSingleCellTouch(buildPlainBobMinor(), "A");
        touch.addCharacters(1, 0, "W");
        Parse parse = new AssignParseType().parse(touch.get());
        Parse result = new MultipleCallPositionsInOneCell().parse(parse);

        ParsedCell parsedCell = result.allCells().get(1, 0);
        assertFalse(parsedCell.getSectionAtElementIndex(0).isPresent());
        assertFalse(parsedCell.getGroupAtElementIndex(1).isPresent());
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
        touch.addDefinition("def1", "-s");
        return touch;
    }


}