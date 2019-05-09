package org.ringingmaster.engine.composition.tableaccess;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import org.ringingmaster.engine.arraytable.ImmutableArrayTable;
import org.ringingmaster.engine.composition.cell.Cell;

import javax.annotation.concurrent.Immutable;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
@Immutable
public class DefaultDefinitionTableAccess<T extends Cell> implements DefinitionTableAccess<T> {

    private final ImmutableArrayTable<T> cells;
    private final ImmutableMap<String, ImmutableArrayTable<T>> byShorthand;
    private final ImmutableSet<ImmutableArrayTable<T>> rows;

    public DefaultDefinitionTableAccess(ImmutableArrayTable<T> cells) {
        this.cells = checkNotNull(cells);
        checkState(cells.getColumnSize() <= 2, "Maximum two columns are allowed in definition table.");

        final Map<String, ImmutableArrayTable<T>> byShorthandBuilder = Maps.newHashMap(); //Not using an ImmutableMap Builder because it cannot handle duplicate entries.
        ImmutableSet.Builder<ImmutableArrayTable<T>> rowsBuilder = ImmutableSet.builder();

        for (int rowIndex=0;rowIndex<cells.getRowSize();rowIndex++) {
            final ImmutableArrayTable<T> singleRow = cells.subTable(rowIndex, rowIndex + 1, 0, Math.min(cells.getColumnSize(),2));
            rowsBuilder.add(singleRow);
            final String shorthand = singleRow.get(0, SHORTHAND_COLUMN).getCharacters().trim();

            // Shorthand can be empty when values are added to the definition-definition column before the shorthand column.
            if (!shorthand.isEmpty()) {
                byShorthandBuilder.put(shorthand, singleRow);
            }
        }
        this.byShorthand = ImmutableMap.copyOf(byShorthandBuilder);
        this.rows = rowsBuilder.build();
    }

    @Override
    public ImmutableArrayTable<T> allDefinitionCells() {
        return cells;
    }

    @Override
    public ImmutableArrayTable<T> definitionShorthandCells() {
        if (cells.getColumnSize() < 1 || cells.getRowSize() == 0 ) {
            return cells.subTable(0, 0, 0, 0);
        }

        return cells.subTable(0, cells.getRowSize(), SHORTHAND_COLUMN, SHORTHAND_COLUMN + 1);
    }

    @Override
    public ImmutableArrayTable<T> definitionDefinitionCells() {
        if (cells.getColumnSize() < 2 || cells.getRowSize() == 0 ) {
            return cells.subTable(0, 0, 0, 0);
        }

        return cells.subTable(0, cells.getRowSize(), DEFINITION_COLUMN, DEFINITION_COLUMN + 1);
    }

    @Override
    public Optional<ImmutableArrayTable<T>> findDefinitionByShorthand(String shorthand) {
        checkNotNull(shorthand);
        return Optional.ofNullable(byShorthand.get(shorthand));
    }

    @Override
    public Set<ImmutableArrayTable<T>> getDefinitionAsTables() {
        return rows;
    }

    public ImmutableSet<String> getAllDefinitionShorthands() {
        return byShorthand.keySet();
    }

    @Override
    public String toString() {
        return "DefaultDefinitionTableAccess{" +
                "cells=" + cells +
                '}';
    }
}
