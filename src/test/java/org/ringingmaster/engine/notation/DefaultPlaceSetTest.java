package org.ringingmaster.engine.notation;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DefaultPlaceSetTest {


	@Test
	public void makesPlace() {

		final Set<Place> elements = new HashSet<Place>();
		elements.add(Place.PLACE_1);
		elements.add(Place.PLACE_8);
		final PlaceSet placeSet = new DefaultPlaceSet(elements);

		assertTrue("Should make place 1", placeSet.makesPlace(Place.PLACE_1));
		assertFalse("Should not make place 2", placeSet.makesPlace(Place.PLACE_2));
		assertFalse("Should not make place 3", placeSet.makesPlace(Place.PLACE_3));
		assertFalse("Should not make place 4", placeSet.makesPlace(Place.PLACE_4));
		assertFalse("Should not make place 5", placeSet.makesPlace(Place.PLACE_5));
		assertFalse("Should not make place 6", placeSet.makesPlace(Place.PLACE_6));
		assertFalse("Should not make place 7", placeSet.makesPlace(Place.PLACE_7));
		assertTrue("Should make place 8", placeSet.makesPlace(Place.PLACE_8));

	}
}
