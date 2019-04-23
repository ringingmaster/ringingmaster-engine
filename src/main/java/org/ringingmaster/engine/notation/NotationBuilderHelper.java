package org.ringingmaster.engine.notation;

import org.pcollections.PSet;
import org.ringingmaster.engine.NumberOfBells;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO
 * User: Stephen
 */
public class NotationBuilderHelper {

    public static List<PlaceSet> getValidatedRowsFromShorthand(final String notationShorthand, final NumberOfBells numberOfBells) {
        checkNotNull(notationShorthand);
        checkNotNull(numberOfBells);

        final List<String> shorthandRows = splitShorthandIntoRows(notationShorthand);
        final List<PlaceSet> validatedRows = getValidatedNotationRows(shorthandRows, numberOfBells);
        return validatedRows;
    }

    /**
     * Build the normalised notation.
     *
     * @return
     */
    public static List<PlaceSet> buildNormalisedFullNotation(List<PlaceSet> placeSets) {
        // 1) Add the normal notation elements
        final List<PlaceSet> normalisedPlaceSets = new ArrayList(placeSets);

        return Collections.unmodifiableList(normalisedPlaceSets);
    }

    /**
     * Build the normalised notation. This will have all the folded palindrome symmetry applied
     *
     * @return
     */
    public static List<PlaceSet> buildNormalisedFoldedPalindrome(List<List<PlaceSet>> notationRowsSets) {
        final List<PlaceSet> normalisedPlaceSets = new ArrayList();

        for (List<PlaceSet> placeSets : notationRowsSets) {

            // 1) Add the normal notation elements
            normalisedPlaceSets.addAll(placeSets);

            // 2) Then add the elements in reverse, starting with the penultimate notation
            if (placeSets.size() > 1) {
                final List<PlaceSet> notationWithoutPenultimate = placeSets.subList(0, placeSets.size() - 1);
                final List<PlaceSet> notationElementsCopy = new ArrayList<>(notationWithoutPenultimate);
                Collections.reverse(notationElementsCopy);
                normalisedPlaceSets.addAll(notationElementsCopy);
            }
        }

        return Collections.unmodifiableList(normalisedPlaceSets);
    }

    private static List<String> splitShorthandIntoRows(final String notationShorthand) {
        final NotationSplitter notationSplitter = new NotationSplitter();
        final List<String> splitShorthands = notationSplitter.split(notationShorthand);
        return splitShorthands;
    }


    private static List<PlaceSet> getValidatedNotationRows(final List<String> shorthandRows, final NumberOfBells numberOfBells) {

        final List<PlaceSet> validatedPlaceSets = new ArrayList<PlaceSet>();

        for (final String shorthandRow : shorthandRows) {
            final Set<Place> unValidatedElementsForRow = Place.parsePlaces(shorthandRow);
            final Set<Place> validatedElementsForRow = validateElements(unValidatedElementsForRow, numberOfBells);
            if (!validatedElementsForRow.isEmpty()) {
                final PlaceSet placeSet = new DefaultPlaceSet(validatedElementsForRow);
                validatedPlaceSets.add(placeSet);
            }
        }

        return validatedPlaceSets;
    }

    private static Set<Place> validateElements(final Set<Place> unValidatedElementsForRow, final NumberOfBells numberOfBells) {
        final List<Place> validatedElementsForRow = new ArrayList<Place>();

        // ALL_CHANGE rows are only valid on even bell methods, and needs to be the only element
        // in the row. ALL_CHANGE has a higher precedence than places.
        if (unValidatedElementsForRow.contains(Place.ALL_CHANGE)) {
            if (numberOfBells.isEven()) {
                validatedElementsForRow.add(Place.ALL_CHANGE);
            } else {
                // Do nothing - we want an empty set
            }
        } else {
            // Places must have an even number of non places between them.
            for (int i = 0; i < numberOfBells.toInt(); i++) {
                final Place placeElement = Place.valueOf(i);
                if (unValidatedElementsForRow.contains(placeElement)) {
                    validatedElementsForRow.add(placeElement);
                } else {
                    i++;
                }
            }

            if ((isEven(validatedElementsForRow.size()) != numberOfBells.isEven()) &&
                    (validatedElementsForRow.size() > 0)) {

                validatedElementsForRow.remove(validatedElementsForRow.size() - 1);
            }
        }

        return new HashSet<>(validatedElementsForRow);
    }

    private static boolean isEven(final int i) {
        return (i % 2) == 0;
    }

    public static PSet<Notation> filterNotationsUptoNumberOfBells(PSet<Notation> notations, NumberOfBells numberOfBells) {

        Set<Notation> notationsToBeRemoved = new HashSet<>();
        for (Notation notation : notations) {
            if (notation.getNumberOfWorkingBells().toInt() > numberOfBells.toInt()) {
                notationsToBeRemoved.add(notation);
            }
        }
        return notations.minusAll(notationsToBeRemoved);
    }

    public static String getAsDisplayString(final List<PlaceSet> rows, final boolean concise) {
        final StringBuilder buff = new StringBuilder();
        PlaceSet lastRow = null;
        for (final PlaceSet row : rows) {
            // Add a separator if required
            if ((!concise && (lastRow != null)) || //The lastRow being null is used as a flag for first row.
                    ((lastRow != null) && !lastRow.isAllChange() && !row.isAllChange())) {
                buff.append(PlaceSetSequence.ROW_SEPARATOR);
            }

            buff.append(row.toDisplayString());

            lastRow = row;
        }
        return buff.toString();
    }


    public static String validateAsDisplayString(String notationShorthand, NumberOfBells numberOfBells, boolean concise) {
        List<PlaceSet> validatedRowsFromShorthand = getValidatedRowsFromShorthand(notationShorthand, numberOfBells);
        return getAsDisplayString(validatedRowsFromShorthand, concise);
    }
}
