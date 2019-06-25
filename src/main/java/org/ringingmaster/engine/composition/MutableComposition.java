package org.ringingmaster.engine.composition;


import com.google.common.base.Strings;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import org.pcollections.PSet;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.arraytable.BackingTableLocationAndValue;
import org.ringingmaster.engine.arraytable.ImmutableArrayTable;
import org.ringingmaster.engine.arraytable.TableBackedImmutableArrayTable;
import org.ringingmaster.engine.composition.cell.Cell;
import org.ringingmaster.engine.composition.cell.CellBuilder;
import org.ringingmaster.engine.composition.cell.EmptyCell;
import org.ringingmaster.engine.composition.compositiontype.CompositionType;
import org.ringingmaster.engine.composition.tableaccess.DefaultCompositionTableAccess;
import org.ringingmaster.engine.composition.tableaccess.DefaultDefinitionTableAccess;
import org.ringingmaster.engine.method.Bell;
import org.ringingmaster.engine.method.MethodBuilder;
import org.ringingmaster.engine.method.Row;
import org.ringingmaster.engine.method.Stroke;
import org.ringingmaster.engine.notation.Notation;
import org.ringingmaster.engine.notation.NotationBuilder;
import org.ringingmaster.engine.notation.NotationBuilderHelper;
import org.ringingmaster.engine.notation.PlaceSetSequence;
import org.ringingmaster.util.smartcompare.SmartCompare;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkPositionIndex;
import static com.google.common.base.Preconditions.checkState;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static org.ringingmaster.engine.composition.DryRun.DryRunResult.NO_CHANGE;
import static org.ringingmaster.engine.composition.DryRun.DryRunResult.SUCCESS;
import static org.ringingmaster.engine.composition.TableType.DEFINITION_TABLE;
import static org.ringingmaster.engine.composition.tableaccess.DefinitionTableAccess.DEFINITION_COLUMN;
import static org.ringingmaster.engine.composition.tableaccess.DefinitionTableAccess.SHORTHAND_COLUMN;
import static org.ringingmaster.engine.notation.PlaceSetSequence.BY_NUMBER_THEN_NAME;


/**
 * TODO Comments
 *
 * @author Steve Lake
 */
public class MutableComposition {

    public static final int START_AT_ROW_MAX = 10_000;
    public static final int TERMINATION_MAX_ROWS_INITIAL_VALUE = 10_000;
    public static final int TERMINATION_MAX_ROWS_MAX = 10_000_000;
    public static final int TERMINATION_MAX_LEADS_MAX = 100_000;
    public static final int TERMINATION_MAX_PARTS_MAX = 10_000;
    public static final int TERMINATION_MAX_CIRCULARITY_INITIAL_VALUE = 2;
    public static final int TERMINATION_MAX_CIRCULARITY_MAX = 10_000;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final BehaviorSubject<Composition> compositionStream;
    private final SmartCompare smartCompare = new SmartCompare("", "> ")
            .comparePaths("numberOfBells")
            .comparePaths("startChange")
            .ignorePaths("actionName")
            .ignorePaths("sequenceNumber")
            .bindComparator((field, object1, object2) -> ((DefaultCompositionTableAccess) object1).allCompositionCells() == ((DefaultCompositionTableAccess) object2).allCompositionCells(), "compositionTableAccessDelegate")
            .bindComparator((field, object1, object2) -> ((DefaultDefinitionTableAccess) object1).allDefinitionCells() == ((DefaultDefinitionTableAccess) object2).allDefinitionCells(), "definitionTableCellsDelegate");

    public MutableComposition() {
        compositionStream = BehaviorSubject.createDefault(new CompositionBuilder().defaults().build("Initialise"));
        if (log.isInfoEnabled()) {
            compositionStream.buffer(2, 1).subscribe(compositions -> {
                log.info("[{}] Action:[{}] Diff [{}]",
                        compositions.get(1).getLoggingTag(), compositions.get(1).getActionName(),
                        smartCompare.stringDifferences(compositions.get(0), compositions.get(1)));
            });
        }
    }

    public Observable<Composition> observable() {
        return compositionStream;
    }

    public Composition get() {
        return compositionStream.getValue();
    }

    public void renotify() {
        log.info("Renotify");
        compositionStream.onNext(get());
    }

    public void setTitle(String title) {
        checkNotNull(title);

        if (Objects.equals(compositionStream.getValue().getTitle(), title)) {
            renotify();
            return;
        }

        CompositionBuilder compositionBuilder = new CompositionBuilder().prototypeOf(compositionStream.getValue())
                .setTitle(title);

        compositionStream.onNext(compositionBuilder.build("Set Title"));
    }

    public void setAuthor(String author) {
        checkNotNull(author);

        if (Objects.equals(compositionStream.getValue().getAuthor(), author)) {
            renotify();
            return;
        }

        CompositionBuilder compositionBuilder = new CompositionBuilder().prototypeOf(compositionStream.getValue())
                .setAuthor(author);

        compositionStream.onNext(compositionBuilder.build("Set Author"));
    }


