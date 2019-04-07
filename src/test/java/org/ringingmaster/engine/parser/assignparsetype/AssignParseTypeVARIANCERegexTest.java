package org.ringingmaster.engine.parser.assignparsetype;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.notation.impl.NotationBuilder;
import org.ringingmaster.engine.parser.AssertParse.Expected;
import org.ringingmaster.engine.parser.parse.Parse;
import org.ringingmaster.engine.touch.ObservableTouch;
import org.ringingmaster.engine.touch.checkingtype.CheckingType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

import static org.ringingmaster.engine.parser.AssertParse.assertParse;
import static org.ringingmaster.engine.parser.AssertParse.section;
import static org.ringingmaster.engine.parser.AssertParse.unparsed;
import static org.ringingmaster.engine.parser.AssertParse.valid;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.DEFAULT_CALL_MULTIPLIER;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.VARIANCE_CLOSE;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.VARIANCE_DETAIL;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.VARIANCE_OPEN;
import static org.ringingmaster.engine.touch.TableType.TOUCH_TABLE;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
@RunWith(Parameterized.class)
public class AssignParseTypeVARIANCERegexTest {

    private final Logger log = LoggerFactory.getLogger(AssignParseTypeVARIANCERegexTest.class);

    @Parameterized.Parameters
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][] {

                // test value,              valid
                { "[",                      new Expected[] {unparsed()} },
                { "[]",                     new Expected[] {unparsed(), valid(VARIANCE_CLOSE)} },

                { "[+",                     new Expected[] {unparsed(2)} },
                { "[++",                    new Expected[] {unparsed(3)} },
                { "[+0",                    new Expected[] {valid(section(VARIANCE_OPEN), section(2, VARIANCE_DETAIL))} },
                { "[+1",                    new Expected[] {valid(section(VARIANCE_OPEN), section(2, VARIANCE_DETAIL))} },
                { "[+10",                   new Expected[] {valid(section(VARIANCE_OPEN), section(3, VARIANCE_DETAIL))} },
                { "[+1,2",                  new Expected[] {valid(section(VARIANCE_OPEN), section(4, VARIANCE_DETAIL))} },
                { "[+11,20,",               new Expected[] {valid(section(VARIANCE_OPEN), section(6, VARIANCE_DETAIL)), unparsed()} },
                { "[+,1",                   new Expected[] {unparsed(3), valid(DEFAULT_CALL_MULTIPLIER)} },
                { "[+,1,",                  new Expected[] {unparsed(3), valid(DEFAULT_CALL_MULTIPLIER),unparsed(1)} },

                { "[+O",                    new Expected[] {valid(section(VARIANCE_OPEN), section(2, VARIANCE_DETAIL))} },
                { "[+O",                    new Expected[] {valid(section(VARIANCE_OPEN), section(2, VARIANCE_DETAIL))} },
                { "[+O,",                   new Expected[] {valid(section(VARIANCE_OPEN), section(2, VARIANCE_DETAIL)), unparsed()} },
                { "[+O1",                   new Expected[] {valid(section(VARIANCE_OPEN), section(2, VARIANCE_DETAIL)), valid(DEFAULT_CALL_MULTIPLIER)} },
                { "[++",                    new Expected[] {unparsed(3)} },

                { "[+o",                    new Expected[] {valid(section(VARIANCE_OPEN), section(2, VARIANCE_DETAIL))} },
                { "[+o,",                   new Expected[] {valid(section(VARIANCE_OPEN), section(2, VARIANCE_DETAIL)), unparsed()} },
                { "[+o1",                   new Expected[] {valid(section(VARIANCE_OPEN), section(2, VARIANCE_DETAIL)), valid(DEFAULT_CALL_MULTIPLIER)} },
                { "[++",                    new Expected[] {unparsed(3)} },

                { "[+odd",                  new Expected[] {valid(section(VARIANCE_OPEN), section(4, VARIANCE_DETAIL))} },
                { "[+OdD",                  new Expected[] {valid(section(VARIANCE_OPEN), section(4, VARIANCE_DETAIL))} },
                { "[+1odd",                 new Expected[] {valid(section(VARIANCE_OPEN), section(2, VARIANCE_DETAIL)), unparsed(3)} },
                { "[+odd1",                 new Expected[] {valid(section(VARIANCE_OPEN), section(4, VARIANCE_DETAIL)), valid(DEFAULT_CALL_MULTIPLIER)} },

                { "[+even",                 new Expected[] {valid(section(VARIANCE_OPEN), section(5, VARIANCE_DETAIL))} },
                { "[+EvEn",                 new Expected[] {valid(section(VARIANCE_OPEN), section(5, VARIANCE_DETAIL))} },
                { "[+e",                    new Expected[] {valid(section(VARIANCE_OPEN), section(2, VARIANCE_DETAIL))} },
                { "[+E",                    new Expected[] {valid(section(VARIANCE_OPEN), section(2, VARIANCE_DETAIL))} },


        });
    }

    @Parameterized.Parameter(0)
    public String testValue;

    @Parameterized.Parameter(1)
    public Expected[] expecteds;


    @Test
    public void validStringParsesAll() {

        log.info("Test Value  {}", testValue);
        ObservableTouch touch = new ObservableTouch();
        touch.addNotation(buildPlainBobMinor());
        touch.setCheckingType(CheckingType.LEAD_BASED);
        touch.setSpliced(false);
        touch.addCharacters(TOUCH_TABLE,0,0,testValue);

        Parse parse = new AssignParseType().apply(touch.get());

        assertParse(parse.allTouchCells().get(0, 0), expecteds);
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


