package org.ringingmaster.engine.touch;


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
import org.ringingmaster.engine.arraytable.ImmutableArrayTable;
import org.ringingmaster.engine.arraytable.TableBackedImmutableArrayTable;
import org.ringingmaster.engine.method.Bell;
import org.ringingmaster.engine.method.MethodRow;
import org.ringingmaster.engine.method.Stroke;
import org.ringingmaster.engine.method.impl.MethodBuilder;
import org.ringingmaster.engine.notation.Notation;
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.notation.impl.NotationBuilder;
import org.ringingmaster.engine.touch.cell.Cell;
import org.ringingmaster.engine.touch.cell.CellBuilder;
import org.ringingmaster.engine.touch.cell.EmptyCell;
import org.ringingmaster.engine.touch.checkingtype.CheckingType;
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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkPositionIndex;
import static com.google.common.base.Preconditions.checkState;
import static org.ringingmaster.engine.touch.TableType.DEFINITION_TABLE;
import static org.ringingmaster.engine.touch.tableaccess.DefinitionTableAccess.DEFINITION_COLUMN;
import static org.ringingmaster.engine.touch.tableaccess.DefinitionTableAccess.SHORTHAND_COLUMN;


/**
 * TODO Comments
 *
 * @author Lake
 */
public class ObservableTouch {

    public static final int START_AT_ROW_MAX                                =     10_000;
    public static final int TERMINATION_MAX_ROWS_INITIAL_VALUE              =     10_000;
    public static final int TERMINATION_MAX_ROWS_MAX                        = 10_000_000;
    public static final int TERMINATION_MAX_LEADS_MAX                       =    100_000;
    public static final int TERMINATION_MAX_PARTS_MAX                       =     10_000;
    public static final int TERMINATION_MAX_CIRCULARITY_INITIAL_VALUE       =          2;
    public static final int TERMINATION_MAX_CIRCULARITY_MAX                 =     10_000;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private Touch currentTouch = new TouchBuilder().defaults().build();
    private final BehaviorSubject<Touch> subject = BehaviorSubject.create();
    private final SmartCompare s = new SmartCompare("", "> ")
            .comparePaths("numberOfBells")
            .comparePaths("startChange");

    public Observable<Touch> observable() {
        return subject;
    }

    public Touch get() {
        return currentTouch;
    }

    private void setCurrentTouch(Touch newTouch) {
        log.info("[{}] Touch diff [{}]", currentTouch.getTitle(), s.stringDifferences(currentTouch, newTouch));
        subject.onNext(newTouch);
        currentTouch = newTouch;
    }

    public void setTitle(String title) {
        checkNotNull(title);

        if (Objects.equals(currentTouch.getTitle(), title)) {
            return;
        }

        TouchBuilder touchBuilder = new TouchBuilder().prototypeOf(currentTouch)
                .setTitle(title);

        setCurrentTouch(touchBuilder.build());
    }

    public void setAuthor(String author) {
        checkNotNull(author);

        if (Objects.equals(currentTouch.getAuthor(), author)) {
            return;
        }

        log.debug("[{}] Set author [{}]", currentTouch.getTitle(), author);

        TouchBuilder touchBuilder = new TouchBuilder().prototypeOf(currentTouch)
                .setAuthor(author);

        setCurrentTouch(touchBuilder.build());
    }