    public DryRun dryRunSetNumberOfBells(NumberOfBells numberOfBells) {
        checkNotNull(numberOfBells);

        if (get().getNumberOfBells() == numberOfBells) {
            return new DryRun(NO_CHANGE);
        }

        List<String> messages = Lists.newArrayList();
        int pointNumber = 1;

        // Call From Bell
        if (get().getCallFromBell().getZeroBasedBell() > numberOfBells.getTenor().getZeroBasedBell()) {
            messages.add(pointNumber++ + ") Call from bell will change from " +
                    (get().getCallFromBell().getZeroBasedBell() + 1) +
                    " to " + (numberOfBells.getTenor().getZeroBasedBell() + 1) + ".");
        }

        // Start Change
        final Row existingStartChange = get().getStartChange();
        final Row newInitialRow = MethodBuilder.transformToNewNumberOfBells(existingStartChange, numberOfBells);
        messages.add(pointNumber++ + ") Start change will change from '" +
                existingStartChange.getDisplayString(false) +
                "' to '" +
                newInitialRow.getDisplayString(false) +
                "'.");

        //Termination Row
        if (get().getTerminationChange().isPresent()) {
            final Row existingTerminationRow = get().getTerminationChange().get();
            final Row newTerminationRow = MethodBuilder.transformToNewNumberOfBells(existingTerminationRow, numberOfBells);

            messages.add(pointNumber++ + ") Termination row will change from '" +
                    existingTerminationRow.getDisplayString(false) +
                    "' to '" +
                    newTerminationRow.getDisplayString(false) +
                    "'.");
        }

        // Managing active method.
        if (!get().isSpliced() &&
                get().getNonSplicedActiveNotation().isPresent() &&
                get().getNonSplicedActiveNotation().get().getNumberOfWorkingBells().toInt() > numberOfBells.toInt()) {
            final PSet<Notation> filteredNotations = NotationBuilderHelper.filterNotationsUptoNumberOfBells(get().getAllNotations(), numberOfBells);
            StringBuilder message = new StringBuilder();
            message.append(pointNumber++).append(") Active method '")
                    .append(get().getNonSplicedActiveNotation().get().getNameIncludingNumberOfBells())
                    .append("' ");
            if (filteredNotations.size() == 0) {
                message.append("will be unset. There is no suitable replacement.");
            } else {
                message.append("will change to '")
                        .append(filteredNotations.stream().sorted(BY_NUMBER_THEN_NAME).findFirst().get().getNameIncludingNumberOfBells())
                        .append("'");
            }
            messages.add(message.toString());
        }

        // Start notation
        if (get().getStartNotation().isPresent()) {
            final String notation = get().getStartNotation().get().getNotationDisplayString(false);
            Notation builtNotation = NotationBuilder.getInstance()
                    .setNumberOfWorkingBells(numberOfBells)
                    .setUnfoldedNotationShorthand(notation)
                    .build();
            String newNotation = builtNotation.getNotationDisplayString(false);
            if (!newNotation.equals(notation)) {

                messages.add(pointNumber++ + ") Start notation '" +
                        notation +
                        "' " +
                        "will change to '" +
                        newNotation +
                        "'");
            }
        }
        return new DryRun(SUCCESS, messages);
    }

    public void setNumberOfBells(NumberOfBells numberOfBells) {
        checkNotNull(numberOfBells);

        if (Objects.equals(compositionStream.getValue().getNumberOfBells(), numberOfBells)) {
            renotify();
            return;
        }

        CompositionBuilder compositionBuilder = new CompositionBuilder()
                .prototypeOf(compositionStream.getValue())
                .setNumberOfBells(numberOfBells);

        if (compositionStream.getValue().getCallFromBell().getZeroBasedBell() > numberOfBells.getTenor().getZeroBasedBell()) {
            compositionBuilder.setCallFromBell(numberOfBells.getTenor());
        }

        final Row existingStartChange = compositionStream.getValue().getStartChange();
        final Row newStartChange = MethodBuilder.transformToNewNumberOfBells(existingStartChange, numberOfBells);
        compositionBuilder.setStartChange(newStartChange);

        if (compositionStream.getValue().getTerminationChange().isPresent()) {
            final Row existingTerminationRow = compositionStream.getValue().getTerminationChange().get();
            final Row newTerminationRow = MethodBuilder.transformToNewNumberOfBells(existingTerminationRow, numberOfBells);
            compositionBuilder.setTerminationChange(Optional.of(newTerminationRow));
        }

        if (!compositionStream.getValue().isSpliced() &&
                compositionStream.getValue().getNonSplicedActiveNotation().isPresent() &&
                compositionStream.getValue().getNonSplicedActiveNotation().get().getNumberOfWorkingBells().toInt() > numberOfBells.toInt()) {
            Optional<Notation> nextBestNonSplicedActiveNotation = findNextBestNonSplicedActiveNotation(compositionStream.getValue().getNonSplicedActiveNotation().get());
            compositionBuilder.setNonSplicedActiveNotation(nextBestNonSplicedActiveNotation);
        }

        if (compositionStream.getValue().getStartNotation().isPresent()) {
            final String originalNotation = compositionStream.getValue().getStartNotation().get().getNotationDisplayString(false);
            Notation builtNotation = NotationBuilder.getInstance()
                    .setNumberOfWorkingBells(numberOfBells)
                    .setUnfoldedNotationShorthand(originalNotation)
                    .build();
            if (builtNotation.size() == 0) {
                compositionBuilder.setStartNotation(Optional.empty());
            } else {
                compositionBuilder.setStartNotation(Optional.of(builtNotation));
            }
        }

        compositionStream.onNext(compositionBuilder.build("Set number of bells to: %s", numberOfBells.getDisplayString()));
    }

    public void setCompositionType(CompositionType compositionType) {
        checkNotNull(compositionType);

        if (Objects.equals(compositionStream.getValue().getCompositionType(), compositionType)) {
            renotify();
            return;
        }

        CompositionBuilder compositionBuilder = new CompositionBuilder().prototypeOf(compositionStream.getValue())
                .setCompositionType(compositionType);

        compositionStream.onNext(compositionBuilder.build("Set Checking Type: %s", compositionType.getName()));
    }

