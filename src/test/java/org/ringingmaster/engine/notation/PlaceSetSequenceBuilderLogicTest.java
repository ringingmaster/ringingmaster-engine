package org.ringingmaster.engine.notation;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.ringingmaster.engine.NumberOfBells;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class PlaceSetSequenceBuilderLogicTest {

	@Parameters
	public static Collection<Object[]> testValues() {
		return Arrays.asList(new Object[][] {
				{ NumberOfBells.BELLS_8, "125", "12" },
				{ NumberOfBells.BELLS_8, "234", "34" },
				{ NumberOfBells.BELLS_8, "24.x", "-" },
				{ NumberOfBells.BELLS_8, "1234", "1234" },
				{ NumberOfBells.BELLS_8, "12345", "1234" },
				{ NumberOfBells.BELLS_8, "12.8", "12" },
				{ NumberOfBells.BELLS_8, "12.87", "12.78" },
				{ NumberOfBells.BELLS_8, "687", "78" },
				{ NumberOfBells.BELLS_8, "12345678", "12345678" },

				{ NumberOfBells.BELLS_7, "x.7", "7" },
				{ NumberOfBells.BELLS_7, "17", "1" },
				{ NumberOfBells.BELLS_7, "127", "127" },
				{ NumberOfBells.BELLS_7, "67", "7" },
				{ NumberOfBells.BELLS_7, "2.7", "7" },
				{ NumberOfBells.BELLS_7, "123567", "12367" },
				{ NumberOfBells.BELLS_7, "12567", "12567" },
				{ NumberOfBells.BELLS_7, "1234567", "1234567" },

				{ NumberOfBells.BELLS_12, "234ET", "34ET" },
		});
	}

	private NotationBuilder fixture;
	private final NumberOfBells numberOFBells;
	private final String placeNotationShorthand ;

	private final String expectedResult;

	public PlaceSetSequenceBuilderLogicTest(final NumberOfBells numberOFBells, final String placeNotationShorthand, final String expectedResult) {
		super();
		this.numberOFBells = numberOFBells;
		this.placeNotationShorthand = placeNotationShorthand;
		this.expectedResult = expectedResult;
	}


	@Test
	public void impossiblePlacesAreRemoved() {
		final Notation notation = fixture
				.setNumberOfWorkingBells(numberOFBells)
				.setUnfoldedNotationShorthand(placeNotationShorthand)
				.build();

		assertEquals(expectedResult, notation.getNotationDisplayString(false));
	}

	@Before
	public void setup() {
		fixture = NotationBuilder.getInstance();
	}
}