    public void setNumberOfBells(NumberOfBells numberOfBells) {
        checkNotNull(numberOfBells);

        if (Objects.equals(currentTouch.getNumberOfBells(), numberOfBells)) {
            return;
        }

        TouchBuilder touchBuilder = new TouchBuilder()
                .prototypeOf(currentTouch)
                .setNumberOfBells(numberOfBells);

        if (currentTouch.getCallFromBell().getZeroBasedBell() > numberOfBells.getTenor().getZeroBasedBell()) {
            touchBuilder.setCallFromBell(numberOfBells.getTenor());
        }

        final MethodRow existingStartChange = currentTouch.getStartChange();
        final MethodRow newStartChange = MethodBuilder.transformToNewNumberOfBells(existingStartChange, numberOfBells);
        touchBuilder.setStartChange(newStartChange);

        if (currentTouch.getTerminationChange().isPresent()) {
            final MethodRow existingTerminationRow = currentTouch.getTerminationChange().get();
            final MethodRow newTerminationRow = MethodBuilder.transformToNewNumberOfBells(existingTerminationRow, numberOfBells);
            touchBuilder.setTerminationChange(Optional.of(newTerminationRow));
        }

        if (!currentTouch.isSpliced() &&
                currentTouch.getNonSplicedActiveNotation().isPresent() &&
                currentTouch.getNonSplicedActiveNotation().get().getNumberOfWorkingBells().toInt() > numberOfBells.toInt()) {
            Optional<NotationBody> nextBestNonSplicedActiveNotation = findNextBestNonSplicedActiveNotation(currentTouch.getNonSplicedActiveNotation().get());
            touchBuilder.setNonSplicedActiveNotation(nextBestNonSplicedActiveNotation);
        }

        if (currentTouch.getStartNotation().isPresent()) {
            final String originalNotation = currentTouch.getStartNotation().get().getNotationDisplayString(false);
            NotationBody builtNotation = NotationBuilder.getInstance()
                    .setNumberOfWorkingBells(numberOfBells)
                    .setUnfoldedNotationShorthand(originalNotation)
                    .build();
            if (builtNotation.getRowCount() == 0) {
                touchBuilder.setStartNotation(Optional.empty());
            } else {
                touchBuilder.setStartNotation(Optional.of(builtNotation));
            }
        }

        setCurrentTouch(touchBuilder.build());
    }

    //TODO need a checkNumberOfBells to drive UI

    public void setTouchCheckingType(CheckingType checkingType) {
        checkNotNull(checkingType);

        if (Objects.equals(currentTouch.getCheckingType(), checkingType)) {
            return;
        }

        TouchBuilder touchBuilder = new TouchBuilder().prototypeOf(currentTouch)
                .setTouchCheckingType(checkingType);

        setCurrentTouch(touchBuilder.build());
    }

    public void setCallFromBell(Bell callFromBell) {
        checkNotNull(callFromBell);
        checkArgument(callFromBell.getZeroBasedBell() < currentTouch.getNumberOfBells().toInt());

        if (Objects.equals(currentTouch.getCallFromBell(), callFromBell)) {
            return;
        }

        TouchBuilder touchBuilder = new TouchBuilder().prototypeOf(currentTouch)
                .setCallFromBell(callFromBell);

        setCurrentTouch(touchBuilder.build());
    }

    private Optional<NotationBody> findNextBestNonSplicedActiveNotation(NotationBody previousNotation) {
        final List<NotationBody> validNotations = Lists.newArrayList(currentTouch.getValidNotations());
        validNotations.remove(previousNotation);

        Comparator<NumberOfBells> byDistanceFromPassedNumberOfBells = (o1, o2) -> ComparisonChain.start()
                .compare(Math.abs(previousNotation.getNumberOfWorkingBells().toInt() - o1.toInt()),
                        Math.abs(previousNotation.getNumberOfWorkingBells().toInt() - o2.toInt()))
                .compare(o2.toInt(), o1.toInt()) // always take higher number of bells where distance is equal
                .result();

        // from the validNotations, find all number of bells in use, sorted by distance from passed number of bells.
        Optional<NumberOfBells> bestNumberOfBells = validNotations.stream()
                .map(Notation::getNumberOfWorkingBells)
                .sorted(byDistanceFromPassedNumberOfBells)
                .findFirst();

        if (!bestNumberOfBells.isPresent()) {
            return Optional.empty();
        }

        // Try notations that are lexicographically the same or higher
        Optional<NotationBody> lexicographicallyHigher = validNotations.stream()
                .filter(notation -> notation.getNumberOfWorkingBells() == bestNumberOfBells.get())
                .filter(notation -> notation.getName().compareTo(previousNotation.getName()) >= 0)
                .sorted(Notation.BY_NAME)
                .findFirst();
        if (lexicographicallyHigher.isPresent()) {
            return lexicographicallyHigher;
        }

        // Try notations that are lexicographically lower
        Optional<NotationBody> lexicographicallyLower = validNotations.stream()
                .filter(notation -> notation.getNumberOfWorkingBells() == bestNumberOfBells.get())
                .filter(notation -> notation.getName().compareTo(previousNotation.getName()) < 0)
                .sorted(Notation.BY_NAME.reversed())
                .findFirst();
        if (lexicographicallyLower.isPresent()) {
            return lexicographicallyLower;
        }

        return Optional.empty();
    }

