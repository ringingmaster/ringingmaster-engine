package org.ringingmaster.engine.touch.newcontainer;


import com.google.common.collect.ImmutableTable;
import net.jcip.annotations.Immutable;
import org.pcollections.HashTreePSet;
import org.pcollections.PSet;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.method.Bell;
import org.ringingmaster.engine.method.MethodRow;
import org.ringingmaster.engine.method.Stroke;
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.notation.impl.NotationBuilderHelper;
import org.ringingmaster.engine.touch.newcontainer.cell.Cell;
import org.ringingmaster.engine.touch.newcontainer.cell.EmptyCell;
import org.ringingmaster.engine.touch.newcontainer.checkingtype.CheckingType;
import org.ringingmaster.engine.touch.newcontainer.definition.Definition;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkPositionIndex;

/**
 * Raw immutable POJO for a touch.
 *
 * @author Lake
 */
@Immutable
public class Touch {

    private final String title;
    private final String author;

    private final NumberOfBells numberOfBells;
    private final CheckingType checkingType;

    private final Bell callFromBell;
    private final PSet<NotationBody> allNotations;
    private final Optional<NotationBody> nonSplicedActiveNotation;
    private final String plainLeadToken;
    private final PSet<Definition> definitions;

    private final MethodRow startChange;
    private final int startAtRow;
    private final Stroke startStroke;
    private final Optional<NotationBody> startNotation;

    private final int terminationMaxRows;
    private final Optional<Integer> terminationMaxLeads;
    private final Optional<Integer> terminationMaxParts;
    private final int terminationMaxCircularity;
    private final Optional<MethodRow> terminationChange;

    private final ImmutableTable<Integer, Integer, Cell> cells;

    public Touch(String title,
                 String author,
                 NumberOfBells numberOfBells,
                 CheckingType checkingType,
                 Bell callFromBell,
                 PSet<NotationBody> allNotations,
                 Optional<NotationBody> nonSplicedActiveNotation,
                 String plainLeadToken,
                 PSet<Definition> definitions,
                 MethodRow startChange, int startAtRow,
                 Stroke startStroke,
                 Optional<NotationBody> startNotation,
                 int terminationMaxRows,
                 Optional<Integer> terminationMaxLeads,
                 Optional<Integer> terminationMaxParts,
                 int terminationMaxCircularity,
                 Optional<MethodRow> terminationChange,
                 ImmutableTable<Integer, Integer, Cell> cells) {
        this.title = title;
        this.author = author;

        this.numberOfBells = numberOfBells;
        this.checkingType = checkingType;

        this.callFromBell = callFromBell;
        this.allNotations = allNotations;
        this.nonSplicedActiveNotation = nonSplicedActiveNotation;
        this.plainLeadToken = plainLeadToken;
        this.definitions = definitions;

        this.startChange = startChange;
        this.startAtRow = startAtRow;
        this.startStroke = startStroke;
        this.startNotation = startNotation;

        this.terminationMaxRows = terminationMaxRows;
        this.terminationMaxLeads = terminationMaxLeads;
        this.terminationMaxParts = terminationMaxParts;
        this.terminationMaxCircularity = terminationMaxCircularity;
        this.terminationChange = terminationChange;
        this.cells = cells;
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

    public PSet<NotationBody> getInUseNotations() {
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

    public PSet<Definition> getAllDefinitions() {
        return definitions;
    }

    public Optional<Definition> findDefinitionByShorthand(String shorthand) {
        checkNotNull(shorthand);

        return definitions.stream()
                .filter((definition) -> shorthand.equals(definition.getShorthand()))
                .findFirst();
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

    /**
     * Not for public use
     */
    ImmutableTable<Integer, Integer, Cell> getCells() {
        return cells;
    }

    public int getColumnCount() {
        return cells.columnKeySet().stream()
                .mapToInt(value -> value+1)
                .max()
                .orElse(0);
    }

    public int getRowCount() {
        return cells.rowKeySet().stream()
                .mapToInt(value -> value+1)
                .max()
                .orElse(0);
    }

    public Cell cell(int rowIndex, int columnIndex) {
        checkPositionIndex(rowIndex, getRowCount());
        checkPositionIndex(columnIndex, getColumnCount());

        Cell cell = cells.get(rowIndex, columnIndex);
        if (cell != null) {
            return cell;
        }
        else {
            return EmptyCell.INSTANCE;
        }
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
                ", definitions=" + definitions +
                ", startChange=" + startChange +
                ", startAtRow=" + startAtRow +
                ", startStroke=" + startStroke +
                ", startNotation=" + startNotation +
                ", terminationMaxRows=" + terminationMaxRows +
                ", terminationMaxLeads=" + terminationMaxLeads +
                ", terminationMaxParts=" + terminationMaxParts +
                ", terminationMaxCircularity=" + terminationMaxCircularity +
                ", terminationChange=" + terminationChange +
                ", cells=" + cells +
                '}';
    }
}
