package org.ringingmaster.engine.parsernew;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import org.ringingmaster.engine.arraytable.TableBackedImmutableArrayTable;
import org.ringingmaster.engine.parsernew.cell.EmptyParsedCell;
import org.ringingmaster.engine.parsernew.cell.ParsedCell;
import org.ringingmaster.engine.parsernew.cell.ParsedDefinitionCell;
import org.ringingmaster.engine.touch.newcontainer.Touch;

import java.util.List;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class ParseBuilder {

    private Touch touch;
    private HashBasedTable<Integer, Integer, ParsedCell> parsedCells;
    private List<ParsedDefinitionCell> parsedDefinitionCells;

    public ParseBuilder prototypeOf(Touch touch) {
        this.touch = touch;
        return this;
    }

    public ParseBuilder prototypeOf(Parse parse) {
        return this;
    }

    public ParseBuilder setParsedCells(HashBasedTable<Integer, Integer, ParsedCell> parsedCells) {
        this.parsedCells = parsedCells;
        return this;
    }

    public ParseBuilder setDefinitions(List<ParsedDefinitionCell> parsedDefinitionCells) {
        this.parsedDefinitionCells = parsedDefinitionCells;
        return this;

    }

    public Parse build() {
        return new DefaultParse(
                touch,
                new TableBackedImmutableArrayTable<>(parsedCells, () -> EmptyParsedCell.INSTANCE),
                ImmutableList.copyOf(parsedDefinitionCells));
    }
}
