package org.ringingmaster.engine.notation;

import com.google.common.base.Strings;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.helper.PlainCourseHelper;
import org.ringingmaster.engine.method.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.concurrent.NotThreadSafe;
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
 * Take user or library input for a notation and verify and build a Notation Object that
 * the engine can use to create a method. Set the state of the notation using
 * the various setter methods, and then call build() to get the notation
 * Each instance should only be used once and discarded as it contains state.
 * <p>
 * This forces that the construction of the Notation is verified.
 *
 * @author Steve Lake
 */

// TODO need to ensure that canned calls (and other components) are re-created when loaded from serialised. This will then take into account options like type 'm' calls being optionally near or extreme . Will also save space when serialised.
//TODO remove classes from impl package - use package visability innstead.
@NotThreadSafe
public class NotationBuilder {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public static final int SPLICE_IDENTIFIER_MAX_LENGTH = 3;

    private String name = "Unknown";
    private NumberOfBells numberOfWorkingBells;
    private final List<String> notationShorthands = new ArrayList<>();
    private boolean foldedPalindrome = false;
    private List<CallBuilder> callBuilders = new ArrayList<>();
    private boolean cannedCalls = false;
    private String defaultCallName = "";
    private String spliceIdentifier;
    private Set<Integer> callInitiationRows = new HashSet<>();
    private Set<CallingPosition> methodCallingPositions = new HashSet<>();

    private NotationBuilder() {
    }

    public static NotationBuilder getInstance() {
        return new NotationBuilder();
    }

    /**
     * Builds the notation based on the set values.
     *
     * @return Notation, built to the set values.
     */
    public Notation build() {
        checkState(!Strings.isNullOrEmpty(name), "Please enter a name");
        checkState(notationShorthands.size() > 0, "Please enter a notation");
        checkState(numberOfWorkingBells != null, "Please add number of bells");

        final List<List<PlaceSet>> notationElementsSets = new ArrayList<>();

        for (String notationShorthand : notationShorthands) {
            final List<PlaceSet> notationElements = NotationBuilderHelper.getValidatedRowsFromShorthand(notationShorthand, numberOfWorkingBells);
            notationElementsSets.add(notationElements);
        }

        List<PlaceSet> normalisedNotationElements;
        if (foldedPalindrome) {
            normalisedNotationElements = NotationBuilderHelper.buildNormalisedFoldedPalindrome(notationElementsSets);
        } else {
            normalisedNotationElements = NotationBuilderHelper.buildNormalisedFullNotation(notationElementsSets.get(0));
        }
        if (normalisedNotationElements.size() == 0) {
            log.info("After validation, all [{}] notation elements were removed as invalid. Returning empty Notation. [{}],[{}], [{}]", name, notationShorthands, numberOfWorkingBells);
            return new DefaultNotation(name,
                    numberOfWorkingBells,
                    normalisedNotationElements,
                    notationElementsSets,
                    foldedPalindrome,
                    "",
                    cannedCalls,
                    Collections.emptySet(),
                    null,
                    Collections.emptySet(),
                    Collections.emptySet(),
                    spliceIdentifier);
        }
        final Method plainCourse = buildPlainCourse(normalisedNotationElements);
        final int changesCountInPlainLead = plainCourse.getLead(0).getRowCount() - 1; // minus 1 as first and last change are shared between leads.
        String leadHeadCode = LeadHeadCalculator.calculateLeadHeadCode(plainCourse.getLead(0), normalisedNotationElements);
        LeadHeadCalculator.LeadHeadType leadHeadType = LeadHeadCalculator.getLeadHeadType(leadHeadCode);
        final SortedSet<Call> calls = buildCalls(leadHeadType);
        final Call defaultCall = getDefaultNotationCall(calls);
        final Set<Integer> validatedCallInitiationRows = getValidatedCallInitiationRows(callInitiationRows, changesCountInPlainLead);
        final Set<CallingPosition> validatedCallingPositions =
                getValidatedMethodCallingPositions(validatedCallInitiationRows, methodCallingPositions, plainCourse.getLeadCount());

        return new DefaultNotation(name,
                numberOfWorkingBells,
                normalisedNotationElements,
                notationElementsSets,
                foldedPalindrome,
                leadHeadCode,
                cannedCalls,
                calls,
                defaultCall,
                validatedCallInitiationRows,
                validatedCallingPositions,
                spliceIdentifier);
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
     *
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
     * folded palindrome symmetry notation 'x.14.12', '16' becomes 'x.14.12.14.x16'
     * folded palindrome symmetry notation 'x.14', '18.16' becomes 'x.14.x.18.16.18'
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
        checkState(cannedCalls == false, "Set either canned calls or actual calls.");

        CallBuilder newCallBuilder = new CallBuilder()
                .setName(name)
                .setNameShorthand(nameShorthand)
                .setUnfoldedNotationShorthand(callNotation);

        for (CallBuilder callBuilder : callBuilders) {
            newCallBuilder.checkClashWith(callBuilder);
        }
        callBuilders.add(newCallBuilder);
        if (defaultCall) {
            this.defaultCallName = name;
        }
        return this;
    }

    public NotationBuilder setCannedCalls() {
        checkState(callBuilders.size() == 0, "Set either canned calls or actual calls.");

        cannedCalls = true;
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

        for (CallingPosition methodCallingPosition : methodCallingPositions) {
            checkArgument(!methodCallingPosition.getName().equals(name), "name [" + name + "] is not unique");
            checkArgument(!(methodCallingPosition.getLeadOfTenor() == leadOfTenor &&
                            methodCallingPosition.getCallInitiationRow() == callInitiationRow),
                    "calling position [" + callInitiationRow + ", " + leadOfTenor + "] is not unique");
        }
        methodCallingPositions.add(new DefaultCallingPosition(callInitiationRow, leadOfTenor, name));

        return this;
    }

