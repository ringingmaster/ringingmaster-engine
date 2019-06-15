package org.ringingmaster.engine.parser.parse;

import com.google.common.collect.HashBasedTable;
import org.ringingmaster.engine.arraytable.ImmutableArrayTable;
import org.ringingmaster.engine.arraytable.TableBackedImmutableArrayTable;
import org.ringingmaster.engine.composition.Composition;
import org.ringingmaster.engine.parser.cell.EmptyParsedCell;
import org.ringingmaster.engine.parser.cell.ParsedCell;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
public class ParseBuilder {

    private Optional<Composition> prototypeComposition = Optional.empty();
    private Optional<Parse> prototypeParse = Optional.empty();
    private Optional<HashBasedTable<Integer, Integer, ParsedCell>> compositionCells = Optional.empty();;
    private Optional<HashBasedTable<Integer, Integer, ParsedCell>> definitionCells = Optional.empty();;
    private Optional<Long> startMs = Optional.empty();
    private Optional<Long> endMs = Optional.empty();

    public Parse build() {

        if (prototypeComposition.isPresent()) {
            checkState(compositionCells.isPresent());
            checkState(definitionCells.isPresent());

            return new DefaultParse(
                    prototypeComposition.get(),
                    new TableBackedImmutableArrayTable<>(compositionCells.get(), () -> EmptyParsedCell.INSTANCE),
                    new TableBackedImmutableArrayTable<>(definitionCells.get(), () -> EmptyParsedCell.INSTANCE),
                    startMs.orElse(0L),
                    endMs.orElse(0L));
        }
        else if (prototypeParse.isPresent()) {
            return new DefaultParse(
                    prototypeParse.get().getComposition(),
                    compositionCells.map((value) ->  (ImmutableArrayTable<ParsedCell>)new TableBackedImmutableArrayTable<>(value, () -> EmptyParsedCell.INSTANCE))
                            .orElse(prototypeParse.get().allCompositionCells()),
                    definitionCells.map((value) ->  (ImmutableArrayTable<ParsedCell>)new TableBackedImmutableArrayTable<>(value, () -> EmptyParsedCell.INSTANCE))
                            .orElse(prototypeParse.get().allDefinitionCells()),
                    startMs.orElse(prototypeParse.get().getStartMs()),
                    endMs.orElse(prototypeParse.get().getEndMs()));
        }
        else {
            throw new IllegalStateException();
        }
    }

    public ParseBuilder prototypeOf(Composition prototypeComposition) {
        checkState(!prototypeParse.isPresent());
        this.prototypeComposition = Optional.of(checkNotNull(prototypeComposition));
        return this;
    }

    public ParseBuilder prototypeOf(Parse prototypeParse) {
        checkState(!prototypeComposition.isPresent());
        this.prototypeParse = Optional.of(checkNotNull(prototypeParse));
        return this;
    }

    public ParseBuilder setCompositionTableCells(HashBasedTable<Integer, Integer, ParsedCell> compositionCells) {
        this.compositionCells = Optional.of(checkNotNull(compositionCells));
        return this;
    }

    public ParseBuilder setDefinitionTableCells(HashBasedTable<Integer, Integer, ParsedCell> definitionCells) {
        this.definitionCells = Optional.of(checkNotNull(definitionCells));
        return this;
    }

    public ParseBuilder setStartMs(long startMs) {
        this.startMs = Optional.of(startMs);
        return this;
    }

    public ParseBuilder setEndMs(long endMs) {
        this.endMs = Optional.of(endMs);
        return this;
    }
}
