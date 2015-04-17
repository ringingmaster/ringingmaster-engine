package com.concurrentperformance.ringingmaster.engine.notation.impl;

import com.concurrentperformance.ringingmaster.engine.NumberOfBells;
import com.concurrentperformance.ringingmaster.engine.helper.PlainCourseHelper;
import com.concurrentperformance.ringingmaster.engine.method.Method;
import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import com.concurrentperformance.ringingmaster.engine.notation.NotationCall;
import com.concurrentperformance.ringingmaster.engine.notation.NotationMethodCallingPosition;
import com.concurrentperformance.ringingmaster.engine.notation.NotationRow;
import com.concurrentperformance.ringingmaster.generated.notation.persist.SerializableNotation;
import com.google.common.base.Strings;
import net.jcip.annotations.NotThreadSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkPositionIndex;
import static com.google.common.base.Preconditions.checkState;


/**
 * Take user or library input for a notation and verify and build a NotationBody Object that
 * the engine can use to create a method. Set the state of the notation using
 * the various setter methods, and then call build() to get the notation
 * Each instance should only be used once and discarded as it contains state.
 *
 * This forces that the construction of the NotationBody is verified.
 *
 * @author Stephen Lake
 */
@NotThreadSafe
public class NotationBuilder {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private String name = "Unknown";
	private NumberOfBells numberOfWorkingBells = NumberOfBells.BELLS_8;
	private final List<String> notationShorthands = new ArrayList<>();
	private boolean foldedPalindrome = false;
	private List<NotationCallBuilder> notationCallBuilders = new ArrayList<>();
	private String defaultCallName = "";
	private String spliceIdentifier;
	private Set<Integer> callInitiationRows = new HashSet<>();
	private Set<NotationMethodCallingPosition> methodCallingPositions = new HashSet<>();

	private NotationBuilder() {
	}

	public static NotationBuilder getInstance() {
		return new NotationBuilder();
	}

	/**
	 * Builds the notation based on the set values.
	 *
	 * @return NotationBody, built to the set values.
	 */
	public NotationBody build() {
		checkState(!Strings.isNullOrEmpty(name), "Please enter a name");
		checkState(notationShorthands.size() > 0, "Please enter a notation");

		final List<List<NotationRow>> notationElementsSets = new ArrayList<>();

		for (String notationShorthand : notationShorthands) {
			final List<NotationRow> notationElements = NotationBuilderHelper.getValidatedRowsFromShorthand(notationShorthand, numberOfWorkingBells);
			notationElementsSets.add(notationElements);
		}

		List<NotationRow> normalisedNotationElements;
		if (foldedPalindrome) {
			normalisedNotationElements = NotationBuilderHelper.buildNormalisedFoldedPalindrome(notationElementsSets);
		}
		else {
			normalisedNotationElements = NotationBuilderHelper.buildNormalisedFullNotation(notationElementsSets.get(0));
		}
		if (normalisedNotationElements.size() == 0) {
			log.info("After validation, all [{}] notation elements were removed as invalid. Returning empty NotationBody. [{}],[{}], [{}]", name, notationShorthands, numberOfWorkingBells);
			return new DefaultNotationBody(name,
					numberOfWorkingBells,
					normalisedNotationElements,
					notationElementsSets,
					foldedPalindrome,
					"",
					Collections.emptySet(),
					null,
					Collections.emptySet(),
					Collections.emptySet(),
					spliceIdentifier);
		}
		final Method plainCourse = buildPlainCourse(normalisedNotationElements);
		final int changesCountInPlainLead = plainCourse.getLead(0).getRowCount() - 1; // minus 1 as first and last change are shared between leads.
		final SortedSet<NotationCall> notationCalls = buildCalls();
		final NotationCall defaultNotationCall = getDefaultNotationCall(notationCalls);
		final Set<Integer> validatedCallInitiationRows = getValidatedCallInitiationRows(callInitiationRows, changesCountInPlainLead);
		final Set<NotationMethodCallingPosition> validatedNotationMethodCallingPositions =
				getValidatedMethodCallingPositions(validatedCallInitiationRows, methodCallingPositions, plainCourse.getLeadCount());
		String leadHeadCode = LeadHeadCalculator.calculateLeadHeadCode(plainCourse.getLead(0), normalisedNotationElements);

		return new DefaultNotationBody(name,
				numberOfWorkingBells,
				normalisedNotationElements,
				notationElementsSets,
				foldedPalindrome,
				leadHeadCode,
				notationCalls,
				defaultNotationCall,
				validatedCallInitiationRows,
				validatedNotationMethodCallingPositions,
				spliceIdentifier);
	}

