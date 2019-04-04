package org.ringingmaster.engine.parser.cell.grouping;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.CALL;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.SPLICE;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
public class DefaultSectionTest {

    @Test
    public void getValuesAsExpected() {
        Section section1 = new DefaultSection(0, 1, CALL);
        assertEquals(0, section1.getStartIndex());
        assertEquals(1, section1.getLength());
        assertEquals(1, section1.getEndIndex());
        assertEquals(CALL, section1.getParseType());


        Section section2 = new DefaultSection(10, 5, SPLICE);
        assertEquals(10, section2.getStartIndex());
        assertEquals(5,  section2.getLength());
        assertEquals(15, section2.getEndIndex());
        assertEquals(SPLICE, section2.getParseType());
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotSetNegativeStartIndex() {
        new DefaultSection(-1, 2, CALL);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotSetNegativeLength() {
        new DefaultSection(1, -2, CALL);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotSetZeroLength() {
        new DefaultSection(1, 0, CALL);
    }

    @Test
    public void createStartIndex0OK() {
        new DefaultSection(0, 1, CALL);
    }

    @Test
    public void fallsWithinBoundsCheck() {
        Section section = new DefaultSection(3, 1, CALL);
        assertEquals(false, section.fallsWithin(2));
        assertEquals(true,  section.fallsWithin(3));
        assertEquals(false, section.fallsWithin(4));
    }

    @Test
    public void fallsWithinSectionmBoundsCheck() {
        Section section1 = new DefaultSection(3, 1, CALL);
        assertEquals(false, section1.fallsWithin(new DefaultSection(2, 1, CALL)));
        assertEquals(true, section1.fallsWithin(new DefaultSection(3, 1, CALL)));
        assertEquals(false, section1.fallsWithin(new DefaultSection(4, 1, CALL)));
    }

    @Test
    public void intersectionCheck() {
        Section section = new DefaultSection(3, 3, CALL);

        assertEquals(true,  section.intersection(1,10));

        assertEquals(false, section.intersection(0, 2));
        assertEquals(false, section.intersection(0, 3));
        assertEquals(true,  section.intersection(0, 4));
        assertEquals(true,  section.intersection(0, 6));
        assertEquals(true,  section.intersection(0, 7));

        assertEquals(true,  section.intersection(3, 1));
        assertEquals(true,  section.intersection(3, 2));
        assertEquals(true,  section.intersection(3, 3));

        assertEquals(true,  section.intersection(5, 1));
        assertEquals(true,  section.intersection(5, 3));
        assertEquals(false, section.intersection(6, 3));
    }

    @Test
    public void canSafelyCallToString() {
        Section section = new DefaultSection(3, 3, CALL);

        section.toString();
    }

}