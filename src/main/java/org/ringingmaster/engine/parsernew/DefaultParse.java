package org.ringingmaster.engine.parsernew;

import com.google.common.collect.ImmutableList;
import jdk.nashorn.internal.ir.annotations.Immutable;
import org.ringingmaster.engine.arraytable.ImmutableArrayTable;
import org.ringingmaster.engine.parsernew.cell.ParsedCell;
import org.ringingmaster.engine.parsernew.cell.ParsedDefinitionCell;
import org.ringingmaster.engine.touch.newcontainer.Touch;

/**
 * TODO comments???
 *
 * @author stevelake
 */
@Immutable
public class DefaultParse implements Parse {

    private final Touch touch;
    private final ImmutableArrayTable<ParsedCell> cells;
    private final ImmutableList<ParsedDefinitionCell> parsedDefinitions;


    DefaultParse(Touch touch, ImmutableArrayTable<ParsedCell> cells, ImmutableList<ParsedDefinitionCell> parsedDefinitions) {
        this.touch = touch;
        this.cells = cells;
        this.parsedDefinitions = parsedDefinitions;
    }

    @Override
    public Touch getTouch() {
        return touch;
    }

    @Override
    public ImmutableArrayTable<ParsedCell> getCells() {
        return cells;
    }

    @Override
    public ImmutableList<ParsedDefinitionCell> getParsedDefinitions() {
        return parsedDefinitions;
    }
}
