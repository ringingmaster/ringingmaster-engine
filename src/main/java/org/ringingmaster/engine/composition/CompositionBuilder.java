package org.ringingmaster.engine.composition;

import org.pcollections.HashTreePSet;
import org.pcollections.PSet;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.arraytable.ImmutableArrayTable;
import org.ringingmaster.engine.arraytable.TableBackedImmutableArrayTable;
import org.ringingmaster.engine.method.Bell;
import org.ringingmaster.engine.method.Row;
import org.ringingmaster.engine.method.Stroke;
import org.ringingmaster.engine.method.MethodBuilder;
import org.ringingmaster.engine.notation.Notation;
import org.ringingmaster.engine.composition.cell.Cell;
import org.ringingmaster.engine.composition.cell.EmptyCell;
import org.ringingmaster.engine.composition.compositiontype.CompositionType;

import java.util.Optional;

import static org.ringingmaster.engine.composition.MutableComposition.TERMINATION_MAX_CIRCULARITY_INITIAL_VALUE;
import static org.ringingmaster.engine.composition.MutableComposition.TERMINATION_MAX_ROWS_INITIAL_VALUE;
import static org.ringingmaster.engine.composition.TableType.DEFINITION_TABLE;
import static org.ringingmaster.engine.composition.TableType.COMPOSITION_TABLE;


/**
 * Used exclusively by class MutableComposition.
 *
 * @author Steve Lake
 */
class CompositionBuilder {

    public static final String DEFAULT_TITLE = "UNNAMED";

    private Composition prototype;

    // Holds the name of the change that caused this change to be built. Used for Undo/Redo
    private Optional<String> actionName = Optional.empty();


    private Optional<String> title = Optional.empty();
    private Optional<String> author = Optional.empty();

    private Optional<NumberOfBells> numberOfBells = Optional.empty();
    private Optional<CompositionType> compositionType = Optional.empty();

    private Optional<Bell> callFromBell = Optional.empty();
    private Optional<PSet<Notation>> allNotations = Optional.empty();
    private Optional<Optional<Notation>> nonSplicedActiveNotation = Optional.empty();
    private Optional<String> plainLeadToken = Optional.empty();
    private Optional<ImmutableArrayTable<Cell>> definitionCells = Optional.empty();

    private Optional<Row> startChange = Optional.empty();
    private Optional<Integer> startAtRow = Optional.empty();
    private Optional<Stroke> startStroke = Optional.empty();
    private Optional<Optional<Notation>> startNotation = Optional.empty();

    private Optional<Integer> terminationMaxRows = Optional.empty();
    private Optional<Optional<Integer>> terminationMaxLeads = Optional.empty();
    private Optional<Optional<Integer>> terminationMaxParts = Optional.empty();
    private Optional<Integer> terminationMaxCircularity = Optional.empty();
    private Optional<Optional<Row>> terminationChange = Optional.empty();

    private Optional<ImmutableArrayTable<Cell>> compositionCells = Optional.empty();


    // only useable in this package
    CompositionBuilder() {
    }

    /**
     * Sets the standard out the box defauts using the standard setters
     * @return
     */
    CompositionBuilder defaults() {
        actionName = Optional.of("New");

        setTitle(DEFAULT_TITLE);
        setAuthor("");

        setNumberOfBells(NumberOfBells.BELLS_6);
        setCompositionType(CompositionType.COURSE_BASED);

        setCallFromBell(numberOfBells.get().getTenor());
        setAllNotations(HashTreePSet.empty());
        setNonSplicedActiveNotation(Optional.empty());
        setPlainLeadToken("p");
        setCells(DEFINITION_TABLE, new TableBackedImmutableArrayTable<>(EmptyCell::new));

        setStartChange(MethodBuilder.buildRoundsRow(numberOfBells.get()));
        setStartAtRow(0);
        setStartStroke(Stroke.BACKSTROKE);
        setStartNotation(Optional.empty());

        setTerminationMaxRows(TERMINATION_MAX_ROWS_INITIAL_VALUE);
        setTerminationMaxLeads(Optional.empty());
        setTerminationMaxParts(Optional.empty());
        setTerminationMaxCircularity(TERMINATION_MAX_CIRCULARITY_INITIAL_VALUE);
        setTerminationChange(Optional.empty());

        setCells(COMPOSITION_TABLE, new TableBackedImmutableArrayTable<>(EmptyCell::new));

        return this;
    }

    CompositionBuilder prototypeOf(Composition prototype) {
        this.prototype = prototype;
        return this;
    }

    CompositionBuilder setTitle(String title) {
        this.title = Optional.of(title);
        return this;
    }

    CompositionBuilder setAuthor(String author) {
        this.author = Optional.of(author);
        return this;
    }

    CompositionBuilder setNumberOfBells(NumberOfBells numberOfBells) {
        this.numberOfBells = Optional.of(numberOfBells);
        return this;
    }

    CompositionBuilder setCompositionType(CompositionType compositionType) {
        this.compositionType = Optional.of(compositionType);
        return this;
    }

    CompositionBuilder setCallFromBell(Bell callFromBell) {
        this.callFromBell = Optional.of(callFromBell);
        return this;
    }

