package org.ringingmaster.engine.parsernew;

import org.ringingmaster.engine.arraytable.ImmutableArrayTable;
import org.ringingmaster.engine.parsernew.cell.ParsedCell;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class DefaultParse implements Parse {

    private final ImmutableArrayTable<ParsedCell> cells;


    public DefaultParse(ImmutableArrayTable<ParsedCell> cells) {
        this.cells = cells;
    }

    public ImmutableArrayTable<ParsedCell> getCells() {
        return cells;
    }
}
