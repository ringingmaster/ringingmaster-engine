package org.ringingmaster.engine.compiler.variance;

import com.google.common.collect.Sets;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * TODO comments???
 * User: Stephen
 */
public class SpecifiedPartsVarianceTest {

	@Test
	public void includeSpecifiedParts() {
		Variance variance = new SpecifiedPartsVariance(VarianceLogicType.INCLUDE, Sets.newHashSet(0, 2));
		assertTrue(variance.includePart(0));
		assertFalse(variance.includePart(1));
		assertTrue(variance.includePart(2));
		assertFalse(variance.includePart(3));
	}

	@Test
	public void excludeSpecifiedParts() {
		Variance variance = new SpecifiedPartsVariance( VarianceLogicType.OMIT, Sets.newHashSet(0, 2));
		assertFalse(variance.includePart(0));
		assertTrue(variance.includePart(1));
		assertFalse(variance.includePart(2));
		assertTrue(variance.includePart(3));
	}

	@Test(expected = IllegalArgumentException.class)
	public void belowZeroPartThrows() {
		new SpecifiedPartsVariance(VarianceLogicType.INCLUDE, Sets.newHashSet(-1));
	}

	@Test
	public void includeEvenParts() {
		Variance variance = new OddEvenVariance(VarianceLogicType.INCLUDE, OddEvenVarianceType.EVEN);
		assertTrue(variance.includePart(0));
		assertFalse(variance.includePart(1));
		assertTrue(variance.includePart(2));
		assertFalse(variance.includePart(3));
	}

	@Test
	public void excludEvenParts() {
		Variance variance = new OddEvenVariance(VarianceLogicType.OMIT, OddEvenVarianceType.EVEN);
		assertFalse(variance.includePart(0));
		assertTrue(variance.includePart(1));
		assertFalse(variance.includePart(2));
		assertTrue(variance.includePart(3));
	}

	@Test
	public void includeOddParts() {
		Variance variance = new OddEvenVariance(VarianceLogicType.INCLUDE, OddEvenVarianceType.ODD);
		assertFalse(variance.includePart(0));
		assertTrue(variance.includePart(1));
		assertFalse(variance.includePart(2));
		assertTrue(variance.includePart(3));
	}

	@Test
	public void excludeOddParts() {
		Variance variance = new OddEvenVariance(VarianceLogicType.OMIT, OddEvenVarianceType.ODD);
		assertTrue(variance.includePart(0));
		assertFalse(variance.includePart(1));
		assertTrue(variance.includePart(2));
		assertFalse(variance.includePart(3));
	}
}
