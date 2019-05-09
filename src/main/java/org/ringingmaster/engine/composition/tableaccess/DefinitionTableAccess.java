package org.ringingmaster.engine.composition.tableaccess;

import com.google.common.collect.ImmutableSet;
import org.ringingmaster.engine.arraytable.ImmutableArrayTable;
import org.ringingmaster.engine.composition.cell.Cell;

import java.util.Optional;
import java.util.Set;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
public interface DefinitionTableAccess<T extends Cell> {

    int SHORTHAND_COLUMN = 0;
    int DEFINITION_COLUMN = 1;

    ImmutableArrayTable<T> allDefinitionCells();

    /**
     * @return Single column of cells.
     */
    ImmutableArrayTable<T> definitionShorthandCells();

    /**
     * @return Single column of cells.
     */
    ImmutableArrayTable<T> definitionDefinitionCells();

    /**
     * @return whole row as a sub table. i.e. up to two cells.
     */
    Optional<ImmutableArrayTable<T>> findDefinitionByShorthand(String shorthand);

    /**
     * @return Set of whole row's as a sub table. i.e. up to two cells per table.
     */
    Set<ImmutableArrayTable<T>> getDefinitionAsTables();


     ImmutableSet<String> getAllDefinitionShorthands();

}