    public void addNotation(NotationBody notationToAdd) {
        checkNotNull(notationToAdd, "notation must not be null");

        List<String> messages = checkAddNotation(notationToAdd);

        if (messages.size() > 0) {
            String message = messages.stream().collect(Collectors.joining(System.lineSeparator()));
            throw new IllegalArgumentException("Can't add notation [" + notationToAdd + "]: " + System.lineSeparator() + message);
        }

        TouchBuilder touchBuilder = new TouchBuilder().prototypeOf(currentTouch);

        PSet<NotationBody> withAddedNotation = currentTouch.getAllNotations().plus(notationToAdd);
        touchBuilder.setAllNotations(withAddedNotation);

//TODO what if the number of bells is wrong?
        if (!currentTouch.isSpliced() && !currentTouch.getNonSplicedActiveNotation().isPresent()) {
            touchBuilder.setNonSplicedActiveNotation(Optional.of(notationToAdd));
        }

        setCurrentTouch(touchBuilder.build());
    }

    public List<String> checkAddNotation(NotationBody notationToAdd) {
        return checkPotentialNewNotation(notationToAdd, Collections.emptySet());
    }

    private List<String> checkPotentialNewNotation(NotationBody notationToCheck, Set<NotationBody> notationsToExclude) {
        checkNotNull(notationToCheck);
        checkNotNull(notationsToExclude);

        List<String> messages = new ArrayList<>();
        PSet<NotationBody> allNotationsWithExclusions = currentTouch.getAllNotations().minusAll(notationsToExclude);

        messages.addAll(allNotationsWithExclusions.stream()
                .filter(existingNotation -> (existingNotation.getNumberOfWorkingBells() == notationToCheck.getNumberOfWorkingBells()) &&
                        (com.google.common.base.Objects.equal(existingNotation.getName(), notationToCheck.getName())))
                .map(existingNotation -> "An existing method with notation '" + existingNotation.getNotationDisplayString(true) + "' has the same Name and Number Of Bells.")
                .collect(Collectors.toList()));

        messages.addAll(allNotationsWithExclusions.stream()
                .filter(existingNotation -> (!Strings.isNullOrEmpty(notationToCheck.getSpliceIdentifier()) &&
                        com.google.common.base.Objects.equal(existingNotation.getSpliceIdentifier(), notationToCheck.getSpliceIdentifier())))
                .map(existingNotation -> "An existing method '" + existingNotation.getNameIncludingNumberOfBells() + "' has the same Splice Identifier.")
                .collect(Collectors.toList()));

        messages.addAll(allNotationsWithExclusions.stream()
                .filter(existingNotation -> (existingNotation.getNumberOfWorkingBells() == notationToCheck.getNumberOfWorkingBells()) &&
                        com.google.common.base.Objects.equal(existingNotation.getNotationDisplayString(true), notationToCheck.getNotationDisplayString(true)))
                .map(existingNotation -> "An existing method '" + existingNotation.getNameIncludingNumberOfBells() + "' has the same Notation '" + notationToCheck.getNotationDisplayString(false) + "'.")
                .collect(Collectors.toList()));
        return messages;
    }