    /**
     * Set the splice identifier. This can be multi characters, but is best
     * kept to a single character for clarity in a UI.
     */
    public NotationBuilder setSpliceIdentifier(final String spliceIdentifier) {
        // Dont do a null check here is a null is acceptable.
        if (spliceIdentifier != null) {
            checkState((spliceIdentifier.length() <= SPLICE_IDENTIFIER_MAX_LENGTH), "Splice identifier should be '%s' characters or less", SPLICE_IDENTIFIER_MAX_LENGTH);
        }
        this.spliceIdentifier = spliceIdentifier;
        return this;
    }

    private SortedSet<Call> buildCalls(LeadHeadCalculator.LeadHeadType leadHeadType) {
        SortedSet<Call> calls = new TreeSet<>(Call.BY_NAME);

        if (cannedCalls) {
            if (leadHeadType != null) {
                if (leadHeadType.equals(LeadHeadCalculator.LeadHeadType.NEAR)) {
                    calls.add(
                            new CallBuilder().setName("Bob")
                                    .setNameShorthand("-")
                                    .setUnfoldedNotationShorthand("14")
                                    .build(numberOfWorkingBells));
                    calls.add(
                            new CallBuilder().setName("Single")
                                    .setNameShorthand("s")
                                    .setUnfoldedNotationShorthand("1234")
                                    .build(numberOfWorkingBells));
                } else if (leadHeadType.equals(LeadHeadCalculator.LeadHeadType.EXTREME)) {
                    calls.add(
                            new CallBuilder().setName("Bob")
                                    .setNameShorthand("-")
                                    .setUnfoldedNotationShorthand("1" + Place.valueOf(numberOfWorkingBells.toInt() - 2 - 1).toDisplayString())
                                    .build(numberOfWorkingBells));
                    calls.add(
                            new CallBuilder().setName("Single")
                                    .setNameShorthand("s")
                                    .setUnfoldedNotationShorthand("1" +
                                            Place.valueOf(numberOfWorkingBells.toInt() - 2 - 1).toDisplayString() +
                                            Place.valueOf(numberOfWorkingBells.toInt() - 1 - 1).toDisplayString() +
                                            Place.valueOf(numberOfWorkingBells.toInt() - 0 - 1).toDisplayString())
                                    .build(numberOfWorkingBells));
                }
            }
        } else {
            for (CallBuilder callBuilder : callBuilders) {
                Call call = callBuilder.build(numberOfWorkingBells);
                calls.add(call);
            }
        }

        for (Call callFrom : calls) {

            for (Call callTo : calls) {
                if (callFrom != callTo) {
                    String displayStringFrom = callFrom.getNotationDisplayString(true);
                    String displayStringTo = callTo.getNotationDisplayString(true);
                    if (displayStringFrom.equals(displayStringTo)) {
                        throw new IllegalArgumentException("Notation of call [" + callFrom.toDisplayString() + "] clashes with Notation of call [" + callTo.toDisplayString() + "]");
                    }
                }
            }
        }
        return calls;
    }

    private Set<Integer> getValidatedCallInitiationRows(Set<Integer> leadCallingPositions, int changesCountInPlainLead) {
        Set<Integer> validatedLeadCallingPositions = new HashSet<>();
        for (Integer callingPosition : leadCallingPositions) {
            checkPositionIndex(callingPosition.intValue(), changesCountInPlainLead, "Call position is greater than the length of the plain lead location.");
            validatedLeadCallingPositions.add(callingPosition);
        }

        if (validatedLeadCallingPositions.size() == 0) { //TODO formalise this with a boolean to add default lead based calling positions
            validatedLeadCallingPositions.add(changesCountInPlainLead - 1);
        }

        return validatedLeadCallingPositions;
    }

    private Set<CallingPosition> getValidatedMethodCallingPositions(Set<Integer> validatedLeadCallingPositions, Set<CallingPosition> methodCallingPositions, int leadsInPlainCourse) {
        Set<CallingPosition> validatedMethodCallingPositions = new HashSet<>();

        for (CallingPosition methodCallingPosition : methodCallingPositions) {
            checkState(validatedLeadCallingPositions.contains(methodCallingPosition.getCallInitiationRow()), "Lead calling position must be one of the valid lead calling position.");
            checkState(methodCallingPosition.getLeadOfTenor() < leadsInPlainCourse, "Lead of tenor cant be greater than the number of leads in a plain course [" + leadsInPlainCourse + "]");
            validatedMethodCallingPositions.add(methodCallingPosition);
        }
        return validatedMethodCallingPositions;
    }

    private Method buildPlainCourse(List<PlaceSet> normalisedNotationElements) {
        Notation plainCourseNotation =
                new DefaultNotation(name,
                        numberOfWorkingBells,
                        normalisedNotationElements,
                        Collections.emptyList(),
                        true, //TODO should this be defaulting to true??
                        "",
                        false,
                        Collections.emptySet(),
                        null,
                        Collections.emptySet(),
                        Collections.emptySet(),
                        spliceIdentifier);

        return PlainCourseHelper.buildPlainCourse(plainCourseNotation, "NotationBuilder > ").getMethod().get();
    }

    private Call getDefaultNotationCall(SortedSet<Call> calls) {
        // Try and match the passed name first
        for (Call call : calls) {
            if (call.getName().equals(defaultCallName)) {
                return call;
            }
        }

        // Try and find one called bob
        for (Call call : calls) {
            if (call.getName().equalsIgnoreCase("bob")) {
                return call;
            }
        }

        // If not take the first call in the calls sorted set.
        if (calls.size() > 0) {
            return calls.iterator().next();
        }

        return null;
    }

}