    public void setCallFromBell(Bell callFromBell) {
        checkNotNull(callFromBell);
        checkArgument(callFromBell.getZeroBasedBell() < compositionStream.getValue().getNumberOfBells().toInt());

        if (Objects.equals(compositionStream.getValue().getCallFromBell(), callFromBell)) {
            renotify();
            return;
        }

        CompositionBuilder compositionBuilder = new CompositionBuilder().prototypeOf(compositionStream.getValue())
                .setCallFromBell(callFromBell);

        compositionStream.onNext(compositionBuilder.build("Set number of bells to: %s", callFromBell.getDisplayString()));
    }

    private Optional<Notation> findNextBestNonSplicedActiveNotation(Notation previousNotation) {
        final List<Notation> validNotations = Lists.newArrayList(compositionStream.getValue().getValidNotations());
        validNotations.remove(previousNotation);

        Comparator<NumberOfBells> byDistanceFromPassedNumberOfBells = (o1, o2) -> ComparisonChain.start()
                .compare(Math.abs(previousNotation.getNumberOfWorkingBells().toInt() - o1.toInt()),
                        Math.abs(previousNotation.getNumberOfWorkingBells().toInt() - o2.toInt()))
                .compare(o2.toInt(), o1.toInt()) // always take higher number of bells where distance is equal
                .result();

        // from the validNotations, find all number of bells in use, sorted by distance from passed number of bells.
        Optional<NumberOfBells> bestNumberOfBells = validNotations.stream()
                .map(PlaceSetSequence::getNumberOfWorkingBells)
                .sorted(byDistanceFromPassedNumberOfBells)
                .findFirst();

        if (bestNumberOfBells.isEmpty()) {
            return Optional.empty();
        }

        // Try notations that are lexicographically the same or higher
        Optional<Notation> lexicographicallyHigher = validNotations.stream()
                .filter(notation -> notation.getNumberOfWorkingBells() == bestNumberOfBells.get())
                .filter(notation -> notation.getName().compareTo(previousNotation.getName()) >= 0)
                .sorted(PlaceSetSequence.BY_NAME)
                .findFirst();
        if (lexicographicallyHigher.isPresent()) {
            return lexicographicallyHigher;
        }

        // Try notations that are lexicographically lower
        Optional<Notation> lexicographicallyLower = validNotations.stream()
                .filter(notation -> notation.getNumberOfWorkingBells() == bestNumberOfBells.get())
                .filter(notation -> notation.getName().compareTo(previousNotation.getName()) < 0)
                .sorted(PlaceSetSequence.BY_NAME.reversed())
                .findFirst();

        return lexicographicallyLower;

    }

    public DryRun dryRunAddNotation(Notation notationToAdd) {
        return dryRunPotentialNewNotation(notationToAdd, Collections.emptySet());
    }

    public void addNotation(Notation notationToAdd) {
        checkNotNull(notationToAdd, "notation must not be null");

        DryRun dryRun = dryRunAddNotation(notationToAdd);

        if (dryRun.result() != SUCCESS) {
            String message = dryRun.getMessages().stream().collect(Collectors.joining(System.lineSeparator()));
            throw new IllegalArgumentException("Can't add notation [" + notationToAdd + "]: " + System.lineSeparator() + message); // TODO why not just return false like all the other methods
        }

        CompositionBuilder compositionBuilder = new CompositionBuilder().prototypeOf(compositionStream.getValue());

        PSet<Notation> withAddedNotation = compositionStream.getValue().getAllNotations().plus(notationToAdd);
        compositionBuilder.setAllNotations(withAddedNotation);

//TODO what if the number of bells is wrong?
        if (!compositionStream.getValue().isSpliced() && !compositionStream.getValue().getNonSplicedActiveNotation().isPresent()) {
            compositionBuilder.setNonSplicedActiveNotation(Optional.of(notationToAdd));
        }

        compositionStream.onNext(compositionBuilder.build("Add method: %s", notationToAdd.getNameIncludingNumberOfBells()));
    }

    private DryRun dryRunPotentialNewNotation(Notation notationToCheck, Set<Notation> notationsToExclude) {
        checkNotNull(notationToCheck);
        checkNotNull(notationsToExclude);

        List<String> messages = new ArrayList<>();
        PSet<Notation> allNotationsWithExclusions = compositionStream.getValue().getAllNotations().minusAll(notationsToExclude);

        // Look for clash in the number of bells and name combination.
        messages.addAll(allNotationsWithExclusions.stream()
                .filter(existingNotation -> (existingNotation.getNumberOfWorkingBells() == notationToCheck.getNumberOfWorkingBells()) &&
                        (equal(existingNotation.getName(), notationToCheck.getName())))
                .map(existingNotation -> "An existing method with notation '" + existingNotation.getNotationDisplayString(true) + "' has the same Name and Number Of Bells.")
                .collect(Collectors.toList()));

        // Look for clash in the splice identifiers
        messages.addAll(allNotationsWithExclusions.stream()
                .filter(existingNotation -> (!Strings.isNullOrEmpty(notationToCheck.getSpliceIdentifier()) &&
                        equal(existingNotation.getSpliceIdentifier(), notationToCheck.getSpliceIdentifier())))
                .map(existingNotation -> "An existing method '" + existingNotation.getNameIncludingNumberOfBells() + "' has the same Splice Identifier.")
                .collect(Collectors.toList()));

        // Look for clash in the umber of bells and notation combination
        messages.addAll(allNotationsWithExclusions.stream()
                .filter(existingNotation -> (existingNotation.getNumberOfWorkingBells() == notationToCheck.getNumberOfWorkingBells()) &&
                        equal(existingNotation.getNotationDisplayString(true), notationToCheck.getNotationDisplayString(true)))
                .map(existingNotation -> "An existing method '" + existingNotation.getNameIncludingNumberOfBells() + "' has the same Notation '" + notationToCheck.getNotationDisplayString(false) + "'.")
                .collect(Collectors.toList()));

        return new DryRun(messages.isEmpty() ? SUCCESS : DryRun.DryRunResult.FAIL, messages);
    }

