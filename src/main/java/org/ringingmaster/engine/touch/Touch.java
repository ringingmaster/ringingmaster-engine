package org.ringingmaster.engine.touch;


import com.google.common.collect.ImmutableSet;
import org.pcollections.HashTreePSet;
import org.pcollections.PSet;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.arraytable.ImmutableArrayTable;
import org.ringingmaster.engine.method.Bell;
import org.ringingmaster.engine.method.MethodRow;
import org.ringingmaster.engine.method.Stroke;
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.notation.impl.NotationBuilderHelper;
import org.ringingmaster.engine.touch.cell.Cell;
import org.ringingmaster.engine.touch.checkingtype.CheckingType;
import org.ringingmaster.engine.touch.tableaccess.DefaultDefinitionTableAccess;
import org.ringingmaster.engine.touch.tableaccess.DefaultTouchTableAccess;
import org.ringingmaster.engine.touch.tableaccess.DefinitionTableAccess;
import org.ringingmaster.engine.touch.tableaccess.TouchTableAccess;

import javax.annotation.concurrent.Immutable;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Raw immutable POJO for a touch.
 *
 * @author Lake
 */
@Immutable
public class Touch implements TouchTableAccess<Cell>, DefinitionTableAccess<Cell> {

    private final String title;
    private final String author;

    private final NumberOfBells numberOfBells;
    private final CheckingType checkingType;

    private final Bell callFromBell;
    private final PSet<NotationBody> allNotations;
    private final Optional<NotationBody> nonSplicedActiveNotation;
    private final String plainLeadToken;
    private final DefinitionTableAccess<Cell> definitionTableCellsDelegate;

    private final MethodRow startChange;
    private final int startAtRow;
    private final Stroke startStroke;
    private final Optional<NotationBody> startNotation;

    private final int terminationMaxRows;
    private final Optional<Integer> terminationMaxLeads;
    private final Optional<Integer> terminationMaxParts;
    private final int terminationMaxCircularity;
    private final Optional<MethodRow> terminationChange;

    private final TouchTableAccess<Cell> touchTableAccessDelegate;

    public Touch(String title,
                 String author,
                 NumberOfBells numberOfBells,
                 CheckingType checkingType,
                 Bell callFromBell,
                 PSet<NotationBody> allNotations,
                 Optional<NotationBody> nonSplicedActiveNotation,
                 String plainLeadToken,
                 ImmutableArrayTable<Cell> definitionCells,
                 MethodRow startChange, int startAtRow,
                 Stroke startStroke,
                 Optional<NotationBody> startNotation,
                 int terminationMaxRows,
                 Optional<Integer> terminationMaxLeads,
                 Optional<Integer> terminationMaxParts,
                 int terminationMaxCircularity,
                 Optional<MethodRow> terminationChange,
                 ImmutableArrayTable<Cell> cells) {
        this.title = title;
        this.author = author;

        this.numberOfBells = numberOfBells;
        this.checkingType = checkingType;

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

        touchTableAccessDelegate = new DefaultTouchTableAccess<>(cells, checkingType, isSpliced());
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

    public CheckingType getCheckingType() {
        return checkingType;
    }

    public Bell getCallFromBell() {
        return callFromBell;
    }

    public PSet<NotationBody> getAllNotations() {
        return allNotations;
    }

    public PSet<NotationBody> getValidNotations() {
        //TODO precalculate
        return NotationBuilderHelper.filterNotationsUptoNumberOfBells(allNotations, numberOfBells);
    }

    public PSet<NotationBody> getAvailableNotations() {
        //TODO precalculate
        if (isSpliced()) {
            return getValidNotations();
        }
        else {
            // Not Spliced
            return nonSplicedActiveNotation.map(HashTreePSet::singleton).orElseGet(HashTreePSet::empty);
        }
    }

    public Optional<NotationBody> getNonSplicedActiveNotation() {
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
    public ImmutableArrayTable<Cell> definitionDefinitionCells() {
        return definitionTableCellsDelegate.definitionShorthandCells();
    }


    public ImmutableSet<String> getAllDefinitionShorthands()  {
        return definitionTableCellsDelegate.getAllDefinitionShorthands();
    }

    public MethodRow getStartChange() {
        return startChange;
    }

    public int getStartAtRow() {
        return startAtRow;
    }

    public Stroke getStartStroke() {
        return startStroke;
    }

    public Optional<NotationBody> getStartNotation() {
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

    public Optional<MethodRow> getTerminationChange() {
        return terminationChange;
    }

    @Override
    public ImmutableArrayTable<Cell> allTouchCells() {
        return touchTableAccessDelegate.allTouchCells();
    }

    @Override
    public ImmutableArrayTable<Cell> mainBodyCells() {
        return touchTableAccessDelegate.mainBodyCells();
    }

    @Override
    public ImmutableArrayTable<Cell> callPositionCells() {
        return touchTableAccessDelegate.callPositionCells();
    }

    @Override
    public ImmutableArrayTable<Cell> splicedCells() {
        return touchTableAccessDelegate.splicedCells();
    }


    @Override
    public String toString() {
        return "Touch{" +
                "title='" + title + '\'' +
                ", author=" + author +
                ", numberOfBells='" + numberOfBells + '\'' +
                ", touchType=" + checkingType +
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
                ", cells=" + touchTableAccessDelegate.allTouchCells() +
                '}';
    }
}
