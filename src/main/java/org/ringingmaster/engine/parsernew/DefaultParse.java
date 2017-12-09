package org.ringingmaster.engine.parsernew;

import com.google.common.collect.ImmutableList;
import jdk.nashorn.internal.ir.annotations.Immutable;
import org.ringingmaster.engine.arraytable.ImmutableArrayTable;
import org.ringingmaster.engine.parsernew.cell.ParsedCell;
import org.ringingmaster.engine.parsernew.cell.ParsedDefinitionCell;
import org.ringingmaster.engine.touch.newcontainer.Touch;
import org.ringingmaster.engine.touch.newcontainer.cellmanipulation.CellManipulation;

/**
 * TODO comments???
 *
 * @author stevelake
 */
@Immutable
public class DefaultParse implements Parse {

    private final Touch touch;
    private final ImmutableList<ParsedDefinitionCell> parsedDefinitions;
    private final CellManipulation<ParsedCell> cellManipulationDelegate;

    DefaultParse(Touch touch, ImmutableArrayTable<ParsedCell> cells, ImmutableList<ParsedDefinitionCell> parsedDefinitions) {
        this.touch = touch;
        this.parsedDefinitions = parsedDefinitions;
        this.cellManipulationDelegate = new CellManipulation<>(cells, touch.getCheckingType(), touch.isSpliced());
    }

    @Override
    public Touch getTouch() {
        return touch;
    }

    @Override
    public ImmutableArrayTable<ParsedCell> allCells() {
        return cellManipulationDelegate.allCells();
    }

    @Override
    public ImmutableArrayTable<ParsedCell> mainBodyCells() {
        return cellManipulationDelegate.mainBodyCells();
    }

    @Override
    public ImmutableArrayTable<ParsedCell> callPositionCells() {
        return cellManipulationDelegate.callPositionCells();
    }

    @Override
    public ImmutableArrayTable<ParsedCell> splicedCells() {
        return cellManipulationDelegate.splicedCells();
    }


    @Override
    public ImmutableList<ParsedDefinitionCell> getDefinitions() {
        return parsedDefinitions;
    }

    @Override
    public String toString() {
        return "DefaultParse{" +
                "touch=" + touch +
                ", parsedDefinitions=" + parsedDefinitions +
                ", cellManipulationDelegate=" + cellManipulationDelegate +
                '}';
    }
}
