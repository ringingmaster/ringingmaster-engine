package org.ringingmaster.engine.parsernew;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import org.ringingmaster.engine.arraytable.TableBackedImmutableArrayTable;
import org.ringingmaster.engine.parsernew.cell.EmptyParsedCell;
import org.ringingmaster.engine.parsernew.cell.ParsedCell;
import org.ringingmaster.engine.parsernew.cell.ParsedDefinitionCell;
import org.ringingmaster.engine.touch.newcontainer.Touch;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class ParseBuilder {

    private Optional<Touch> prototypeTouch = Optional.empty();
    private Optional<Parse> prototypeParse = Optional.empty();
    private HashBasedTable<Integer, Integer, ParsedCell> parsedCells;
    private List<ParsedDefinitionCell> parsedDefinitionCells = Collections.emptyList();

    public Parse build() {
        if (prototypeTouch.isPresent()) {
            return new DefaultParse(
                    prototypeTouch.get(),
                    new TableBackedImmutableArrayTable<>(parsedCells, () -> EmptyParsedCell.INSTANCE),
                    ImmutableList.copyOf(parsedDefinitionCells));
        }
        else if (prototypeParse.isPresent()) {
            return new DefaultParse(
                    prototypeParse.get().getTouch(),
                    new TableBackedImmutableArrayTable<>(parsedCells, () -> EmptyParsedCell.INSTANCE),
                    ImmutableList.copyOf(parsedDefinitionCells));
        }
        else {
            throw new IllegalStateException();
        }
    }

    public ParseBuilder prototypeOf(Touch prototypeTouch) {
        checkState(!prototypeParse.isPresent());
        this.prototypeTouch = Optional.of(checkNotNull(prototypeTouch));
        return this;
    }

    public ParseBuilder prototypeOf(Parse prototypeParse) {
        checkState(!prototypeTouch.isPresent());
        this.prototypeParse = Optional.of(checkNotNull(prototypeParse));
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

}
