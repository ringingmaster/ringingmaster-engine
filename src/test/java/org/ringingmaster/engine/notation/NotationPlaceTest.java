package org.ringingmaster.engine.notation;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NotationPlaceTest {

	@Test
	public void valueOfWorks() {
		assertEquals(NotationPlace.ALL_CHANGE, NotationPlace.valueOf("ALL_CHANGE"));
	}
}