    public void removeNotation(Notation notationForRemoval) {
        checkNotNull(notationForRemoval, "notationForRemoval must not be null");

        PSet<Notation> allNotations = compositionStream.getValue().getAllNotations();
        checkState(allNotations.contains(notationForRemoval));

        PSet<Notation> withRemovedNotation = allNotations.minus(notationForRemoval);
        CompositionBuilder compositionBuilder = new CompositionBuilder().prototypeOf(compositionStream.getValue())
                .setAllNotations(withRemovedNotation);

        // Sort out the next notation if it is the active notation
        if (compositionStream.getValue().getNonSplicedActiveNotation().isPresent() &&
                notationForRemoval.equals(compositionStream.getValue().getNonSplicedActiveNotation().get())) {
            Optional<Notation> nextBestNonSplicedActiveNotation = findNextBestNonSplicedActiveNotation(notationForRemoval);
            compositionBuilder.setNonSplicedActiveNotation(nextBestNonSplicedActiveNotation);
        }

        compositionStream.onNext(compositionBuilder.build("Remove method: %s", notationForRemoval.getNameIncludingNumberOfBells()));
    }

    public DryRun dryRunExchangeNotation(Notation originalNotation, Notation replacementNotation) {
        checkNotNull(originalNotation);
        checkNotNull(replacementNotation);

        return dryRunPotentialNewNotation(replacementNotation, Sets.<Notation>newHashSet(originalNotation));
    }

    public void exchangeNotation(Notation originalNotation, Notation replacementNotation) {
        checkNotNull(originalNotation, "originalNotation must not be null");
        checkNotNull(replacementNotation, "replacementNotation must not be null");
        checkArgument(originalNotation != replacementNotation);


        PSet<Notation> allNotations = compositionStream.getValue().getAllNotations();
        checkState(allNotations.contains(originalNotation));

        DryRun dryRun = dryRunExchangeNotation(originalNotation, replacementNotation);

        if (dryRun.result() != SUCCESS) {
            String message = dryRun.getMessages().stream().collect(Collectors.joining(System.lineSeparator()));
            throw new IllegalArgumentException("Can't update notation [" + replacementNotation + "]: " + System.lineSeparator() + message);
        }

        CompositionBuilder compositionBuilder = new CompositionBuilder().prototypeOf(compositionStream.getValue());

        allNotations = allNotations.minus(originalNotation)
                .plus(replacementNotation);
        compositionBuilder.setAllNotations(allNotations);

        if (compositionStream.getValue().getNonSplicedActiveNotation().isPresent() &&
                compositionStream.getValue().getNonSplicedActiveNotation().get() == originalNotation) {
            if (replacementNotation.getNumberOfWorkingBells().toInt() > compositionStream.getValue().getNumberOfBells().toInt()) {
                Optional<Notation> nextBestNonSplicedActiveNotation = findNextBestNonSplicedActiveNotation(replacementNotation);
                compositionBuilder.setNonSplicedActiveNotation(nextBestNonSplicedActiveNotation);
            } else {
                compositionBuilder.setNonSplicedActiveNotation(Optional.of(replacementNotation));
            }
        }
        compositionStream.onNext(compositionBuilder.build("Update method: %s", originalNotation.getNameIncludingNumberOfBells()));
    }

    public void setNonSplicedActiveNotation(Notation nonSplicedActiveNotation) {
        checkNotNull(nonSplicedActiveNotation);
        checkState(compositionStream.getValue().getAllNotations().contains(nonSplicedActiveNotation), "Can't set NonSplicedActiveNotation to notation not part of composition.");

        if (compositionStream.getValue().getNonSplicedActiveNotation().isPresent() &&
                Objects.equals(compositionStream.getValue().getNonSplicedActiveNotation().get(), nonSplicedActiveNotation)) {
            renotify();
            return;
        }

        CompositionBuilder compositionBuilder = new CompositionBuilder().prototypeOf(compositionStream.getValue())
                .setNonSplicedActiveNotation(Optional.of(nonSplicedActiveNotation));

        compositionStream.onNext(compositionBuilder.build("Set active method: %s", nonSplicedActiveNotation.getNameIncludingNumberOfBells()));
    }

    public void setSpliced(boolean spliced) {

        if (Objects.equals(compositionStream.getValue().isSpliced(), spliced)) {
            renotify();
            return;
        }

        CompositionBuilder compositionBuilder = new CompositionBuilder().prototypeOf(compositionStream.getValue());


        if (spliced) {
            compositionBuilder.setNonSplicedActiveNotation(Optional.empty());
        } else {
            final Set<Notation> validNotations = compositionStream.getValue().getValidNotations();

            if (validNotations.size() > 0) {
                Optional<Notation> firstByName = validNotations.stream()
                        .sorted(Notation.BY_NAME)
                        .findFirst();
                compositionBuilder.setNonSplicedActiveNotation(firstByName);
            }
        }

        compositionStream.onNext(compositionBuilder.build((spliced ? "Set spliced" : "Set non spliced")));
    }