    public void removeNotation(NotationBody notationForRemoval) {
        checkNotNull(notationForRemoval, "notationForRemoval must not be null");

        PSet<NotationBody> allNotations = currentTouch.getAllNotations();
        checkState(allNotations.contains(notationForRemoval));

        PSet<NotationBody> withRemovedNotation = allNotations.minus(notationForRemoval);
        TouchBuilder touchBuilder = new TouchBuilder().prototypeOf(currentTouch)
                .setAllNotations(withRemovedNotation);

        // Sort out the next notation if it is the active notation
        if (currentTouch.getNonSplicedActiveNotation().isPresent() &&
                notationForRemoval.equals(currentTouch.getNonSplicedActiveNotation().get())) {
            Optional<NotationBody> nextBestNonSplicedActiveNotation = findNextBestNonSplicedActiveNotation(notationForRemoval);
            touchBuilder.setNonSplicedActiveNotation(nextBestNonSplicedActiveNotation);
        }

        setCurrentTouch(touchBuilder.build());
    }

    public void exchangeNotation(NotationBody originalNotation, NotationBody replacementNotation) {
        checkNotNull(originalNotation, "originalNotation must not be null");
        checkNotNull(replacementNotation, "replacementNotation must not be null");
        checkArgument(originalNotation != replacementNotation);


        PSet<NotationBody> allNotations = currentTouch.getAllNotations();
        checkState(allNotations.contains(originalNotation));

        List<String> messages = checkUpdateNotation(originalNotation, replacementNotation);

        if (messages.size() > 0) {
            String message = messages.stream().collect(Collectors.joining(System.lineSeparator()));
            throw new IllegalArgumentException("Can't update notation [" + replacementNotation + "]: " + System.lineSeparator() + message);
        }

        TouchBuilder touchBuilder = new TouchBuilder().prototypeOf(currentTouch);

        allNotations = allNotations.minus(originalNotation)
                .plus(replacementNotation);
        touchBuilder.setAllNotations(allNotations);

        if (currentTouch.getNonSplicedActiveNotation().isPresent() &&
                currentTouch.getNonSplicedActiveNotation().get() == originalNotation) {
            if (replacementNotation.getNumberOfWorkingBells().toInt() > currentTouch.getNumberOfBells().toInt()) {
                Optional<NotationBody> nextBestNonSplicedActiveNotation = findNextBestNonSplicedActiveNotation(replacementNotation);
                touchBuilder.setNonSplicedActiveNotation(nextBestNonSplicedActiveNotation);
            } else {
                touchBuilder.setNonSplicedActiveNotation(Optional.of(replacementNotation));
            }
        }
        setCurrentTouch(touchBuilder.build());
    }

    //TODO rename exchangeNotation
    public List<String> checkUpdateNotation(NotationBody originalNotation, NotationBody replacementNotation) {
        checkNotNull(originalNotation);
        checkNotNull(replacementNotation);

        return checkPotentialNewNotation(replacementNotation, Sets.<NotationBody>newHashSet(originalNotation));
    }

    public void setNonSplicedActiveNotation(NotationBody nonSplicedActiveNotation) {
        checkNotNull(nonSplicedActiveNotation);
        checkState(currentTouch.getAllNotations().contains(nonSplicedActiveNotation), "Can't set NonSplicedActiveNotation to notation not part of touch.");

        if (currentTouch.getNonSplicedActiveNotation().isPresent() &&
                Objects.equals(currentTouch.getNonSplicedActiveNotation().get(), nonSplicedActiveNotation)) {
            return;
        }

        TouchBuilder touchBuilder = new TouchBuilder().prototypeOf(currentTouch)
                .setNonSplicedActiveNotation(Optional.of(nonSplicedActiveNotation));

        setCurrentTouch(touchBuilder.build());
    }

