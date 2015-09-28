package com.concurrentperformance.ringingmaster.engine.notation.persist;

import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import com.concurrentperformance.ringingmaster.engine.notation.impl.NotationBuilder;
import com.concurrentperformance.ringingmaster.persist.generated.v1.LibraryNotationPersist;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class PersistableNotationTransformerTest {

	@Test
	public void canConvertAndRecoverToSerializableNotationPalindromeNotation() throws IOException, ClassNotFoundException {
		final NotationBuilder notationBuilder = NotationBuilder.getInstance();
		notationBuilder.setName("Plain Bob");
		notationBuilder.setFoldedPalindromeNotationShorthand("x18x18x18x18", "12");
		final NotationBody originalNotationBody = notationBuilder.build();

		checkRoundTrip(originalNotationBody);
	}

	@Test
	public void canConvertAndRecoverToSerializableNotation() throws IOException, ClassNotFoundException {
		final NotationBuilder notationBuilder = NotationBuilder.getInstance();
		notationBuilder.setName("Plain Bob");
		notationBuilder.setUnfoldedNotationShorthand("x18x18x18x18");
		final NotationBody originalNotationBody = notationBuilder.build();

		checkRoundTrip(originalNotationBody);
	}

	private void checkRoundTrip(final NotationBody originalNotationBody)
			throws IOException, ClassNotFoundException {

		LibraryNotationPersist persistablePersistableNotation = PersistableNotationTransformer.buildPersistableNotation(originalNotationBody);

		NotationBody deserialisedNotationBody = PersistableNotationTransformer
				.populateBuilderFromPersistableNotation(persistablePersistableNotation)
				.build();

		Assert.assertEquals(originalNotationBody.toString(), deserialisedNotationBody.toString());
	}

}