    public void setPlainLeadToken(String plainLeadToken) {
        checkNotNull(plainLeadToken);

        if (Objects.equals(compositionStream.getValue().getPlainLeadToken(), plainLeadToken)) {
            renotify();
            return;
        }

        CompositionBuilder compositionBuilder = new CompositionBuilder().prototypeOf(compositionStream.getValue())
                .setPlainLeadToken(plainLeadToken);

        compositionStream.onNext(compositionBuilder.build("Set Plain Lead Token: %s", plainLeadToken));
    }

    public void addDefinition(String shorthand, String characters) {
        checkNotNull(shorthand, "shorthand must not be null");
        checkState(shorthand.length() > 0, "shorthand must contain some characters");

        // Check duplicate name
        shorthand = shorthand.trim();
        if (compositionStream.getValue().findDefinitionByShorthand(shorthand).isPresent()) {
            throw new IllegalArgumentException("Can't add definition [" + shorthand + "] as it has a duplicate shorthand to existing definition [" + compositionStream.getValue().findDefinitionByShorthand(shorthand) + "]");
        }

        Table<Integer, Integer, Cell> cells = HashBasedTable.create(compositionStream.getValue().allDefinitionCells().getBackingTable());
        int insertionRow = compositionStream.getValue().allDefinitionCells().getRowSize();

        Cell shorthandCell = new CellBuilder()
                .defaults()
                .insert(0, shorthand)
                .build();
        cells.put(insertionRow, 0, shorthandCell);

        Cell charactersCell = new CellBuilder()
                .defaults()
                .insert(0, characters)
                .build();
        cells.put(insertionRow, 1, charactersCell);

        //TODO sort the definitions alpha numeric???

        CompositionBuilder compositionBuilder = new CompositionBuilder().prototypeOf(compositionStream.getValue())
                .setCells(DEFINITION_TABLE, new TableBackedImmutableArrayTable<>(cells, EmptyCell::new));

        compositionStream.onNext(compositionBuilder.build("Add Definition: %s", shorthand));
    }

    public void removeDefinition(String shorthand) {
        checkNotNull(shorthand, "shorthand must not be null");
        checkState(shorthand.length() > 0, "shorthand must contain some characters");

        final ImmutableArrayTable<Cell> definitionCells = compositionStream.getValue().allDefinitionCells();
        Table<Integer, Integer, Cell> mutatedCells = HashBasedTable.create(definitionCells.getBackingTable());

        for (int rowIndex = 0; rowIndex < definitionCells.getRowSize(); rowIndex++) {
            final Cell shorthandCell = mutatedCells.get(rowIndex, SHORTHAND_COLUMN);
            final Cell definitionCell = mutatedCells.get(rowIndex, DEFINITION_COLUMN);
            if (shorthandCell != null) {
                if (Objects.equals(shorthand, shorthandCell.getCharacters())) {
                    removeCharactersInternal(rowIndex, SHORTHAND_COLUMN, 0, shorthandCell.size(), mutatedCells, definitionCells);
                    if (definitionCell != null) {
                        removeCharactersInternal(rowIndex, DEFINITION_COLUMN, 0, definitionCell.size(), mutatedCells, definitionCells);
                    }
                    removeRowIfEmpty(rowIndex, mutatedCells, definitionCells);
                    break;
                }
            }
        }

        CompositionBuilder compositionBuilder = new CompositionBuilder().prototypeOf(compositionStream.getValue())
                .setCells(DEFINITION_TABLE, new TableBackedImmutableArrayTable<>(mutatedCells, EmptyCell::new));

        compositionStream.onNext(compositionBuilder.build("Remove definition: %s", shorthand));
    }

    public void setStartChange(Row startChange) {
        checkNotNull(startChange);
        checkArgument(startChange.getNumberOfBells() == compositionStream.getValue().getNumberOfBells());

        if (Objects.equals(compositionStream.getValue().getStartChange(), startChange)) {
            renotify();
            return;
        }

        CompositionBuilder compositionBuilder = new CompositionBuilder().prototypeOf(compositionStream.getValue())
                .setStartChange(startChange);

        compositionStream.onNext(compositionBuilder.build("Set Start Change: " + startChange.getDisplayString(true)));
    }


    public DryRun dryRunSetStartAtRow(int startAtRow) {
        int result = max(0, min(START_AT_ROW_MAX, startAtRow));

        if (startAtRow != result) {
            return new DryRun(result);
        }

        else {
            return new DryRun(SUCCESS);
        }
    }

    public void setStartAtRow(int startAtRow) {
        checkArgument(startAtRow >= 0, "Start at row must be 0 or greater.");
        checkArgument(startAtRow <= START_AT_ROW_MAX, "Start at row must be less than or equal to %s", START_AT_ROW_MAX);


        if (Objects.equals(compositionStream.getValue().getStartAtRow(), startAtRow)) {
            renotify();
            return;
        }

        CompositionBuilder compositionBuilder = new CompositionBuilder().prototypeOf(compositionStream.getValue())
                .setStartAtRow(startAtRow);

        compositionStream.onNext(compositionBuilder.build("Set Start At Row: %d", startAtRow));
    }

    public void setStartStroke(Stroke startStroke) {
        checkNotNull(startStroke);

        if (Objects.equals(compositionStream.getValue().getStartStroke(), startStroke)) {
            renotify();
            return;
        }

        CompositionBuilder compositionBuilder = new CompositionBuilder().prototypeOf(compositionStream.getValue())
                .setStartStroke(startStroke);

        compositionStream.onNext(compositionBuilder.build("Set Start Stroke: %s", startStroke));
    }

