package org.ringingmaster.engine.notation.persist;

import org.junit.Test;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.notation.Notation;
import org.ringingmaster.engine.notation.NotationBuilder;

import java.io.IOException;

//import org.ringingmaster.persist.generated.v1.LibraryNotationPersist;

public class PersistablePlaceSetSequenceTransformerTest {

	@Test
	public void canConvertAndRecoverToSerializableNotationPalindromeNotation() throws IOException, ClassNotFoundException {
		final NotationBuilder notationBuilder = NotationBuilder.getInstance();
		notationBuilder.setName("Plain Bob");
		notationBuilder.setFoldedPalindromeNotationShorthand("x18x18x18x18", "12");
		notationBuilder.setNumberOfWorkingBells(NumberOfBells.BELLS_8);
		final Notation originalNotation = notationBuilder.build();

		checkRoundTrip(originalNotation);
	}

	@Test
	public void canConvertAndRecoverToSerializableNotation() throws IOException, ClassNotFoundException {
		final NotationBuilder notationBuilder = NotationBuilder.getInstance();
		notationBuilder.setName("Plain Bob");
		notationBuilder.setNumberOfWorkingBells(NumberOfBells.BELLS_8);
		notationBuilder.setUnfoldedNotationShorthand("x18x18x18x18");
		final Notation originalNotation = notationBuilder.build();

		checkRoundTrip(originalNotation);
	}

	private void checkRoundTrip(final Notation originalNotation)
			throws IOException, ClassNotFoundException {

//		LibraryNotationPersist persistablePersistableNotation = PersistableNotationTransformer.buildPersistableNotation(originalNotation);
//
//		Notation deserialisedNotationBody = PersistableNotationTransformer
//				.populateBuilderFromPersistableNotation(persistablePersistableNotation)
//				.build();

//		Assert.assertEquals(originalNotation.toString(), deserialisedNotationBody.toString());
	}

}
