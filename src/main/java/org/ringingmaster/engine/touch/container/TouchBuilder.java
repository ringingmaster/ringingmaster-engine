package org.ringingmaster.engine.touch.container;

import org.pcollections.HashTreePSet;
import org.pcollections.PSet;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.arraytable.ImmutableArrayTable;
import org.ringingmaster.engine.arraytable.TableBackedImmutableArrayTable;
import org.ringingmaster.engine.method.Bell;
import org.ringingmaster.engine.method.MethodRow;
import org.ringingmaster.engine.method.Stroke;
import org.ringingmaster.engine.method.impl.MethodBuilder;
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.touch.container.cell.Cell;
import org.ringingmaster.engine.touch.container.cell.EmptyCell;
import org.ringingmaster.engine.touch.container.checkingtype.CheckingType;

import java.util.Optional;

import static org.ringingmaster.engine.touch.container.ObservableTouch.TERMINATION_MAX_CIRCULARITY_INITIAL_VALUE;
import static org.ringingmaster.engine.touch.container.ObservableTouch.TERMINATION_MAX_ROWS_INITIAL_VALUE;
import static org.ringingmaster.engine.touch.container.TableType.DEFINITION_TABLE;
import static org.ringingmaster.engine.touch.container.TableType.TOUCH_TABLE;


/**
 * TODO Comments
 *
 * @author Lake
 */
class TouchBuilder {

    private Touch prototype;

    private Optional<String> title = Optional.empty();
    private Optional<String> author = Optional.empty();

    private Optional<NumberOfBells> numberOfBells = Optional.empty();
    private Optional<CheckingType> touchCheckingType = Optional.empty();

    private Optional<Bell> callFromBell = Optional.empty();
    private Optional<PSet<NotationBody>> allNotations = Optional.empty();
    private Optional<Optional<NotationBody>> nonSplicedActiveNotation = Optional.empty();
    private Optional<String> plainLeadToken = Optional.empty();
    private Optional<ImmutableArrayTable<Cell>> definitionCells = Optional.empty();

    private Optional<MethodRow> startChange = Optional.empty();
    private Optional<Integer> startAtRow = Optional.empty();
    private Optional<Stroke> startStroke = Optional.empty();
    private Optional<Optional<NotationBody>> startNotation = Optional.empty();

    private Optional<Integer> terminationMaxRows = Optional.empty();
    private Optional<Optional<Integer>> terminationMaxLeads = Optional.empty();
    private Optional<Optional<Integer>> terminationMaxParts = Optional.empty();
    private Optional<Integer> terminationMaxCircularity = Optional.empty();
    private Optional<Optional<MethodRow>> terminationChange = Optional.empty();

    private Optional<ImmutableArrayTable<Cell>> touchCells = Optional.empty();


    // only useable in this package
    TouchBuilder() {
    }

    /**
     * Sets the standard out the box defauts using the standard setters
     * @return
     */
    TouchBuilder defaults() {
        setTitle("");
        setAuthor("");

        setNumberOfBells(NumberOfBells.BELLS_6);
        setTouchCheckingType(CheckingType.COURSE_BASED);

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

        setCells(TOUCH_TABLE, new TableBackedImmutableArrayTable<>(EmptyCell::new));

        return this;
    }

    TouchBuilder prototypeOf(Touch prototype) {
        this.prototype = prototype;
        return this;
    }

    TouchBuilder setTitle(String title) {
        this.title = Optional.of(title);
        return this;
    }

    TouchBuilder setAuthor(String author) {
        this.author = Optional.of(author);
        return this;
    }

    TouchBuilder setNumberOfBells(NumberOfBells numberOfBells) {
        this.numberOfBells = Optional.of(numberOfBells);
        return this;
    }

    TouchBuilder setTouchCheckingType(CheckingType checkingType) {
        this.touchCheckingType = Optional.of(checkingType);
        return this;
    }

    TouchBuilder setCallFromBell(Bell callFromBell) {
        this.callFromBell = Optional.of(callFromBell);
        return this;
    }

    TouchBuilder setAllNotations(PSet<NotationBody> allNotations) {
        this.allNotations = Optional.of(allNotations);
        return this;
    }

    TouchBuilder setNonSplicedActiveNotation(Optional<NotationBody> nonSplicedActiveNotation) {
        this.nonSplicedActiveNotation = Optional.of(nonSplicedActiveNotation);
        return this;
    }

    TouchBuilder setPlainLeadToken(String plainLeadToken) {
        this.plainLeadToken = Optional.of(plainLeadToken);
        return this;
    }

    TouchBuilder setStartChange(MethodRow startChange) {
        this.startChange = Optional.of(startChange);
        return this;
    }

    TouchBuilder setStartAtRow(int startAtRow) {
        this.startAtRow = Optional.of(startAtRow);
        return this;
    }

    TouchBuilder setStartStroke(Stroke startStroke) {
        this.startStroke = Optional.of(startStroke);
        return this;
    }

    TouchBuilder setStartNotation(Optional<NotationBody> startNotation) {
        this.startNotation = Optional.of(startNotation);
        return this;
    }

    TouchBuilder setTerminationMaxRows(int terminationMaxRows) {
        this.terminationMaxRows = Optional.of(terminationMaxRows);
        return this;
    }

    TouchBuilder setTerminationMaxLeads(Optional<Integer> terminationMaxLeads) {
        this.terminationMaxLeads = Optional.of(terminationMaxLeads);
        return this;
    }

    TouchBuilder setTerminationMaxParts(Optional<Integer> terminationMaxParts) {
        this.terminationMaxParts = Optional.of(terminationMaxParts);
        return this;
    }

    TouchBuilder setTerminationMaxCircularity(int terminationMaxCircularity) {
        this.terminationMaxCircularity = Optional.of(terminationMaxCircularity);
        return this;
    }

    TouchBuilder setTerminationChange(Optional<MethodRow> terminationChange) {
        this.terminationChange = Optional.of(terminationChange);
        return this;
    }

    TouchBuilder setCells(TableType tableType, ImmutableArrayTable<Cell> cells) {
        switch (tableType) {

            case TOUCH_TABLE:
                this.touchCells = Optional.of(cells);
                break;
            case DEFINITION_TABLE:
                this.definitionCells = Optional.of(cells);
                break;
        }

        return this;
    }


    Touch build() {
        return new Touch(
                title.orElseGet(()->prototype.getTitle()),
                author.orElseGet(()->prototype.getAuthor()),

                numberOfBells.orElseGet(()->prototype.getNumberOfBells()),
                touchCheckingType.orElseGet(()->prototype.getCheckingType()),

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

                touchCells.orElseGet(()->prototype.allTouchCells())
        );
    }

    @Override
    public String toString() {
        return "TouchBuilder{" +
                "prototype=" + prototype +
                ", title=" + title +
                ", author=" + author +
                ", numberOfBells=" + numberOfBells +
                ", touchCheckingType=" + touchCheckingType +
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
                ", touchCells=" + touchCells +
                '}';
    }
}