    public void setStartNotation(Notation startNotation) {
        checkNotNull(startNotation);
        checkState(startNotation.getNumberOfWorkingBells() == compositionStream.getValue().getNumberOfBells(), "Start Notation number of bells must match composition number of bells");

        if (compositionStream.getValue().getStartNotation().isPresent() &&
                compositionStream.getValue().getStartNotation().get().getNotationDisplayString(false).equals(startNotation.getNotationDisplayString(false))) {
            renotify();
            return;
        }

        CompositionBuilder compositionBuilder = new CompositionBuilder().prototypeOf(compositionStream.getValue())
                .setStartNotation(Optional.of(startNotation));

        compositionStream.onNext(compositionBuilder.build("Set Start Notation: " + startNotation.getNotationDisplayString(true)));
    }

    public void removeStartNotation() {
        if (compositionStream.getValue().getStartNotation().isEmpty()) {
            renotify();
            return;
        }

        CompositionBuilder compositionBuilder = new CompositionBuilder().prototypeOf(compositionStream.getValue())
                .setStartNotation(Optional.empty());

        compositionStream.onNext(compositionBuilder.build("Remove Start Notation"));
    }

    public DryRun dryRunSetTerminationMaxRows(int terminationMaxRows) {
        int result = max(1, min(TERMINATION_MAX_ROWS_MAX, terminationMaxRows));

        if (terminationMaxRows != result) {
            return new DryRun(result);
        }

        else {
            return new DryRun(SUCCESS);
        }
    }

    public void setTerminationMaxRows(int terminationMaxRows) {
        checkArgument(terminationMaxRows > 0, "Termination max rows must be greater than 0");
        checkArgument(terminationMaxRows <= TERMINATION_MAX_ROWS_MAX, "Termination max rows must be less than or equal to %s", TERMINATION_MAX_ROWS_MAX);

        if (Objects.equals(compositionStream.getValue().getTerminationMaxRows(), terminationMaxRows)) {
            renotify();
            return;
        }

        CompositionBuilder compositionBuilder = new CompositionBuilder().prototypeOf(compositionStream.getValue())
                .setTerminationMaxRows(terminationMaxRows);

        compositionStream.onNext(compositionBuilder.build("Set Row Limit: %d", terminationMaxRows));
    }

    public DryRun dryRunSetTerminationMaxLeads(int terminationMaxLeads) {
        int result = max(1, min(TERMINATION_MAX_LEADS_MAX, terminationMaxLeads));

        if (terminationMaxLeads != result) {
            return new DryRun(result);
        }

        else {
            return new DryRun(SUCCESS);
        }
    }

    public void setTerminationMaxLeads(int terminationMaxLeads) {
        checkArgument(terminationMaxLeads > 0, "Termination max leads must be greater than 0");
        checkArgument(terminationMaxLeads <= TERMINATION_MAX_LEADS_MAX, "Termination max leads must be less than or equal to %s", TERMINATION_MAX_LEADS_MAX);

        if (compositionStream.getValue().getTerminationMaxLeads().isPresent() &&
                Objects.equals(compositionStream.getValue().getTerminationMaxLeads().get(), terminationMaxLeads)) {
            renotify();
            return;
        }

        CompositionBuilder compositionBuilder = new CompositionBuilder().prototypeOf(compositionStream.getValue())
                .setTerminationMaxLeads(Optional.of(terminationMaxLeads));

        compositionStream.onNext(compositionBuilder.build("Set Lead Limit: %d", terminationMaxLeads));
    }

    public void removeTerminationMaxLeads() {
        if (compositionStream.getValue().getTerminationMaxLeads().isEmpty()) {
            renotify();
            return;
        }

        CompositionBuilder compositionBuilder = new CompositionBuilder().prototypeOf(compositionStream.getValue())
                .setTerminationMaxLeads(Optional.empty());

        compositionStream.onNext(compositionBuilder.build("Remove Lead Limit"));
    }

    public DryRun dryRunSetTerminationMaxParts(int terminationMaxParts) {
        int result = max(1, min(TERMINATION_MAX_PARTS_MAX, terminationMaxParts));

        if (terminationMaxParts != result) {
            return new DryRun(result);
        }

        else {
            return new DryRun(SUCCESS);
        }
    }

    public void setTerminationMaxParts(int terminationMaxParts) {
        checkArgument(terminationMaxParts > 0, "Termination max parts must be greater than 0");
        checkArgument(terminationMaxParts <= TERMINATION_MAX_PARTS_MAX, "Termination max parts must be less than or equal to %s", TERMINATION_MAX_PARTS_MAX);

        if (compositionStream.getValue().getTerminationMaxParts().isPresent() &&
                Objects.equals(compositionStream.getValue().getTerminationMaxParts().get(), terminationMaxParts)) {
            renotify();
            return;
        }

        CompositionBuilder compositionBuilder = new CompositionBuilder().prototypeOf(compositionStream.getValue())
                .setTerminationMaxParts(Optional.of(terminationMaxParts));

        compositionStream.onNext(compositionBuilder.build("Set Part Limit: %d", terminationMaxParts));
    }

    public void removeTerminationMaxParts() {
        if (compositionStream.getValue().getTerminationMaxParts().isEmpty()) {
            renotify();
            return;
        }

        CompositionBuilder compositionBuilder = new CompositionBuilder().prototypeOf(compositionStream.getValue())
                .setTerminationMaxParts(Optional.empty());

        compositionStream.onNext(compositionBuilder.build("Remove Part Limit"));
    }

