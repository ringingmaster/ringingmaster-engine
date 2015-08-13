package com.concurrentperformance.ringingmaster.engine.notation.persist;

import com.concurrentperformance.ringingmaster.engine.NumberOfBells;
import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import com.concurrentperformance.ringingmaster.engine.notation.impl.NotationBuilder;
import com.concurrentperformance.ringingmaster.persist.generated.v1.PersistableNotation;

/**
 * Manage the conversion of the persistable and engine version of notations.
 * Attempt to keep the persistable stuff isolated.
 *
 * @author Lake
 */
public class PersistableNotationTransformer {


	public static NotationBuilder populateBuilderFromPersistableNotation(PersistableNotation persistableNotation) {
		NotationBuilder notationBuilder = NotationBuilder.getInstance();

		notationBuilder.setNumberOfWorkingBells(NumberOfBells.valueOf(persistableNotation.getNumberOfBells()));
		if (!persistableNotation.isFoldedPalindrome()) {
			notationBuilder.setUnfoldedNotationShorthand(persistableNotation.getNotation());
		} else {
			notationBuilder.setFoldedPalindromeNotationShorthand(persistableNotation.getNotation(), persistableNotation.getNotation2());
		}
		notationBuilder.setName(persistableNotation.getName());

		return notationBuilder;
	}

	public static PersistableNotation buildPersistableNotation(NotationBody notationBody) {

		PersistableNotation persistableNotation = new PersistableNotation();
		persistableNotation.setName(notationBody.getName());
		persistableNotation.setNumberOfBells(notationBody.getNumberOfWorkingBells().getBellCount());
		persistableNotation.setFoldedPalindrome(notationBody.isFoldedPalindrome());
		persistableNotation.setNotation(notationBody.getRawNotationDisplayString(0, true));
		persistableNotation.setNotation2(notationBody.getRawNotationDisplayString(1, true));
		return persistableNotation;
	}
}
