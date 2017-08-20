package org.ringingmaster.engine.method.impl;

import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.method.Bell;
import org.ringingmaster.engine.method.MethodLead;
import org.ringingmaster.engine.method.MethodRow;
import org.ringingmaster.engine.method.RowCourseType;
import org.ringingmaster.engine.method.Stroke;
import org.ringingmaster.engine.notation.NotationPlace;
import com.google.common.collect.Iterators;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DefaultMethodLeadTest {

	private final List<Integer> empty = Collections.emptyList();

	@Test
	public void sequencesMatchPassedInRows() {
		final List<MethodRow> rows = new ArrayList<>();
		rows.add(buildRow(NumberOfBells.BELLS_4, 1,2,3,4));
		rows.add(buildRow(NumberOfBells.BELLS_4, 2,1,4,3));
		rows.add(buildRow(NumberOfBells.BELLS_4, 2,4,1,3));
		rows.add(buildRow(NumberOfBells.BELLS_4, 4,2,3,1));

		final MethodLead lead = new DefaultMethodLead(NumberOfBells.BELLS_4, rows, empty);

		assertEquals(Arrays.asList(0, 1, 2, 3), lead.getPlaceSequenceForBell(Bell.BELL_1));
		assertEquals(Arrays.asList(1, 0, 0, 1), lead.getPlaceSequenceForBell(Bell.BELL_2));
		assertEquals(Arrays.asList(2, 3, 3, 2), lead.getPlaceSequenceForBell(Bell.BELL_3));
		assertEquals(Arrays.asList(3, 2, 1, 0), lead.getPlaceSequenceForBell(Bell.BELL_4));

	}

	@Test
	public void getLastRowReturnsCorrectRow() {
		final List<MethodRow> rows = new ArrayList<>();
		rows.add(buildRow(NumberOfBells.BELLS_4, 4, 3, 2, 1));
		rows.add(buildRow(NumberOfBells.BELLS_4, 1, 2, 3, 4));

		final MethodLead lead = new DefaultMethodLead(NumberOfBells.BELLS_4, rows, empty);

		assertEquals("1234", lead.getLastRow().getDisplayString(false));

	}

	@Test(expected=NullPointerException.class)
	public void cantConstructLeadWithNullRows() {
		new DefaultMethodLead(NumberOfBells.BELLS_8, null, empty);
	}

	@Test(expected=IllegalArgumentException.class)
	public void cantConstructLeadWithEmptyRows() {
		final MethodLead lead = new DefaultMethodLead(NumberOfBells.BELLS_8, new ArrayList<>(), empty);

		assertNull(lead.getLastRow().getDisplayString(false));
	}

	@Test
	public void getHuntBellsReturnsEmptyCollectionWhenNumberOfRowsIsWrong() {

		final List<MethodRow> rows = new ArrayList<>();
		rows.add(buildRow(NumberOfBells.BELLS_3, 1,2,3));
		rows.add(buildRow(NumberOfBells.BELLS_3, 2,1,3));
		rows.add(buildRow(NumberOfBells.BELLS_3, 2, 3, 1));

		final MethodLead lead = new DefaultMethodLead(NumberOfBells.BELLS_3, rows, empty);

		assertEquals(0, lead.getHuntBellStartPlace().size());
	}

	@Test
	public void getHuntBellsReturnsSinglePlaceWhenThatBellIsHuntnig() {

		final List<MethodRow> rows = new ArrayList<>();
		rows.add(buildRow(NumberOfBells.BELLS_4, 1,2,3,4));
		rows.add(buildRow(NumberOfBells.BELLS_4, 2,1,4,3));
		rows.add(buildRow(NumberOfBells.BELLS_4, 2,4,1,3));
		rows.add(buildRow(NumberOfBells.BELLS_4, 4,2,3,1));
		rows.add(buildRow(NumberOfBells.BELLS_4, 4,3,2,1));
		rows.add(buildRow(NumberOfBells.BELLS_4, 3,4,1,2));
		rows.add(buildRow(NumberOfBells.BELLS_4, 3,1,4,2));
		rows.add(buildRow(NumberOfBells.BELLS_4, 1,3,2,4));
		rows.add(buildRow(NumberOfBells.BELLS_4, 1,3,4,2));

		final MethodLead lead = new DefaultMethodLead(NumberOfBells.BELLS_4, rows, empty);

		assertEquals(1, lead.getHuntBellStartPlace().size());
		Assert.assertEquals(NotationPlace.PLACE_1, Iterators.getOnlyElement(lead.getHuntBellStartPlace().iterator()));
	}

	@Test
	public void getHuntBellsReturnsAllPlaceForPlainHunt() {

		final List<MethodRow> rows = new ArrayList<>();
		rows.add(buildRow(NumberOfBells.BELLS_4, 1,2,3,4));
		rows.add(buildRow(NumberOfBells.BELLS_4, 2,1,4,3));
		rows.add(buildRow(NumberOfBells.BELLS_4, 2,4,1,3));
		rows.add(buildRow(NumberOfBells.BELLS_4, 4,2,3,1));
		rows.add(buildRow(NumberOfBells.BELLS_4, 4,3,2,1));
		rows.add(buildRow(NumberOfBells.BELLS_4, 3,4,1,2));
		rows.add(buildRow(NumberOfBells.BELLS_4, 3,1,4,2));
		rows.add(buildRow(NumberOfBells.BELLS_4, 1,3,2,4));
		rows.add(buildRow(NumberOfBells.BELLS_4, 1,2,3,4));

		final MethodLead lead = new DefaultMethodLead(NumberOfBells.BELLS_4, rows, empty);

		assertEquals(4, lead.getHuntBellStartPlace().size());
	}

	private MethodRow buildRow(final NumberOfBells numberOfBells, final int... oneBasedBell) {
		Bell[] bells = new Bell[oneBasedBell.length];
		for (int i=0;i<oneBasedBell.length;i++) {
			bells[i] = Bell.valueOf(oneBasedBell[i] - 1);
		}
		return new DefaultMethodRow(numberOfBells, bells, 0, Stroke.HANDSTROKE, RowCourseType.POSITIVE);
	}

}
