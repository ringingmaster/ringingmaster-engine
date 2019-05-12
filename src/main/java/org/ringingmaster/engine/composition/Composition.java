package org.ringingmaster.engine.composition;


import com.google.common.collect.ImmutableSet;
import org.pcollections.HashTreePSet;
import org.pcollections.PSet;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.arraytable.ImmutableArrayTable;
import org.ringingmaster.engine.composition.tableaccess.DefaultCompositionTableAccess;
import org.ringingmaster.engine.method.Bell;
import org.ringingmaster.engine.method.Row;
import org.ringingmaster.engine.method.Stroke;
import org.ringingmaster.engine.notation.Notation;
import org.ringingmaster.engine.notation.NotationBuilderHelper;
import org.ringingmaster.engine.composition.cell.Cell;
import org.ringingmaster.engine.composition.compositiontype.CompositionType;
import org.ringingmaster.engine.composition.tableaccess.DefaultDefinitionTableAccess;
import org.ringingmaster.engine.composition.tableaccess.DefinitionTableAccess;
import org.ringingmaster.engine.composition.tableaccess.CompositionTableAccess;

import javax.annotation.concurrent.Immutable;
import java.util.Optional;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Raw immutable POJO for a composition.
 *
 * @author Steve Lake
 */
@Immutable
public class Composition implements CompositionTableAccess<Cell>, DefinitionTableAccess<Cell> {

    private final String actionName;

    private final String title;
    private final String author;

    private final NumberOfBells numberOfBells;
    private final CompositionType compositionType;

    private final Bell callFromBell;
    private final PSet<Notation> allNotations;
    private final Optional<Notation> nonSplicedActiveNotation;
    private final String plainLeadToken;
    private final DefinitionTableAccess<Cell> definitionTableCellsDelegate;

    private final Row startChange;
    private final int startAtRow;
    private final Stroke startStroke;
    private final Optional<Notation> startNotation;

    private final int terminationMaxRows;
    private final Optional<Integer> terminationMaxLeads;
    private final Optional<Integer> terminationMaxParts;
    private final int terminationMaxCircularity;
    private final Optional<Row> terminationChange;

    private final CompositionTableAccess<Cell> compositionTableAccessDelegate;

    public Composition(String actionName,
                       String title,
                       String author,
                       NumberOfBells numberOfBells,
                       CompositionType compositionType,
                       Bell callFromBell,
                       PSet<Notation> allNotations,
                       Optional<Notation> nonSplicedActiveNotation,
                       String plainLeadToken,
                       ImmutableArrayTable<Cell> definitionCells,
                       Row startChange, int startAtRow,
                       Stroke startStroke,
                       Optional<Notation> startNotation,
                       int terminationMaxRows,
                       Optional<Integer> terminationMaxLeads,
                       Optional<Integer> terminationMaxParts,
                       int terminationMaxCircularity,
                       Optional<Row> terminationChange,
                       ImmutableArrayTable<Cell> cells) {

        this.actionName = actionName;

        this.title = title;
        this.author = author;

        this.numberOfBells = numberOfBells;
        this.compositionType = compositionType;

        this.callFromBell = callFromBell;
        this.allNotations = allNotations;
        this.nonSplicedActiveNotation = nonSplicedActiveNotation;
        this.plainLeadToken = plainLeadToken;
        this.definitionTableCellsDelegate = new DefaultDefinitionTableAccess<>(definitionCells);

        this.startChange = startChange;
        this.startAtRow = startAtRow;
        this.startStroke = startStroke;
        this.startNotation = startNotation;

        this.terminationMaxRows = terminationMaxRows;
        this.terminationMaxLeads = terminationMaxLeads;
        this.terminationMaxParts = terminationMaxParts;
        this.terminationMaxCircularity = terminationMaxCircularity;
        this.terminationChange = terminationChange;

        this.compositionTableAccessDelegate = new DefaultCompositionTableAccess<>(cells, compositionType, isSpliced());
    }