    public void setSpliced(boolean spliced) {

        if (Objects.equals(currentTouch.isSpliced(), spliced)) {
            return;
        }

        TouchBuilder touchBuilder = new TouchBuilder().prototypeOf(currentTouch);


        if (spliced) {
            touchBuilder.setNonSplicedActiveNotation(Optional.empty());
        }
        else {
            final Set<NotationBody> validNotations = currentTouch.getValidNotations();

            if (validNotations.size() > 0) {
                Optional<NotationBody> firstByName = validNotations.stream()
                        .sorted(NotationBody.BY_NAME)
                        .findFirst();
                touchBuilder.setNonSplicedActiveNotation(firstByName);
            }
        }

        setCurrentTouch(touchBuilder.build());
    }

    public void setPlainLeadToken(String plainLeadToken) {
        checkNotNull(plainLeadToken);

        if (Objects.equals(currentTouch.getPlainLeadToken(), plainLeadToken)) {
            return;
        }

        TouchBuilder touchBuilder = new TouchBuilder().prototypeOf(currentTouch)
                .setPlainLeadToken(plainLeadToken);

        setCurrentTouch(touchBuilder.build());
    }

    public void addDefinition(String shorthand, String characters) {
        checkNotNull(shorthand, "shorthand must not be null");
        checkState(shorthand.length() > 0, "shorthand must contain some characters");

        // Check duplicate name
        shorthand = shorthand.trim();
        if (currentTouch.findDefinitionByShorthand(shorthand).isPresent()) {
            throw new IllegalArgumentException("Can't add definition [" + shorthand + "] as it has a duplicate shorthand to existing definition [" + currentTouch.findDefinitionByShorthand(shorthand) + "]");
        }

        Table<Integer, Integer, Cell> cells = HashBasedTable.create(currentTouch.allDefinitionCells().getBackingTable());
        int insertionRow = currentTouch.allDefinitionCells().getRowSize();

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

        TouchBuilder touchBuilder = new TouchBuilder().prototypeOf(currentTouch)
                .setCells(DEFINITION_TABLE, new TableBackedImmutableArrayTable<>(cells, EmptyCell::new));

        setCurrentTouch(touchBuilder.build());
    }

    public void removeDefinition(String shorthand) {
        checkNotNull(shorthand, "shorthand must not be null");
        checkState(shorthand.length() > 0, "shorthand must contain some characters");

        final ImmutableArrayTable<Cell> definitionCells = currentTouch.allDefinitionCells();
        Table<Integer, Integer, Cell> mutatedCells = HashBasedTable.create(definitionCells.getBackingTable());

        for (int rowIndex=0;rowIndex<definitionCells.getRowSize();rowIndex++) {
            final Cell shorthandCell = mutatedCells.get(rowIndex, SHORTHAND_COLUMN);
            final Cell definitionCell = mutatedCells.get(rowIndex, DEFINITION_COLUMN);
            if (shorthandCell != null) {
                if (Objects.equals(shorthand, shorthandCell.getCharacters())){
                    removeCharactersInternal(rowIndex, SHORTHAND_COLUMN, 0,shorthandCell.getElementSize(), mutatedCells, definitionCells);
                    if (definitionCell != null) {
                        removeCharactersInternal(rowIndex, DEFINITION_COLUMN, 0, definitionCell.getElementSize(), mutatedCells, definitionCells);
                    }
                    removeRowIfEmpty(rowIndex, mutatedCells, definitionCells);
                    break;
                }
            }
        }

        TouchBuilder touchBuilder = new TouchBuilder().prototypeOf(currentTouch)
            .setCells(DEFINITION_TABLE, new TableBackedImmutableArrayTable<>(mutatedCells, EmptyCell::new));

        setCurrentTouch(touchBuilder.build());
    }

