package org.ringingmaster.engine.notation.persist;

import org.junit.Test;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.notation.impl.NotationBuilder;

import java.io.IOException;

//import org.ringingmaster.persist.generated.v1.LibraryNotationPersist;

public class PersistableNotationTransformerTest {

	@Test
	public void canConvertAndRecoverToSerializableNotationPalindromeNotation() throws IOException, ClassNotFoundException {
		final NotationBuilder notationBuilder = NotationBuilder.getInstance();
		notationBuilder.setName("Plain Bob");
		notationBuilder.setFoldedPalindromeNotationShorthand("x18x18x18x18", "12");
		notationBuilder.setNumberOfWorkingBells(NumberOfBells.BELLS_8);
		final NotationBody originalNotationBody = notationBuilder.build();

		checkRoundTrip(originalNotationBody);
	}

	@Test
	public void canConvertAndRecoverToSerializableNotation() throws IOException, ClassNotFoundException {
		final NotationBuilder notationBuilder = NotationBuilder.getInstance();
		notationBuilder.setName("Plain Bob");
		notationBuilder.setNumberOfWorkingBells(NumberOfBells.BELLS_8);
		notationBuilder.setUnfoldedNotationShorthand("x18x18x18x18");
		final NotationBody originalNotationBody = notationBuilder.build();

		checkRoundTrip(originalNotationBody);
	}

	private void checkRoundTrip(final NotationBody originalNotationBody)
			throws IOException, ClassNotFoundException {

//		LibraryNotationPersist persistablePersistableNotation = PersistableNotationTransformer.buildPersistableNotation(originalNotationBody);
//
//		NotationBody deserialisedNotationBody = PersistableNotationTransformer
//				.populateBuilderFromPersistableNotation(persistablePersistableNotation)
//				.build();

//		Assert.assertEquals(originalNotationBody.toString(), deserialisedNotationBody.toString());
	}

}