    public String getActionName() {
        return actionName;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public NumberOfBells getNumberOfBells() {
        return numberOfBells;
    }

    public CompositionType getCompositionType() {
        return compositionType;
    }

    public Bell getCallFromBell() {
        return callFromBell;
    }

    public PSet<Notation> getAllNotations() {
        return allNotations;
    }

    public PSet<Notation> getValidNotations() {
        //TODO precalculate
        return NotationBuilderHelper.filterNotationsUptoNumberOfBells(allNotations, numberOfBells);
    }

    public PSet<Notation> getAvailableNotations() {
        //TODO precalculate
        if (isSpliced()) {
            return getValidNotations();
        }
        else {
            // Not Spliced
            return nonSplicedActiveNotation.map(HashTreePSet::singleton).orElseGet(HashTreePSet::empty);
        }
    }

    public Optional<Notation> getNonSplicedActiveNotation() {
        return nonSplicedActiveNotation;
    }

    public boolean isSpliced() {
        return !allNotations.isEmpty() && !nonSplicedActiveNotation.isPresent();
    }

    public String getPlainLeadToken() {
        return plainLeadToken;
    }

    @Override
    public ImmutableArrayTable<Cell> allDefinitionCells() {
        return definitionTableCellsDelegate.allDefinitionCells();
    }

    @Override
    public ImmutableArrayTable<Cell> definitionShorthandCells() {
        return definitionTableCellsDelegate.definitionShorthandCells();
    }

    @Override
    public Optional<ImmutableArrayTable<Cell>> findDefinitionByShorthand(String shorthand) {
        checkNotNull(shorthand);
        return definitionTableCellsDelegate.findDefinitionByShorthand(shorthand);
    }

    @Override
    public Set<ImmutableArrayTable<Cell>> getDefinitionAsTables() {
        return definitionTableCellsDelegate.getDefinitionAsTables();
    }

    @Override
    public ImmutableArrayTable<Cell> definitionDefinitionCells() {
        return definitionTableCellsDelegate.definitionDefinitionCells();
    }


    public ImmutableSet<String> getAllDefinitionShorthands()  {
        return definitionTableCellsDelegate.getAllDefinitionShorthands();
    }

    public Row getStartChange() {
        return startChange;
    }

    public int getStartAtRow() {
        return startAtRow;
    }

    public Stroke getStartStroke() {
        return startStroke;
    }

    public Optional<Notation> getStartNotation() {
        return startNotation;
    }

    public int getTerminationMaxRows() {
        return terminationMaxRows;
    }

    public Optional<Integer> getTerminationMaxLeads() {
        return terminationMaxLeads;
    }

    public Optional<Integer> getTerminationMaxParts() {
        return terminationMaxParts;
    }

    public int getTerminationMaxCircularity() {
        return terminationMaxCircularity;
    }

    public Optional<Row> getTerminationChange() {
        return terminationChange;
    }

    @Override
    public ImmutableArrayTable<Cell> allCompositionCells() {
        return compositionTableAccessDelegate.allCompositionCells();
    }

    @Override
    public ImmutableArrayTable<Cell> mainBodyCells() {
        return compositionTableAccessDelegate.mainBodyCells();
    }

    @Override
    public ImmutableArrayTable<Cell> callingPositionCells() {
        return compositionTableAccessDelegate.callingPositionCells();
    }

    @Override
    public ImmutableArrayTable<Cell> splicedCells() {
        return compositionTableAccessDelegate.splicedCells();
    }

    @Override
    public ImmutableArrayTable<Cell> nullAreaCells() {
        return compositionTableAccessDelegate.nullAreaCells();
    }

    @Override
    public String toString() {
        return "Composition{" +
                "<" + actionName + "> " +
                "title='" + title + '\'' +
                ", author=" + author +
                ", numberOfBells='" + numberOfBells + '\'' +
                ", compositionType=" + compositionType +
                ", callFromBell='" + callFromBell + '\'' +
                ", allNotations=" + allNotations +
                ", nonSplicedActiveNotation=" + nonSplicedActiveNotation +
                ", plainLeadToken='" + plainLeadToken + '\'' +
                ", definitions=" + definitionTableCellsDelegate.allDefinitionCells() +
                ", startChange=" + startChange +
                ", startAtRow=" + startAtRow +
                ", startStroke=" + startStroke +
                ", startNotation=" + startNotation +
                ", terminationMaxRows=" + terminationMaxRows +
                ", terminationMaxLeads=" + terminationMaxLeads +
                ", terminationMaxParts=" + terminationMaxParts +
                ", terminationMaxCircularity=" + terminationMaxCircularity +
                ", terminationChange=" + terminationChange +
                ", cells=" + compositionTableAccessDelegate.allCompositionCells() +
                '}';
    }
}