	public NotationBuilder setFromSerializableNotation(SerializableNotation serializableNotation) {

		setNumberOfWorkingBells(NumberOfBells.valueOf(serializableNotation.getNumberOfBells()));
		if (!serializableNotation.isFoldedPalindrome()) {
			setUnfoldedNotationShorthand(serializableNotation.getNotation());
		} else {
			setFoldedPalindromeNotationShorthand(serializableNotation.getNotation(), serializableNotation.getNotation2());
		}
		setName(serializableNotation.getName());

		return this;
	}


	/**
	 * Set the name of the notation, NOT including the number of bells extension.
	 *
	 * @param name
	 */
	public NotationBuilder setName(final String name) {
		checkState(!Strings.isNullOrEmpty(name), "Please supply a valid name");
		this.name = name;
		return this;
	}

	/**
	 * Set the number of bells that the notation is defined for.
	 * This value excludes all covers.
	 * Defaults to NumberOfBells.BELL_8
	 *
	 * @param numberOfWorkingBells
	 */
	public NotationBuilder setNumberOfWorkingBells(final NumberOfBells numberOfWorkingBells) {
		this.numberOfWorkingBells = checkNotNull(numberOfWorkingBells, "numberOfWorkingBells must not be null");
		return this;
	}

	/**
	 * Set a non folded palindrome notation. When using this, the place notation shorthand
	 * will be used once per lead as supplied.
	 * i.e.
	 * non folded palindrome notation 'x.14' stays as 'x.14'
	 * @param notationShorthand
	 */
	public NotationBuilder setUnfoldedNotationShorthand(final String notationShorthand) {
		checkState(this.notationShorthands.size() == 0, "Only set notation once");

		this.notationShorthands.add(checkNotNull(notationShorthand, "unfoldedNotationShorthand must not be null"));
		foldedPalindrome = false;
		return this;
	}

	/**
	 * Set a folded palindrome symmetry notation. When using this,each place notation shorthand
	 * will be used in reverse after being used forward.
	 * i.e.
	 *  folded palindrome symmetry notation 'x.14.12', '16' becomes 'x.14.12.14.x16'
	 *  folded palindrome symmetry notation 'x.14', '18.16' becomes 'x.14.x.18.16.18'
	 */
 	public NotationBuilder setFoldedPalindromeNotationShorthand(final String... foldedPalindromeNotationShorthands) {
		checkNotNull(foldedPalindromeNotationShorthands, "foldedPalindromeNotationShorthands must not be null");
	    checkArgument(foldedPalindromeNotationShorthands.length > 0, "foldedPalindromeNotationShorthands must supply at least one notation");
	    checkState(this.notationShorthands.size() == 0, "Only set notation once");

	    for (String foldedPalindromeNotationShorthand : foldedPalindromeNotationShorthands) {
		    if (foldedPalindromeNotationShorthand != null) {
			    this.notationShorthands.add(foldedPalindromeNotationShorthand);
		    }
	    }

	    checkState(this.notationShorthands.size() > 0);
	    foldedPalindrome = true;
		return this;
	}

	/**
	 * Add a call.
	 */
	public NotationBuilder addCall(String name, String nameShorthand, String callNotation, boolean defaultCall) {
		NotationCallBuilder newCallBuilder =  new NotationCallBuilder()
				.setName(name)
				.setNameShorthand(nameShorthand)
				.setUnfoldedNotationShorthand(callNotation);

		for (NotationCallBuilder callBuilder : notationCallBuilders) {
			newCallBuilder.checkClashWith(callBuilder) ;
		}
		notationCallBuilders.add(newCallBuilder);
		if (defaultCall) {
			this.defaultCallName =  name;
		}
		return this;
	}

	/**
	 * Add a lead based calling position.
	 */
	public NotationBuilder addCallInitiationRow(int callInitiationRow) {
		checkArgument(callInitiationRow >= 0, "call initiation row [" + callInitiationRow + "] must be positive");
		callInitiationRows.add(callInitiationRow);
		return this;
	}

