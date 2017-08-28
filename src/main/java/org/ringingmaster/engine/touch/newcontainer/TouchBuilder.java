package org.ringingmaster.engine.touch.newcontainer;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.method.Bell;
import org.ringingmaster.engine.method.MethodRow;
import org.ringingmaster.engine.method.Stroke;
import org.ringingmaster.engine.method.impl.MethodBuilder;
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.touch.container.TouchCheckingType;
import org.ringingmaster.engine.touch.newcontainer.definition.Definition;

import java.util.Optional;

import static org.ringingmaster.engine.touch.container.Touch.TERMINATION_CIRCULAR_TOUCH_INITIAL_VALUE;
import static org.ringingmaster.engine.touch.container.Touch.TERMINATION_MAX_ROWS_INITIAL_VALUE;

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
    private Optional<TouchCheckingType> touchCheckingType = Optional.empty();

    private Optional<Bell> callFromBell = Optional.empty();
    private Optional<ImmutableList<NotationBody>> sortedNotations = Optional.empty();
    private Optional<Optional<NotationBody>> nonSplicedActiveNotation = Optional.empty();
    private Optional<Boolean> spliced = Optional.empty(); // we use separate spliced and active-notation, rather than an optional because otherwise, adding your first notation will always be spliced.
    private Optional<String> plainLeadToken = Optional.empty();
    private Optional<ImmutableSet<Definition>> definitions = Optional.empty();

    private Optional<MethodRow> startChange = Optional.empty();
    private Optional<Integer> startAtRow = Optional.empty();
    private Optional<Stroke> startStroke = Optional.empty();
    private Optional<Optional<NotationBody>> startNotation = Optional.empty();

    private Optional<Integer> terminationMaxRows = Optional.empty();
    private Optional<Optional<Integer>> terminationMaxLeads = Optional.empty();
    private Optional<Optional<Integer>> terminationMaxParts = Optional.empty();
    private Optional<Optional<Integer>> terminationMaxCircularTouch = Optional.empty();
    private Optional<Optional<MethodRow>> terminationChange = Optional.empty();


    /**
     * Sets the standard out the box defauts using the standard setters
     * @return
     */
    TouchBuilder defaults() {
        setTitle("");
        setAuthor("");

        setNumberOfBells(NumberOfBells.BELLS_6);
        setTouchCheckingType(TouchCheckingType.COURSE_BASED);

        setCallFromBell(numberOfBells.get().getTenor());
        setSortedNotations(ImmutableList.of());
        setNonSplicedActiveNotation(Optional.empty());
        setSpliced(false);
        setPlainLeadToken("p");
        setDefinitions(ImmutableSet.of());

        setStartChange(MethodBuilder.buildRoundsRow(numberOfBells.get()));
        setStartAtRow(0);
        setStartStroke(Stroke.BACKSTROKE);
        setStartNotation(Optional.empty());

        setTerminationMaxRows(TERMINATION_MAX_ROWS_INITIAL_VALUE);
        setTerminationMaxLeads(Optional.empty());
        setTerminationMaxParts(Optional.empty());
        setTerminationMaxCircularTouch(Optional.of(TERMINATION_CIRCULAR_TOUCH_INITIAL_VALUE));
        setTerminationChange(Optional.empty());

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

    TouchBuilder setTouchCheckingType(TouchCheckingType touchCheckingType) {
        this.touchCheckingType = Optional.of(touchCheckingType);
        return this;
    }

    TouchBuilder setCallFromBell(Bell callFromBell) {
        this.callFromBell = Optional.of(callFromBell);
        return this;
    }

    TouchBuilder setSortedNotations(ImmutableList<NotationBody> sortedNotations) {
        this.sortedNotations = Optional.of(sortedNotations);
        return this;
    }

    TouchBuilder setNonSplicedActiveNotation(Optional<NotationBody> nonSplicedActiveNotation) {
        this.nonSplicedActiveNotation = Optional.of(nonSplicedActiveNotation);
        return this;
    }

    TouchBuilder setSpliced(boolean spliced) {
        this.spliced = Optional.of(spliced);
        return this;
    }

    TouchBuilder setPlainLeadToken(String plainLeadToken) {
        this.plainLeadToken = Optional.of(plainLeadToken);
        return this;
    }

    TouchBuilder setDefinitions(ImmutableSet<Definition> touchDefinitions) {
        this.definitions = Optional.of(touchDefinitions);
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

    TouchBuilder setTerminationMaxCircularTouch(Optional<Integer> terminationMaxCircularTouch) {
        this.terminationMaxCircularTouch = Optional.of(terminationMaxCircularTouch);
        return this;
    }

    TouchBuilder setTerminationChange(Optional<MethodRow> terminationChange) {
        this.terminationChange = Optional.of(terminationChange);
        return this;
    }

    Touch build() {
        return new Touch(
                title.orElseGet(()->prototype.getTitle()),
                author.orElseGet(()->prototype.getAuthor()),

                numberOfBells.orElseGet(()->prototype.getNumberOfBells()),
                touchCheckingType.orElseGet(()->prototype.getTouchCheckingType()),

                callFromBell.orElseGet(()->prototype.getCallFromBell()),
                sortedNotations.orElseGet(()->prototype.getAllNotations()),
                nonSplicedActiveNotation.orElseGet(()->prototype.getNonSplicedActiveNotation()),//TODO - optional - replace spliced.
                spliced.orElseGet(()->prototype.isSpliced()),
                plainLeadToken.orElseGet(()->prototype.getPlainLeadToken()),
                definitions.orElseGet(()->prototype.getAllDefinitions()),

                startChange.orElseGet(()->prototype.getStartChange()),
                startAtRow.orElseGet(()->prototype.getStartAtRow()),
                startStroke.orElseGet(()->prototype.getStartStroke()),
                startNotation.orElseGet(()->prototype.getStartNotation()),

                terminationMaxRows.orElseGet(()->prototype.getTerminationMaxRows()),
                terminationMaxLeads.orElseGet(()->prototype.getTerminationMaxLeads()),
                terminationMaxParts.orElseGet(()->prototype.getTerminationMaxParts()),
                terminationMaxCircularTouch.orElseGet(()->prototype.getTerminationMaxCircularTouch()),
                terminationChange.orElseGet(()->prototype.getTerminationChange())
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
                ", sortedNotations=" + sortedNotations +
                ", nonSplicedActiveNotation=" + nonSplicedActiveNotation +
                ", spliced=" + spliced +
                ", plainLeadToken=" + plainLeadToken +
                ", definitions=" + definitions +
                ", startChange=" + startChange +
                ", startAtRow=" + startAtRow +
                ", startStroke=" + startStroke +
                ", startNotation=" + startNotation +
                ", terminationMaxRows=" + terminationMaxRows +
                ", terminationMaxLeads=" + terminationMaxLeads +
                ", terminationMaxParts=" + terminationMaxParts +
                ", terminationMaxCircularTouch=" + terminationMaxCircularTouch +
                ", terminationChange=" + terminationChange +
                '}';
    }
}