    CompositionBuilder setAllNotations(PSet<Notation> allNotations) {
        this.allNotations = Optional.of(allNotations);
        return this;
    }

    CompositionBuilder setNonSplicedActiveNotation(Optional<Notation> nonSplicedActiveNotation) {
        this.nonSplicedActiveNotation = Optional.of(nonSplicedActiveNotation);
        return this;
    }

    CompositionBuilder setPlainLeadToken(String plainLeadToken) {
        this.plainLeadToken = Optional.of(plainLeadToken);
        return this;
    }

    CompositionBuilder setStartChange(Row startChange) {
        this.startChange = Optional.of(startChange);
        return this;
    }

    CompositionBuilder setStartAtRow(int startAtRow) {
        this.startAtRow = Optional.of(startAtRow);
        return this;
    }

    CompositionBuilder setStartStroke(Stroke startStroke) {
        this.startStroke = Optional.of(startStroke);
        return this;
    }

    CompositionBuilder setStartNotation(Optional<Notation> startNotation) {
        this.startNotation = Optional.of(startNotation);
        return this;
    }

    CompositionBuilder setTerminationMaxRows(int terminationMaxRows) {
        this.terminationMaxRows = Optional.of(terminationMaxRows);
        return this;
    }

    CompositionBuilder setTerminationMaxLeads(Optional<Integer> terminationMaxLeads) {
        this.terminationMaxLeads = Optional.of(terminationMaxLeads);
        return this;
    }

    CompositionBuilder setTerminationMaxParts(Optional<Integer> terminationMaxParts) {
        this.terminationMaxParts = Optional.of(terminationMaxParts);
        return this;
    }

    CompositionBuilder setTerminationMaxCircularity(int terminationMaxCircularity) {
        this.terminationMaxCircularity = Optional.of(terminationMaxCircularity);
        return this;
    }

    CompositionBuilder setTerminationChange(Optional<Row> terminationChange) {
        this.terminationChange = Optional.of(terminationChange);
        return this;
    }

    CompositionBuilder setCells(TableType tableType, ImmutableArrayTable<Cell> cells) {
        switch (tableType) {

            case COMPOSITION_TABLE:
                this.compositionCells = Optional.of(cells);
                break;
            case DEFINITION_TABLE:
                this.definitionCells = Optional.of(cells);
                break;
        }

        return this;
    }

    Composition build(String actionNameFormat, Object... actionNameArgs) {
        this.actionName = Optional.of(String.format(actionNameFormat, actionNameArgs));

        return new Composition(
                this.actionName.get(),

                title.orElseGet(()->prototype.getTitle()),
                author.orElseGet(()->prototype.getAuthor()),

                numberOfBells.orElseGet(()->prototype.getNumberOfBells()),
                compositionType.orElseGet(()->prototype.getCompositionType()),

                callFromBell.orElseGet(()->prototype.getCallFromBell()),
                allNotations.orElseGet(()->prototype.getAllNotations()),
                nonSplicedActiveNotation.orElseGet(()->prototype.getNonSplicedActiveNotation()),
                plainLeadToken.orElseGet(()->prototype.getPlainLeadToken()),
                definitionCells.orElseGet(()->prototype.allDefinitionCells()),

                startChange.orElseGet(()->prototype.getStartChange()),
                startAtRow.orElseGet(()->prototype.getStartAtRow()),
                startStroke.orElseGet(()->prototype.getStartStroke()),
                startNotation.orElseGet(()->prototype.getStartNotation()),

                terminationMaxRows.orElseGet(()->prototype.getTerminationMaxRows()),
                terminationMaxLeads.orElseGet(()->prototype.getTerminationMaxLeads()),
                terminationMaxParts.orElseGet(()->prototype.getTerminationMaxParts()),
                terminationMaxCircularity.orElseGet(()->prototype.getTerminationMaxCircularity()),
                terminationChange.orElseGet(()->prototype.getTerminationChange()),

                compositionCells.orElseGet(()->prototype.allCompositionCells())
        );
    }

    @Deprecated
    Composition build() {
        return build("TODO");
    }

    @Override
    public String toString() {
        return "CompositionBuilder{" +
                "prototype=" + prototype +
                ", title=" + title +
                ", author=" + author +
                ", numberOfBells=" + numberOfBells +
                ", compositionType=" + compositionType +
                ", callFromBell=" + callFromBell +
                ", allNotations=" + allNotations +
                ", nonSplicedActiveNotation=" + nonSplicedActiveNotation +
                ", plainLeadToken=" + plainLeadToken +
                ", definitionCells=" + definitionCells +
                ", startChange=" + startChange +
                ", startAtRow=" + startAtRow +
                ", startStroke=" + startStroke +
                ", startNotation=" + startNotation +
                ", terminationMaxRows=" + terminationMaxRows +
                ", terminationMaxLeads=" + terminationMaxLeads +
                ", terminationMaxParts=" + terminationMaxParts +
                ", terminationMaxCircularity=" + terminationMaxCircularity +
                ", terminationChange=" + terminationChange +
                ", compositionCells=" + compositionCells +
                '}';
    }
}
