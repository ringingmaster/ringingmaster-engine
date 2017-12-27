package org.ringingmaster.engine.touch.tableaccess;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.ringingmaster.engine.arraytable.ImmutableArrayTable;
import org.ringingmaster.engine.touch.cell.Cell;

import javax.annotation.concurrent.Immutable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * TODO comments???
 *
 * @author stevelake
 */
@Immutable
public class DefaultDefinitionTableAccess<T extends Cell> implements DefinitionTableAccess<T> {

    private final ImmutableArrayTable<T> cells;
    private final ImmutableMap<String, ImmutableArrayTable<T>> byShorthand;

    public DefaultDefinitionTableAccess(ImmutableArrayTable<T> cells) {
        this.cells = checkNotNull(cells);
        checkState(cells.getColumnSize() <= 2, "Maximum two columns are allowed in definition table.");

        final Map<String, ImmutableArrayTable<T>> byShorthand = new HashMap<>();
        for (int rowIndex=0;rowIndex<cells.getRowSize();rowIndex++) {
            final ImmutableArrayTable<T> singleRow = cells.subTable(rowIndex, rowIndex + 1, 0, Math.min(cells.getColumnSize(),2));
            final String shorthand = singleRow.get(0, SHORTHAND_COLUMN).getCharacters().trim();
            byShorthand.put(shorthand, singleRow);
        }
        this.byShorthand = ImmutableMap.copyOf(byShorthand);
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