    public void setStartChange(MethodRow startChange) {
        checkNotNull(startChange);
        checkArgument(startChange.getNumberOfBells() == currentTouch.getNumberOfBells());

        if (Objects.equals(currentTouch.getStartChange(), startChange)) {
            return;
        }

        TouchBuilder touchBuilder = new TouchBuilder().prototypeOf(currentTouch)
                .setStartChange(startChange);

        setCurrentTouch(touchBuilder.build());
    }

    public void setStartAtRow(int startAtRow) {
        checkArgument(startAtRow >= 0, "Start at row must be 0 or greater.");
        checkArgument(startAtRow <= START_AT_ROW_MAX, "Start at row must be less than or equal to %s", START_AT_ROW_MAX);


        if (Objects.equals(currentTouch.getStartAtRow(), startAtRow)) {
            return;
        }

        TouchBuilder touchBuilder = new TouchBuilder().prototypeOf(currentTouch)
                .setStartAtRow(startAtRow);

        setCurrentTouch(touchBuilder.build());
    }

    public void setStartStroke(Stroke startStroke) {
        checkNotNull(startStroke);

        if (Objects.equals(currentTouch.getStartStroke(), startStroke)) {
            return;
        }

        TouchBuilder touchBuilder = new TouchBuilder().prototypeOf(currentTouch)
                .setStartStroke(startStroke);

        setCurrentTouch(touchBuilder.build());
    }

    public void setStartNotation(NotationBody startNotation) {
        checkNotNull(startNotation);
        checkState(startNotation.getNumberOfWorkingBells() == currentTouch.getNumberOfBells(), "Start Notation number of bells must match touch number of bells");

        if (currentTouch.getStartNotation().isPresent() &&
                currentTouch.getStartNotation().get().getNotationDisplayString(false).equals(startNotation.getNotationDisplayString(false))) {
            return;
        }

        TouchBuilder touchBuilder = new TouchBuilder().prototypeOf(currentTouch)
                .setStartNotation(Optional.of(startNotation));

        setCurrentTouch(touchBuilder.build());

    }

    public void removeStartNotation() {
        if (!currentTouch.getStartNotation().isPresent()) {
            return;
        }

        TouchBuilder touchBuilder = new TouchBuilder().prototypeOf(currentTouch)
                .setStartNotation(Optional.empty());

        setCurrentTouch(touchBuilder.build());
    }

    public void setTerminationMaxRows(int terminationMaxRows) {
        checkArgument(terminationMaxRows > 0, "Termination max rows must be greater than 0");
        checkArgument(terminationMaxRows <= TERMINATION_MAX_ROWS_MAX, "Termination max rows must be less than or equal to %s", TERMINATION_MAX_ROWS_MAX);

        if (Objects.equals(currentTouch.getTerminationMaxRows(), terminationMaxRows)) {
            return;
        }

        TouchBuilder touchBuilder = new TouchBuilder().prototypeOf(currentTouch)
                .setTerminationMaxRows(terminationMaxRows);

        setCurrentTouch(touchBuilder.build());
    }

    public void setTerminationMaxLeads(int terminationMaxLeads) {
        checkArgument(terminationMaxLeads > 0, "Termination max leads must be greater than 0");
        checkArgument(terminationMaxLeads <= TERMINATION_MAX_LEADS_MAX, "Termination max leads must be less than or equal to %s", TERMINATION_MAX_LEADS_MAX);

        if (Objects.equals(currentTouch.getTerminationMaxLeads(), terminationMaxLeads)) {
            return;
        }

        TouchBuilder touchBuilder = new TouchBuilder().prototypeOf(currentTouch)
                .setTerminationMaxLeads(Optional.of(terminationMaxLeads));

        setCurrentTouch(touchBuilder.build());
    }

    public void removeTerminationMaxLeads() {
        if (!currentTouch.getTerminationMaxLeads().isPresent()) {
            return;
        }

        TouchBuilder touchBuilder = new TouchBuilder().prototypeOf(currentTouch)
                .setTerminationMaxLeads(Optional.empty());

        setCurrentTouch(touchBuilder.build());
    }

