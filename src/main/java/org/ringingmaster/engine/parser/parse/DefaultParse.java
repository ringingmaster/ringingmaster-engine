package org.ringingmaster.engine.parser.parse;

import com.google.common.collect.ImmutableSet;
import org.ringingmaster.engine.arraytable.ImmutableArrayTable;
import org.ringingmaster.engine.composition.Composition;
import org.ringingmaster.engine.composition.tableaccess.CompositionTableAccess;
import org.ringingmaster.engine.composition.tableaccess.DefaultCompositionTableAccess;
import org.ringingmaster.engine.composition.tableaccess.DefaultDefinitionTableAccess;
import org.ringingmaster.engine.composition.tableaccess.DefinitionTableAccess;
import org.ringingmaster.engine.parser.cell.ParsedCell;

import javax.annotation.concurrent.Immutable;
import java.util.Optional;
import java.util.Set;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
@Immutable
class DefaultParse implements Parse {

    private final Composition composition;
    private final CompositionTableAccess<ParsedCell> compositionTableAccessDelegate;
    private final DefinitionTableAccess<ParsedCell> definitionTableAccessDelegate;
    private final long startMs;
    private final long endMs;

    DefaultParse(Composition composition, ImmutableArrayTable<ParsedCell> mainTableCells, ImmutableArrayTable<ParsedCell> definitionCells, long startMs, long endMs) {
        this.composition = composition;
        this.compositionTableAccessDelegate = new DefaultCompositionTableAccess<>(mainTableCells, composition.getCompositionType(), composition.isSpliced());
        this.definitionTableAccessDelegate = new DefaultDefinitionTableAccess<>(definitionCells);
        this.startMs = startMs;
        this.endMs = endMs;
    }

    @Override
    public Composition getComposition() {
        return composition;
    }

    @Override
    public long getStartMs() {
        return startMs;
    }

    @Override
    public long getEndMs() {
        return endMs;
    }

    @Override
    public ImmutableArrayTable<ParsedCell> allCompositionCells() {
        return compositionTableAccessDelegate.allCompositionCells();
    }

    @Override
    public ImmutableArrayTable<ParsedCell> mainBodyCells() {
        return compositionTableAccessDelegate.mainBodyCells();
    }

    @Override
    public ImmutableArrayTable<ParsedCell> callingPositionCells() {
        return compositionTableAccessDelegate.callingPositionCells();
    }

    @Override
    public ImmutableArrayTable<ParsedCell> splicedCells() {
        return compositionTableAccessDelegate.splicedCells();
    }

    @Override
    public ImmutableArrayTable<ParsedCell> nullAreaCells() {
        return compositionTableAccessDelegate.nullAreaCells();
    }


    @Override
    public ImmutableArrayTable<ParsedCell> allDefinitionCells() {
        return definitionTableAccessDelegate.allDefinitionCells();
    }

    @Override
    public ImmutableArrayTable<ParsedCell> definitionShorthandCells() {
        return definitionTableAccessDelegate.definitionShorthandCells();
    }

    @Override
    public ImmutableArrayTable<ParsedCell> definitionDefinitionCells() {
        return definitionTableAccessDelegate.definitionDefinitionCells();
    }

    @Override
    public Optional<ImmutableArrayTable<ParsedCell>> findDefinitionByShorthand(String shorthand) {
        return definitionTableAccessDelegate.findDefinitionByShorthand(shorthand);
    }

    @Override
    public Set<ImmutableArrayTable<ParsedCell>> getDefinitionAsTables() {
        return definitionTableAccessDelegate.getDefinitionAsTables();
    }

    @Override
    public ImmutableSet<String> getAllDefinitionShorthands() {
        return definitionTableAccessDelegate.getAllDefinitionShorthands();
    }

    @Override
    public String toString() {
        return "DefaultParse{" +
                "composition=" + composition +
                ", CompositionTableAccess=" + compositionTableAccessDelegate +
                ", definitionTableAccess=" + definitionTableAccessDelegate +
                '}';
    }
}