    public DryRun dryRunSetTerminationMaxCircularity(int terminationMaxCircularity) {
        int result = max(1, min(TERMINATION_MAX_ROWS_MAX, terminationMaxCircularity));

        if (terminationMaxCircularity != result) {
            return new DryRun(result);
        }
        else {
            return new DryRun(SUCCESS);
        }
    }

    public void setTerminationMaxCircularity(int terminationMaxCircularity) {
        checkArgument(terminationMaxCircularity > 0, "Termination circular composition must be greater than 0");
        checkArgument(terminationMaxCircularity <= TERMINATION_MAX_CIRCULARITY_MAX, "Termination circular composition must be less than or equal to %s", TERMINATION_MAX_CIRCULARITY_MAX);

        CompositionBuilder compositionBuilder = new CompositionBuilder().prototypeOf(compositionStream.getValue());

        if (Objects.equals(compositionStream.getValue().getTerminationMaxCircularity(), terminationMaxCircularity)) {
            renotify();
            return;
        }

        compositionBuilder.setTerminationMaxCircularity(terminationMaxCircularity);

        compositionStream.onNext(compositionBuilder.build("Set Circular Composition Limit: %d", terminationMaxCircularity));
    }

    public void setTerminationChange(Row terminationChange) {
        checkNotNull(terminationChange, "terminationChange cant be null");
        checkArgument(terminationChange.getNumberOfBells().equals(compositionStream.getValue().getNumberOfBells()));

        if (compositionStream.getValue().getTerminationChange().isPresent() &&
                compositionStream.getValue().getTerminationChange().get().equals(terminationChange)) {
            renotify();
            return;
        }

        CompositionBuilder compositionBuilder = new CompositionBuilder().prototypeOf(compositionStream.getValue())
                .setTerminationChange(Optional.of(terminationChange));

        compositionStream.onNext(compositionBuilder.build("Set Termination Change: " +  terminationChange.getDisplayString(true)));
    }

    public void removeTerminationChange() {
        if (compositionStream.getValue().getTerminationChange().isEmpty()) {
            renotify();
            return;
        }

        CompositionBuilder compositionBuilder = new CompositionBuilder().prototypeOf(compositionStream.getValue())
                .setTerminationChange(Optional.empty());

        compositionStream.onNext(compositionBuilder.build("Remove Termination Change"));
    }

    public void bulkSetCharacters(TableType tableType, Set<BackingTableLocationAndValue<String>> cellCharactersAndLocations) {
        checkNotNull(tableType);
        checkNotNull(cellCharactersAndLocations);

        ImmutableArrayTable<Cell> originalCells = getCells(tableType);

        Table<Integer, Integer, Cell> mutatedCells = HashBasedTable.create(originalCells.getBackingTable());

        for (BackingTableLocationAndValue<String> cellDetail : cellCharactersAndLocations) {

            int columnIndex = cellDetail.getCol();
            int rowIndex = cellDetail.getRow();
            String characters = cellDetail.getValue();

            if (tableType == DEFINITION_TABLE) {
                checkArgument(columnIndex < 2, "Maximum of two columns allowed in definition table.");
            }

            Cell cell = new CellBuilder()
                    .defaults()
                    .insert(0, characters)
                    .build();
            mutatedCells.put(rowIndex, columnIndex, cell);
        }

        CompositionBuilder compositionBuilder = new CompositionBuilder().prototypeOf(compositionStream.getValue())
                .setCells(tableType, new TableBackedImmutableArrayTable<>(mutatedCells, EmptyCell::new));

        compositionStream.onNext(compositionBuilder.build("Bulk Set"));
    }

    /**
     * Because of the self collapsing nature of the grid, it is only possible to add characters to
     * one index value larger that the current size for both rows and columns.
     *
     * @param tableType   the section of the document we want to act upon
     * @param rowIndex    must be no more than one larger than current row size
     * @param columnIndex must be no more than one larger than current column size
     * @param characters  non null and greater than 0 in length
     */
    public void addCharacters(TableType tableType, int rowIndex, int columnIndex, String characters) {
        checkNotNull(tableType);
        checkNotNull(characters);
        checkArgument(!characters.isEmpty(), "Empty characters");
        if (tableType == DEFINITION_TABLE) {
            checkArgument(columnIndex < 2, "Maximum of two columns allowed in definition table.");
        }

        ImmutableArrayTable<Cell> cells = getCells(tableType);
        checkPositionIndex(rowIndex, cells.getRowSize(), "rowIndex");
        checkPositionIndex(columnIndex, cells.getColumnSize(), "columnIndex");

        int cellInsertIndex = 0;
        if (rowIndex < cells.getRowSize() &&
                columnIndex < cells.getColumnSize()) {
            Cell cell = cells.get(rowIndex, columnIndex);
            cellInsertIndex = (cell == null) ? 0 : cell.size();
        }

        insertCharacters(tableType, rowIndex, columnIndex, cellInsertIndex, characters);
    }

