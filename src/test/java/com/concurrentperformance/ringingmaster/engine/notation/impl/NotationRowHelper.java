package com.concurrentperformance.ringingmaster.engine.notation.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.concurrentperformance.ringingmaster.engine.notation.NotationPlace;
import com.concurrentperformance.ringingmaster.engine.notation.NotationRow;

/**
 * Helper class in the correct package to allow the calling of the 
 * NotationRow package private constructor
 * @author Stephen
 *
 */
public class NotationRowHelper {

	public static NotationRow buildNotationRow(NotationPlace... notationPlaces)  {
		Set<NotationPlace> elements = new HashSet<NotationPlace>(Arrays.asList(notationPlaces));
		NotationRow expected = new DefaultNotationRow(elements);
		return expected;
	}
}