    public void setTerminationMaxParts(int terminationMaxParts) {
        checkArgument(terminationMaxParts > 0, "Termination max parts must be greater than 0");
        checkArgument(terminationMaxParts <= TERMINATION_MAX_PARTS_MAX, "Termination max parts must be less than or equal to %s", TERMINATION_MAX_PARTS_MAX);

        if (Objects.equals(currentTouch.getTerminationMaxParts(), terminationMaxParts)) {
            return;
        }

        TouchBuilder touchBuilder = new TouchBuilder().prototypeOf(currentTouch)
                .setTerminationMaxParts(Optional.of(terminationMaxParts));

        setCurrentTouch(touchBuilder.build());
    }

    public void removeTerminationMaxParts() {
        if (!currentTouch.getTerminationMaxParts().isPresent()) {
            return;
        }

        TouchBuilder touchBuilder = new TouchBuilder().prototypeOf(currentTouch)
                .setTerminationMaxParts(Optional.empty());

        setCurrentTouch(touchBuilder.build());
    }

    public void setTerminationMaxCircularTouch(int terminationCircularTouch) {
        checkArgument(terminationCircularTouch > 0, "Termination circular touch must be greater than 0");
        checkArgument(terminationCircularTouch <= TERMINATION_MAX_CIRCULARITY_MAX, "Termination circular touch must be less than or equal to %s", TERMINATION_MAX_CIRCULARITY_MAX);

        if (Objects.equals(currentTouch.getTerminationMaxCircularity(), terminationCircularTouch)) {
            return;
        }

        TouchBuilder touchBuilder = new TouchBuilder().prototypeOf(currentTouch)
                .setTerminationMaxCircularity(terminationCircularTouch);

        setCurrentTouch(touchBuilder.build());
    }

    public void setTerminationChange(MethodRow terminationChange) {
        checkNotNull(terminationChange, "terminationChange cant be null");
        checkArgument(terminationChange.getNumberOfBells().equals(currentTouch.getNumberOfBells()));

        if (currentTouch.getTerminationChange().isPresent() &&
                currentTouch.getTerminationChange().get().equals(terminationChange)) {
            return;
        }

        TouchBuilder touchBuilder = new TouchBuilder().prototypeOf(currentTouch)
                .setTerminationChange(Optional.of(terminationChange));

        setCurrentTouch(touchBuilder.build());
    }

    public void removeTerminationChange() {
        if (!currentTouch.getTerminationChange().isPresent()) {
            return;
        }

        TouchBuilder touchBuilder = new TouchBuilder().prototypeOf(currentTouch)
                .setTerminationChange(Optional.empty());

        setCurrentTouch(touchBuilder.build());
    }

