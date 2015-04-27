package com.concurrentperformance.ringingmaster.engine.method.impl;

import com.concurrentperformance.ringingmaster.engine.NumberOfBells;
import com.concurrentperformance.ringingmaster.engine.method.Bell;
import com.concurrentperformance.ringingmaster.engine.method.MethodLead;
import com.concurrentperformance.ringingmaster.engine.method.MethodRow;
import com.concurrentperformance.ringingmaster.engine.method.RowCourseType;
import com.concurrentperformance.ringingmaster.engine.method.Stroke;
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
		final List<MethodRow> rows = new ArrayList<MethodRow>();
		rows.add(buildRow(NumberOfBells.BELLS_4, Bell.BELL_1, Bell.BELL_2, Bell.BELL_3, Bell.BELL_4));
		rows.add(buildRow(NumberOfBells.BELLS_4, Bell.BELL_2, Bell.BELL_1, Bell.BELL_4, Bell.BELL_3));
		rows.add(buildRow(NumberOfBells.BELLS_4, Bell.BELL_2, Bell.BELL_4, Bell.BELL_1, Bell.BELL_3));
		rows.add(buildRow(NumberOfBells.BELLS_4, Bell.BELL_4, Bell.BELL_2, Bell.BELL_3, Bell.BELL_1));

		final MethodLead lead = new DefaultMethodLead(NumberOfBells.BELLS_4, rows, empty);

		assertEquals(Arrays.asList(0, 1, 2, 3), lead.getPlaceSequenceForBell(Bell.BELL_1));
		assertEquals(Arrays.asList(1, 0, 0, 1), lead.getPlaceSequenceForBell(Bell.BELL_2));
		assertEquals(Arrays.asList(2, 3, 3, 2), lead.getPlaceSequenceForBell(Bell.BELL_3));
		assertEquals(Arrays.asList(3, 2, 1, 0), lead.getPlaceSequenceForBell(Bell.BELL_4));

	}

	@Test
	public void getLastRowReturnsCorrectRow() {
		final List<MethodRow> rows = new ArrayList<MethodRow>();
		rows.add(buildRow(NumberOfBells.BELLS_4, Bell.BELL_4, Bell.BELL_3, Bell.BELL_2, Bell.BELL_1));
		rows.add(buildRow(NumberOfBells.BELLS_4, Bell.BELL_1, Bell.BELL_2, Bell.BELL_3, Bell.BELL_4));

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

	private MethodRow buildRow(final NumberOfBells numberOfBells, final Bell... bells) {
		return new DefaultMethodRow(numberOfBells, bells, 0, Stroke.HANDSTROKE, RowCourseType.POSITIVE);
	}

}
