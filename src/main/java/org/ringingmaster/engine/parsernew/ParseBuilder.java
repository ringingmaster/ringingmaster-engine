package org.ringingmaster.engine.parsernew;

import com.google.common.collect.HashBasedTable;
import org.ringingmaster.engine.arraytable.TableBackedImmutableArrayTable;
import org.ringingmaster.engine.parsernew.cell.EmptyParsedCell;
import org.ringingmaster.engine.parsernew.cell.ParsedCell;
import org.ringingmaster.engine.touch.newcontainer.Touch;

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
    private HashBasedTable<Integer, Integer, ParsedCell> touchCells;
    private HashBasedTable<Integer, Integer, ParsedCell> definitionCells;

    public Parse build() {
        checkNotNull(touchCells);
        checkNotNull(definitionCells);

        if (prototypeTouch.isPresent()) {
            return new DefaultParse(
                    prototypeTouch.get(),
                    new TableBackedImmutableArrayTable<>(touchCells, () -> EmptyParsedCell.INSTANCE),
                    new TableBackedImmutableArrayTable<>(definitionCells, () -> EmptyParsedCell.INSTANCE));
        }
        else if (prototypeParse.isPresent()) {
            return new DefaultParse(
                    prototypeParse.get().getTouch(),
                    new TableBackedImmutableArrayTable<>(touchCells, () -> EmptyParsedCell.INSTANCE),
                    new TableBackedImmutableArrayTable<>(definitionCells, () -> EmptyParsedCell.INSTANCE));
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

    public ParseBuilder setTouchTableCells(HashBasedTable<Integer, Integer, ParsedCell> touchCells) {
        this.touchCells = touchCells;
        return this;
    }

    public ParseBuilder setDefinitionTableCells(HashBasedTable<Integer, Integer, ParsedCell> definitionCells) {
        this.definitionCells = definitionCells;
        return this;
    }

}
