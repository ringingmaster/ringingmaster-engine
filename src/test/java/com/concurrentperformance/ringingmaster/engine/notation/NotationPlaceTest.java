package com.concurrentperformance.ringingmaster.engine.notation;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class NotationPlaceTest {

	@Test
	public void valueOfWorks() {
		assertEquals(NotationPlace.ALL_CHANGE, NotationPlace.valueOf("ALL_CHANGE"));
	}
}
