package org.ringingmaster.engine.notation;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Helper class in the correct package to allow the calling of the 
 * PlaceSet package private constructor
 * @author Steve Lake
 *
 */
public class NotationRowHelper {

	public static PlaceSet buildNotationRow(Place... places)  {
		Set<Place> elements = new HashSet<Place>(Arrays.asList(places));
		PlaceSet expected = new DefaultPlaceSet(elements);
		return expected;
	}
}