	/**
	 * Add a lead based calling position.
	 */
	public NotationBuilder addMethodCallingPosition(String name, int callInitiationRow, int leadOfTenor) {
		checkArgument(callInitiationRow >= 0, "call initiation row [" + callInitiationRow + "] must be positive");
		checkArgument(callInitiationRow >= 0, "lead of tenor [" + leadOfTenor + "] must be positive");
		checkNotNull(name, "name must not be null");
		checkArgument(name.length() > 0, "name length must be greater than 0");

		for (NotationMethodCallingPosition methodCallingPosition : methodCallingPositions) {
			checkArgument(!methodCallingPosition.getName().equals(name), "name [" + name + "] is not unique");
			checkArgument(!(methodCallingPosition.getLeadOfTenor() == leadOfTenor &&
							methodCallingPosition.getCallInitiationRow() == callInitiationRow),
							"calling position [" + callInitiationRow + ", " + leadOfTenor + "] is not unique");
		}
		methodCallingPositions.add(new DefaultNotationMethodCallingPosition(callInitiationRow, leadOfTenor, name));

		return this;
	}

	/**
	 * Set the splice identifier. This can be multi characters, but is best
	 * kept to a single character for clarity in a UI.
	 */
	public NotationBuilder setSpliceIdentifier(final String spliceIdentifier) {
		this.spliceIdentifier = checkNotNull(spliceIdentifier, "spliceIdentifier must not be null");
		return this;
	}

	private SortedSet<NotationCall> buildCalls() {
		SortedSet<NotationCall> notationCalls = new TreeSet<>(NotationCall.BY_NAME);
		for (NotationCallBuilder callBuilder : notationCallBuilders) {
			NotationCall notationCall = callBuilder.build();
			notationCalls.add(notationCall);
		}

		for (NotationCall notationCallFrom : notationCalls) {

			for (NotationCall notationCallTo : notationCalls) {
				if (notationCallFrom != notationCallTo){
					String displayStringFrom = notationCallFrom.getNotationDisplayString(true);
					String displayStringTo = notationCallTo.getNotationDisplayString(true);
					if (displayStringFrom.equals(displayStringTo)) {
						throw new IllegalArgumentException("Notation clash between [" + notationCallFrom + "] and [" + notationCallTo + "]");
					}
				}
			}
		}
		return notationCalls;
	}

	private Set<Integer> getValidatedCallInitiationRows(Set<Integer> leadCallPositions, int changesCountInPlainLead) {
		Set<Integer> validatedLeadCallPositions = new HashSet<>();
		for (Integer callPosition : leadCallPositions) {
			checkPositionIndex(callPosition.intValue(), changesCountInPlainLead, "Call position is greater than the length of the plain lead location.");
			validatedLeadCallPositions.add(callPosition);
		}

		if (validatedLeadCallPositions.size() == 0) { //TODO formalise this with a boolean to add default lead based calling positions
			validatedLeadCallPositions.add(changesCountInPlainLead - 1);
		}

		return validatedLeadCallPositions;
	}

	private Set<NotationMethodCallingPosition> getValidatedMethodCallingPositions(Set<Integer> validatedLeadCallPositions, Set<NotationMethodCallingPosition> methodCallingPositions, int leadsInPlainCourse) {
		Set<NotationMethodCallingPosition> validatedMethodCallPositions = new HashSet<>();

		for (NotationMethodCallingPosition methodCallingPosition : methodCallingPositions) {
			checkState(validatedLeadCallPositions.contains(methodCallingPosition.getCallInitiationRow()), "Lead calling position must be one of the valid lead calling position.");
			checkState(methodCallingPosition.getLeadOfTenor() < leadsInPlainCourse, "Lead of tenor cant be greater than the number of leads in a plain course [" + leadsInPlainCourse + "]");
			validatedMethodCallPositions.add(methodCallingPosition);
		}
		return validatedMethodCallPositions;
	}

	private Method buildPlainCourse(List<NotationRow> normalisedNotationElements) {
		NotationBody plainCourseNotation = new PlainCourseNotationBody(name, numberOfWorkingBells, normalisedNotationElements);
		return PlainCourseHelper.buildPlainCourse(plainCourseNotation, "[NotationBuilder] ", false).getCreatedMethod();
	}

	private NotationCall getDefaultNotationCall(SortedSet<NotationCall> notationCalls) {
		// Try and match the passed name first
		for (NotationCall notationCall : notationCalls) {
			if (notationCall.getName().equals(defaultCallName)) {
				return notationCall;
			}
		}

		// Try and find one called bob
		for (NotationCall notationCall : notationCalls) {
			if (notationCall.getName().equalsIgnoreCase("bob")) {
				return notationCall;
			}
		}

		// If not take the first call in the notationCalls sorted set.
		if (notationCalls.size() > 0) {
			return notationCalls.iterator().next();
		}

		return null;
	}

}
