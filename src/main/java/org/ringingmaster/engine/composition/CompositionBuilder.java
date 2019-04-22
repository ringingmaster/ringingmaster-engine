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
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.composition.cell.Cell;
import org.ringingmaster.engine.composition.cell.EmptyCell;
import org.ringingmaster.engine.composition.compositiontype.CompositionType;

import java.util.Optional;

import static org.ringingmaster.engine.composition.ObservableComposition.TERMINATION_MAX_CIRCULARITY_INITIAL_VALUE;
import static org.ringingmaster.engine.composition.ObservableComposition.TERMINATION_MAX_ROWS_INITIAL_VALUE;
import static org.ringingmaster.engine.composition.TableType.DEFINITION_TABLE;
import static org.ringingmaster.engine.composition.TableType.MAIN_TABLE;


/**
 * Used exclusively by class ObservableComposition.
 *
 * @author Lake
 */
class CompositionBuilder {

    public static final String DEFAULT_TITLE = "UNNAMED";

    private Composition prototype;

    private Optional<String> title = Optional.empty();
    private Optional<String> author = Optional.empty();

    private Optional<NumberOfBells> numberOfBells = Optional.empty();
    private Optional<CompositionType> checkingType = Optional.empty();

    private Optional<Bell> callFromBell = Optional.empty();
    private Optional<PSet<NotationBody>> allNotations = Optional.empty();
    private Optional<Optional<NotationBody>> nonSplicedActiveNotation = Optional.empty();
    private Optional<String> plainLeadToken = Optional.empty();
    private Optional<ImmutableArrayTable<Cell>> definitionCells = Optional.empty();

    private Optional<Row> startChange = Optional.empty();
    private Optional<Integer> startAtRow = Optional.empty();
    private Optional<Stroke> startStroke = Optional.empty();
    private Optional<Optional<NotationBody>> startNotation = Optional.empty();

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
        setTitle(DEFAULT_TITLE);
        setAuthor("");

        setNumberOfBells(NumberOfBells.BELLS_6);
        setCheckingType(CompositionType.COURSE_BASED);

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

        setCells(MAIN_TABLE, new TableBackedImmutableArrayTable<>(EmptyCell::new));

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

    CompositionBuilder setCheckingType(CompositionType compositionType) {
        this.checkingType = Optional.of(compositionType);
        return this;
    }

    CompositionBuilder setCallFromBell(Bell callFromBell) {
        this.callFromBell = Optional.of(callFromBell);
        return this;
    }

    CompositionBuilder setAllNotations(PSet<NotationBody> allNotations) {
        this.allNotations = Optional.of(allNotations);
        return this;
    }

    CompositionBuilder setNonSplicedActiveNotation(Optional<NotationBody> nonSplicedActiveNotation) {
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

    CompositionBuilder setStartNotation(Optional<NotationBody> startNotation) {
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

            case MAIN_TABLE:
                this.compositionCells = Optional.of(cells);
                break;
            case DEFINITION_TABLE:
                this.definitionCells = Optional.of(cells);
                break;
        }

        return this;
    }


    Composition build() {
        return new Composition(
                title.orElseGet(()->prototype.getTitle()),
                author.orElseGet(()->prototype.getAuthor()),

                numberOfBells.orElseGet(()->prototype.getNumberOfBells()),
                checkingType.orElseGet(()->prototype.getCompositionType()),

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

    @Override
    public String toString() {
        return "CompositionBuilder{" +
                "prototype=" + prototype +
                ", title=" + title +
                ", author=" + author +
                ", numberOfBells=" + numberOfBells +
                ", checkingType=" + checkingType +
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