    /**
     * Because of the self collapsing nature of the grid, it is only possible to add characters to
     * one index value larger that the current size for both rows and columns.
     *
     * @param tableType   the section of the document we want to act upon
     * @param rowIndex    must be no more than one larger than current row size
     * @param columnIndex must be no more than one larger than current column size
     * @param cellInsertIndex  ??
     * @param characters  non null and greater than 0 in length
     */
    public void insertCharacters(TableType tableType, int rowIndex, int columnIndex, int cellInsertIndex, String characters) {
        checkNotNull(tableType);
        checkNotNull(characters);
        checkArgument(characters.length() > 0);
        checkArgument(cellInsertIndex >= 0);
        if (tableType == DEFINITION_TABLE) {
            checkArgument(columnIndex < 2, "Maximum of two columns allowed in definition table.");
        }

        ImmutableArrayTable<Cell> originalCells = getCells(tableType);
        checkPositionIndex(rowIndex, originalCells.getRowSize(), "rowIndex");
        checkPositionIndex(columnIndex, originalCells.getColumnSize(), "columnIndex");

        Table<Integer, Integer, Cell> mutatedCells = HashBasedTable.create(originalCells.getBackingTable());
        Cell currentCell = mutatedCells.get(rowIndex, columnIndex);

        if (currentCell == null) {
            // insert a new cell.
            Cell cell = new CellBuilder()
                    .defaults()
                    .insert(cellInsertIndex, characters)
                    .build();
            mutatedCells.put(rowIndex, columnIndex, cell);
        } else {
            Cell cell = new CellBuilder()
                    .prototypeOf(currentCell)
                    .insert(cellInsertIndex, characters)
                    .build();
            mutatedCells.put(rowIndex, columnIndex, cell);
        }

        CompositionBuilder compositionBuilder = new CompositionBuilder().prototypeOf(compositionStream.getValue())
                .setCells(tableType, new TableBackedImmutableArrayTable<>(mutatedCells, EmptyCell::new));

        compositionStream.onNext(compositionBuilder.build("Typing"));
    }

    public void removeCharacters(TableType tableType, int rowIndex, int columnIndex, int cellIndex, int count) {
        checkNotNull(tableType);
        checkArgument(cellIndex >= 0);

        ImmutableArrayTable<Cell> originalCells = getCells(tableType);
        checkPositionIndex(rowIndex, originalCells.getRowSize(), "rowIndex");
        checkPositionIndex(columnIndex, originalCells.getColumnSize(), "columnIndex");

        Table<Integer, Integer, Cell> mutatedCells = HashBasedTable.create(originalCells.getBackingTable());
        removeCharactersInternal(rowIndex, columnIndex, cellIndex, count, mutatedCells, originalCells);

        CompositionBuilder compositionBuilder = new CompositionBuilder().prototypeOf(compositionStream.getValue())
                .setCells(tableType, new TableBackedImmutableArrayTable<>(mutatedCells, EmptyCell::new));

        compositionStream.onNext(compositionBuilder.build("Delete"));
    }

    private void removeCharactersInternal(int rowIndex, int columnIndex, int cellIndex, int count, Table<Integer, Integer, Cell> mutatedCells, ImmutableArrayTable<Cell> originalCells) {
        Cell currentCell = mutatedCells.get(rowIndex, columnIndex);

        checkNotNull(currentCell);

        Cell cell = new CellBuilder()
                .prototypeOf(currentCell)
                .delete(cellIndex, count)
                .build();

        if (cell.size() == 0) {
            mutatedCells.remove(rowIndex, columnIndex);
            removeRowIfEmpty(rowIndex, mutatedCells, originalCells);
            removeColumnIfEmpty(columnIndex, mutatedCells, originalCells);
        } else {
            mutatedCells.put(rowIndex, columnIndex, cell);
        }
    }

    private ImmutableArrayTable<Cell> getCells(TableType tableType) {
        switch (tableType) {

            case COMPOSITION_TABLE:
                return compositionStream.getValue().allCompositionCells();
            case DEFINITION_TABLE:
                return compositionStream.getValue().allDefinitionCells();
            default:
                throw new UnsupportedOperationException();
        }
    }

    private void removeColumnIfEmpty(int columnIndexForRemoval, Table<Integer, Integer, Cell> cells, ImmutableArrayTable<Cell> originalCells) {
        Map<Integer, Cell> columnItems = cells.column(columnIndexForRemoval);

        if (!columnItems.isEmpty()) {
            return;
        }

        int rowCount = originalCells.getRowSize();
        int columnCount = originalCells.getColumnSize();
        // We allow the column loop to go '1' past end to ensure the final column is shifted.
        for (int columnIndex = columnIndexForRemoval; columnIndex < columnCount + 1; columnIndex++) {
            for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
                Cell cell = cells.get(rowIndex, columnIndex);
                if (cell != null) {
                    cells.put(rowIndex, columnIndex - 1, cell);
                    cells.remove(rowIndex, columnIndex);
                }
            }
        }
    }

    private void removeRowIfEmpty(int rowIndexForRemoval, Table<Integer, Integer, Cell> mutatedCells, ImmutableArrayTable<Cell> originalCells) {
        Map<Integer, Cell> rowItems = mutatedCells.row(rowIndexForRemoval);

        if (!rowItems.isEmpty()) {
            return;
        }

        int rowCount = originalCells.getRowSize();
        int columnCount = originalCells.getColumnSize();
        // We allow the row loop to go '1' past end to ensure the final row is shifted.
        for (int rowIndex = rowIndexForRemoval; rowIndex < rowCount + 1; rowIndex++) {
            for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
                Cell cell = mutatedCells.get(rowIndex, columnIndex);
                if (cell != null) {
                    mutatedCells.put(rowIndex - 1, columnIndex, cell);
                    mutatedCells.remove(rowIndex, columnIndex);
                }
            }
        }
    }

    @Override
    public String toString() {
        return "MutableComposition{" +
                "composition=" + compositionStream.getValue().getTitle() +
                '}';
    }

}