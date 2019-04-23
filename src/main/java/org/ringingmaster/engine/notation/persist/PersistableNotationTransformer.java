package org.ringingmaster.engine.notation.persist;

import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.notation.Notation;
import org.ringingmaster.engine.notation.NotationBuilder;
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

    public static LibraryNotationPersist buildPersistableNotation(Notation notation) {

        LibraryNotationPersist persistableNotation = new LibraryNotationPersist();
        persistableNotation.setName(notation.getName());
        persistableNotation.setNumberOfWorkingBells(notation.getNumberOfWorkingBells().toInt());
        persistableNotation.setFoldedPalindrome(notation.isFoldedPalindrome());
        persistableNotation.setNotation(notation.getRawNotationDisplayString(0, true));
        persistableNotation.setNotation2(notation.getRawNotationDisplayString(1, true));
        return persistableNotation;
    }
}
