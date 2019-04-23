package org.ringingmaster.engine.notation;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PlaceTest {

	@Test
	public void valueOfWorks() {
		assertEquals(Place.ALL_CHANGE, Place.valueOf("ALL_CHANGE"));
	}
}
