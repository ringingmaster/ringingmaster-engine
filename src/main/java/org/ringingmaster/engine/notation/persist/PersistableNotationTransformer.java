package org.ringingmaster.engine.notation.persist;

import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.notation.impl.NotationBuilder;
import org.ringingmaster.persist.generated.v1.LibraryNotationPersist;

/**
 * Manage the conversion of the persistable and engine version of notations.
 * Attempt to keep the persistable stuff isolated.
 *
 * @author Lake
 */
public class PersistableNotationTransformer {


	public static NotationBuilder populateBuilderFromPersistableNotation(LibraryNotationPersist persistableNotation) {
		NotationBuilder notationBuilder = NotationBuilder.getInstance();

		notationBuilder.setNumberOfWorkingBells(NumberOfBells.valueOf(persistableNotation.getNumberOfWorkingBells()));
		if (!persistableNotation.isFoldedPalindrome()) {
			notationBuilder.setUnfoldedNotationShorthand(persistableNotation.getNotation());
		} else {
			notationBuilder.setFoldedPalindromeNotationShorthand(persistableNotation.getNotation(), persistableNotation.getNotation2());
		}
		notationBuilder.setName(persistableNotation.getName());

		return notationBuilder;
	}

	public static LibraryNotationPersist buildPersistableNotation(NotationBody notationBody) {

		LibraryNotationPersist persistableNotation = new LibraryNotationPersist();
		persistableNotation.setName(notationBody.getName());
		persistableNotation.setNumberOfWorkingBells(notationBody.getNumberOfWorkingBells().getBellCount());
		persistableNotation.setFoldedPalindrome(notationBody.isFoldedPalindrome());
		persistableNotation.setNotation(notationBody.getRawNotationDisplayString(0, true));
		persistableNotation.setNotation2(notationBody.getRawNotationDisplayString(1, true));
		return persistableNotation;
	}
}