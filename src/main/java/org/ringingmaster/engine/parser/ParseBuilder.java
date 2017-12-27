package org.ringingmaster.engine.parser;

import com.google.common.collect.HashBasedTable;
import org.ringingmaster.engine.arraytable.ImmutableArrayTable;
import org.ringingmaster.engine.arraytable.TableBackedImmutableArrayTable;
import org.ringingmaster.engine.parser.cell.EmptyParsedCell;
import org.ringingmaster.engine.parser.cell.ParsedCell;
import org.ringingmaster.engine.touch.container.Touch;

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
    private Optional<HashBasedTable<Integer, Integer, ParsedCell>> touchCells = Optional.empty();;
    private Optional<HashBasedTable<Integer, Integer, ParsedCell>> definitionCells = Optional.empty();;

    public Parse build() {

        if (prototypeTouch.isPresent()) {
            checkState(touchCells.isPresent());
            checkState(definitionCells.isPresent());

            return new DefaultParse(
                    prototypeTouch.get(),
                    new TableBackedImmutableArrayTable<>(touchCells.get(), () -> EmptyParsedCell.INSTANCE),
                    new TableBackedImmutableArrayTable<>(definitionCells.get(), () -> EmptyParsedCell.INSTANCE));
        }
        else if (prototypeParse.isPresent()) {
            return new DefaultParse(
                    prototypeParse.get().getTouch(),
                    touchCells.map((value) ->  (ImmutableArrayTable<ParsedCell>)new TableBackedImmutableArrayTable<>(value, () -> EmptyParsedCell.INSTANCE))
                            .orElse(prototypeParse.get().allTouchCells()),
                    definitionCells.map((value) ->  (ImmutableArrayTable<ParsedCell>)new TableBackedImmutableArrayTable<>(value, () -> EmptyParsedCell.INSTANCE))
                            .orElse(prototypeParse.get().allDefinitionCells()));
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
        this.touchCells = Optional.of(checkNotNull(touchCells));
        return this;
    }

    public ParseBuilder setDefinitionTableCells(HashBasedTable<Integer, Integer, ParsedCell> definitionCells) {
        this.definitionCells = Optional.of(checkNotNull(definitionCells));
        return this;
    }

}