    /**
     * Because of the self collapsing nature of the grid, it is only possible to add characters to
     * one index value larger that the current size for both rows and columns.
     *
     * @param tableType the section of the document we want to act upon
     * @param rowIndex must be no more than one larger than current row size
     * @param columnIndex must be no more than one larger than current column size
     * @param characters non null and greater than 0 in length
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
        if (rowIndex    < cells.getRowSize() &&
            columnIndex < cells.getColumnSize()) {
            Cell cell = cells.get(rowIndex, columnIndex);
            cellInsertIndex = (cell == null) ? 0 : cell.getElementSize();
        }

        insertCharacters(tableType, rowIndex, columnIndex, cellInsertIndex, characters);
    }

    public void insertCharacters(TableType tableType, int rowIndex, int columnIndex, int cellInsertIndex, String characters) {
        checkNotNull(tableType);
        checkNotNull(characters);
        checkArgument(characters.length() > 0);
        checkArgument(cellInsertIndex >= 0 );
        if (tableType == DEFINITION_TABLE) {
            checkArgument(columnIndex < 2, "Maximum of two columns allowed in definition table.");
        }

        ImmutableArrayTable<Cell> cells = getCells(tableType);
        checkPositionIndex(rowIndex, cells.getRowSize(), "rowIndex");
        checkPositionIndex(columnIndex, cells.getColumnSize(), "columnIndex");

        Table<Integer, Integer, Cell> mutatedCells = HashBasedTable.create(cells.getBackingTable());
        Cell currentCell = mutatedCells.get(rowIndex, columnIndex);

        if (currentCell == null) {
            // insert a new cell.
            Cell cell = new CellBuilder()
                    .defaults()
                    .insert(cellInsertIndex, characters)
                    .build();
            mutatedCells.put(rowIndex, columnIndex, cell);
        }
        else {
            Cell cell = new CellBuilder()
                    .prototypeOf(currentCell)
                    .insert(cellInsertIndex, characters)
                    .build();
            mutatedCells.put(rowIndex, columnIndex, cell);
        }

        TouchBuilder touchBuilder = new TouchBuilder().prototypeOf(currentTouch)
                .setCells(tableType, new TableBackedImmutableArrayTable<>(mutatedCells, EmptyCell::new));

        setCurrentTouch(touchBuilder.build());

    }

    public void removeCharacters(TableType tableType, int rowIndex, int columnIndex, int cellIndex, int count) {
        checkNotNull(tableType);
        checkArgument(cellIndex >= 0 );

        ImmutableArrayTable<Cell> originalCells = getCells(tableType);
        checkPositionIndex(rowIndex, originalCells.getRowSize(), "rowIndex");
        checkPositionIndex(columnIndex, originalCells.getColumnSize(), "columnIndex");

        Table<Integer, Integer, Cell> mutatedCells = HashBasedTable.create(originalCells.getBackingTable());
        removeCharactersInternal(rowIndex, columnIndex, cellIndex, count, mutatedCells, originalCells);

        TouchBuilder touchBuilder = new TouchBuilder().prototypeOf(currentTouch)
                .setCells(tableType, new TableBackedImmutableArrayTable<Cell>(mutatedCells, EmptyCell::new));

        setCurrentTouch(touchBuilder.build());
    }

    private void removeCharactersInternal(int rowIndex, int columnIndex, int cellIndex, int count, Table<Integer, Integer, Cell> mutatedCells, ImmutableArrayTable<Cell> originalCells) {
        Cell currentCell = mutatedCells.get(rowIndex, columnIndex);

        checkNotNull(currentCell);

        Cell cell = new CellBuilder()
                .prototypeOf(currentCell)
                .delete(cellIndex, count)
                .build();

        if (cell.getElementSize() == 0) {
            mutatedCells.remove(rowIndex, columnIndex);
            removeRowIfEmpty(rowIndex, mutatedCells, originalCells);
            removeColumnIfEmpty(columnIndex, mutatedCells, originalCells);
        }
        else {
            mutatedCells.put(rowIndex, columnIndex, cell);
        }
    }

    private ImmutableArrayTable<Cell> getCells(TableType tableType) {
        switch (tableType){

            case TOUCH_TABLE:
                return currentTouch.allTouchCells();
            case DEFINITION_TABLE:
                return currentTouch.allDefinitionCells();
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
        // We allow the column loop to go '1' past end to ensure the final column is removed.
        for (int columnIndex=columnIndexForRemoval;columnIndex<columnCount+1;columnIndex++) {
            for (int rowIndex=0;rowIndex<rowCount;rowIndex++) {
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
        // We allow the row loop to go '1' past end to ensure the final row is removed.
        for (int rowIndex=rowIndexForRemoval;rowIndex<rowCount+1;rowIndex++) {
            for (int columnIndex=0;columnIndex<columnCount;columnIndex++) {
                Cell cell = mutatedCells.get(rowIndex, columnIndex);
                if (cell != null) {
                    mutatedCells.put(rowIndex-1,columnIndex, cell);
                    mutatedCells.remove(rowIndex, columnIndex);
                }
            }
        }
    }
}