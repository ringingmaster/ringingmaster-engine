package org.ringingmaster.engine.method;

import com.google.common.collect.Iterators;
import org.junit.Assert;
import org.junit.Test;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.notation.Place;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DefaultLeadTest {

    private final List<Integer> empty = Collections.emptyList();

    @Test
    public void sequencesMatchPassedInRows() {
        final List<Row> rows = new ArrayList<>();
        rows.add(buildRow(NumberOfBells.BELLS_4, 1, 2, 3, 4));
        rows.add(buildRow(NumberOfBells.BELLS_4, 2, 1, 4, 3));
        rows.add(buildRow(NumberOfBells.BELLS_4, 2, 4, 1, 3));
        rows.add(buildRow(NumberOfBells.BELLS_4, 4, 2, 3, 1));

        final Lead lead = new DefaultLead(NumberOfBells.BELLS_4, 0, rows, empty);

        assertEquals(Arrays.asList(0, 1, 2, 3), lead.getPlaceSequenceForBell(Bell.BELL_1));
        assertEquals(Arrays.asList(1, 0, 0, 1), lead.getPlaceSequenceForBell(Bell.BELL_2));
        assertEquals(Arrays.asList(2, 3, 3, 2), lead.getPlaceSequenceForBell(Bell.BELL_3));
        assertEquals(Arrays.asList(3, 2, 1, 0), lead.getPlaceSequenceForBell(Bell.BELL_4));

    }

    @Test
    public void getLastRowReturnsCorrectRow() {
        final List<Row> rows = new ArrayList<>();
        rows.add(buildRow(NumberOfBells.BELLS_4, 4, 3, 2, 1));
        rows.add(buildRow(NumberOfBells.BELLS_4, 1, 2, 3, 4));

        final Lead lead = new DefaultLead(NumberOfBells.BELLS_4, 0, rows, empty);

        assertEquals("1234", lead.getLastRow().getDisplayString(false));

    }

    @Test(expected = NullPointerException.class)
    public void cantConstructLeadWithNullRows() {
        new DefaultLead(NumberOfBells.BELLS_8, 0, null, empty);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cantConstructLeadWithEmptyRows() {
        final Lead lead = new DefaultLead(NumberOfBells.BELLS_8, 0, new ArrayList<>(), empty);

        assertNull(lead.getLastRow().getDisplayString(false));
    }

    @Test
    public void getHuntBellsReturnsEmptyCollectionWhenNumberOfRowsIsWrong() {

        final List<Row> rows = new ArrayList<>();
        rows.add(buildRow(NumberOfBells.BELLS_3, 1, 2, 3));
        rows.add(buildRow(NumberOfBells.BELLS_3, 2, 1, 3));
        rows.add(buildRow(NumberOfBells.BELLS_3, 2, 3, 1));

        final Lead lead = new DefaultLead(NumberOfBells.BELLS_3, 0, rows, empty);

        assertEquals(0, lead.getHuntBellStartPlace().size());
    }

    @Test
    public void getHuntBellsReturnsSinglePlaceWhenThatBellIsHuntnig() {

        final List<Row> rows = new ArrayList<>();
        rows.add(buildRow(NumberOfBells.BELLS_4, 1, 2, 3, 4));
        rows.add(buildRow(NumberOfBells.BELLS_4, 2, 1, 4, 3));
        rows.add(buildRow(NumberOfBells.BELLS_4, 2, 4, 1, 3));
        rows.add(buildRow(NumberOfBells.BELLS_4, 4, 2, 3, 1));
        rows.add(buildRow(NumberOfBells.BELLS_4, 4, 3, 2, 1));
        rows.add(buildRow(NumberOfBells.BELLS_4, 3, 4, 1, 2));
        rows.add(buildRow(NumberOfBells.BELLS_4, 3, 1, 4, 2));
        rows.add(buildRow(NumberOfBells.BELLS_4, 1, 3, 2, 4));
        rows.add(buildRow(NumberOfBells.BELLS_4, 1, 3, 4, 2));

        final Lead lead = new DefaultLead(NumberOfBells.BELLS_4, 0, rows, empty);

        assertEquals(1, lead.getHuntBellStartPlace().size());
        Assert.assertEquals(Place.PLACE_1, Iterators.getOnlyElement(lead.getHuntBellStartPlace().iterator()));
    }

    @Test
    public void getHuntBellsReturnsAllPlaceForPlainHunt() {

        final List<Row> rows = new ArrayList<>();
        rows.add(buildRow(NumberOfBells.BELLS_4, 1, 2, 3, 4));
        rows.add(buildRow(NumberOfBells.BELLS_4, 2, 1, 4, 3));
        rows.add(buildRow(NumberOfBells.BELLS_4, 2, 4, 1, 3));
        rows.add(buildRow(NumberOfBells.BELLS_4, 4, 2, 3, 1));
        rows.add(buildRow(NumberOfBells.BELLS_4, 4, 3, 2, 1));
        rows.add(buildRow(NumberOfBells.BELLS_4, 3, 4, 1, 2));
        rows.add(buildRow(NumberOfBells.BELLS_4, 3, 1, 4, 2));
        rows.add(buildRow(NumberOfBells.BELLS_4, 1, 3, 2, 4));
        rows.add(buildRow(NumberOfBells.BELLS_4, 1, 2, 3, 4));

        final Lead lead = new DefaultLead(NumberOfBells.BELLS_4, 1, rows, empty);

        assertEquals(4, lead.getHuntBellStartPlace().size());
    }

    @Test
    public void canGetPartNumber() {
        final List<Row> rows = new ArrayList<>();
        rows.add(buildRow(NumberOfBells.BELLS_4, 1, 2, 3, 4));

        final Lead lead = new DefaultLead(NumberOfBells.BELLS_4, 22, rows, empty);

        assertEquals(22, lead.getPartIndex());
    }


    @Test(expected = IllegalArgumentException.class)
    public void negativePartNumberThrows() {
        final List<Row> rows = new ArrayList<>();
        rows.add(buildRow(NumberOfBells.BELLS_4, 1, 2, 3, 4));

        new DefaultLead(NumberOfBells.BELLS_4, -1, rows, empty);
    }

    @Test
    public void zeroPartNumberOK() {
        final List<Row> rows = new ArrayList<>();
        rows.add(buildRow(NumberOfBells.BELLS_4, 1, 2, 3, 4));

        new DefaultLead(NumberOfBells.BELLS_4, 0, rows, empty);
    }

    private Row buildRow(final NumberOfBells numberOfBells, final int... oneBasedBell) {
        Bell[] bells = new Bell[oneBasedBell.length];
        for (int i = 0; i < oneBasedBell.length; i++) {
            bells[i] = Bell.valueOf(oneBasedBell[i] - 1);
        }
        return new DefaultRow(numberOfBells, bells, 0, Stroke.HANDSTROKE, RowCourseType.POSITIVE);
    }

}
