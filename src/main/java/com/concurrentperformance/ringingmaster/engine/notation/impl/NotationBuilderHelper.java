package com.concurrentperformance.ringingmaster.engine.notation.impl;

import com.concurrentperformance.ringingmaster.engine.NumberOfBells;
import com.concurrentperformance.ringingmaster.engine.notation.Notation;
import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import com.concurrentperformance.ringingmaster.engine.notation.NotationPlace;
import com.concurrentperformance.ringingmaster.engine.notation.NotationRow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * TODO
 * User: Stephen
 */
public class NotationBuilderHelper {

	public static List<NotationRow> getValidatedRowsFromShorthand(final String notationShorthand, final NumberOfBells numberOfBells) {
		final List<String> shorthandRows = splitShorthandIntoRows(notationShorthand);
		final List<NotationRow> validatedRows = getValidatedNotationRows(shorthandRows, numberOfBells);
		return validatedRows;
	}

	/**
	 * Build the normalised notation.
	 *
	 * @return
	 */
	public static List<NotationRow> buildNormalisedFullNotation(List<NotationRow> notationRows) {
		// 1) Add the normal notation elements
		final List<NotationRow> normalisedNotationRows = new ArrayList(notationRows);

		return Collections.unmodifiableList(normalisedNotationRows);
	}

	/**
	 * Build the normalised notation. This will have all the folded palindrome symmetry applied
	 *
	 * @return
	 */
	public static List<NotationRow> buildNormalisedFoldedPalindrome(List<List<NotationRow>> notationRowsSets) {
		final List<NotationRow> normalisedNotationRows = new ArrayList();

		for (List<NotationRow> notationRows : notationRowsSets) {

			// 1) Add the normal notation elements
			normalisedNotationRows.addAll(notationRows);

			// 2) Then add the elements in reverse, starting with the penultimate notation
			if (notationRows.size() > 1) {
				final List<NotationRow> notationWithoutPenultimate = notationRows.subList(0, notationRows.size() - 1);
				final List<NotationRow> notationElementsCopy = new ArrayList<>(notationWithoutPenultimate);
				Collections.reverse(notationElementsCopy);
				normalisedNotationRows.addAll(notationElementsCopy);
			}
		}

		return Collections.unmodifiableList(normalisedNotationRows);
	}

	private static List<String> splitShorthandIntoRows(final String notationShorthand) {
		final NotationSplitter notationSplitter = new NotationSplitter();
		final List<String> splitShorthands = notationSplitter.split(notationShorthand);
		return splitShorthands;
	}


	private static List<NotationRow> getValidatedNotationRows(final List<String> shorthandRows, final NumberOfBells numberOfBells) {

		final List<NotationRow> validatedNotationRows = new ArrayList<NotationRow>();

		for (final String shorthandRow : shorthandRows) {
			final Set<NotationPlace> unValidatedElementsForRow = NotationPlace.getNotationElements(shorthandRow);
			final Set<NotationPlace> validatedElementsForRow = validateElements(unValidatedElementsForRow, numberOfBells);
			if (!validatedElementsForRow.isEmpty()) {
				final NotationRow notationRow = new DefaultNotationRow(validatedElementsForRow);
				validatedNotationRows.add(notationRow);
			}
		}

		return validatedNotationRows;
	}

	private static Set<NotationPlace> validateElements(final Set<NotationPlace> unValidatedElementsForRow, final NumberOfBells numberOfBells) {
		final List<NotationPlace> validatedElementsForRow = new ArrayList<NotationPlace>();

		// ALL_CHANGE rows are only valid on even bell methods, and needs to be the only element
		// in the row. ALL_CHANGE has a higher precedence than places.
		if (unValidatedElementsForRow.contains(NotationPlace.ALL_CHANGE)) {
			if (numberOfBells.isEven()) {
				validatedElementsForRow.add(NotationPlace.ALL_CHANGE);
			}
			else {
				// Do nothing - we want an empty set
			}
		}
		else {
			// Places must have an even number of non places between them.
			for (int i=0;i<numberOfBells.getBellCount();i++) {
				final NotationPlace placeElement = NotationPlace.valueOf(i);
				if (unValidatedElementsForRow.contains(placeElement)) {
					validatedElementsForRow.add(placeElement);
				}
				else {
					i++;
				}
			}

			if ((isEven(validatedElementsForRow.size()) != numberOfBells.isEven()) &&
					(validatedElementsForRow.size() > 0)) {

				validatedElementsForRow.remove(validatedElementsForRow.size()-1);
			}
		}

		return new HashSet<>(validatedElementsForRow);
	}

	private static boolean isEven(final int i) {
		return (i % 2) == 0;
	}

	public static List<NotationBody> filterNotations(List<NotationBody> notations, NumberOfBells numberOfBells) {
		List<NotationBody> filteredNotations = new ArrayList<>();
		for (NotationBody notation : notations) {
			if (notation.getNumberOfWorkingBells().getBellCount() <= numberOfBells.getBellCount()) {
				filteredNotations.add(notation);
			}
		}
		return filteredNotations;
	}

	public static String getAsDisplayString(final List<NotationRow> rows, final boolean concise) {
		final StringBuilder buff = new StringBuilder();
		NotationRow lastRow = null;
		for (final NotationRow row : rows) {
			// Add a separator if required
			if ((!concise && (lastRow != null)) || //The lastRow being null is used as a flag for first row.
					((lastRow != null) && !lastRow.isAllChange() && !row.isAllChange())) {
				buff.append(Notation.ROW_SEPARATOR);
			}

			buff.append(row.toDisplayString());

			lastRow = row;
		}
		return buff.toString();
	}


	public static String validateAsDisplayString(String notationShorthand, NumberOfBells numberOfBells, boolean concise) {
		List<NotationRow> validatedRowsFromShorthand = getValidatedRowsFromShorthand(notationShorthand, numberOfBells);
		return getAsDisplayString(validatedRowsFromShorthand, concise);
	}
}
