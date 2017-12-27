package org.ringingmaster.engine.touch.container.tableaccess;

import com.google.common.collect.ImmutableSet;
import org.ringingmaster.engine.arraytable.ImmutableArrayTable;
import org.ringingmaster.engine.touch.container.cell.Cell;

import java.util.Optional;

/**
 * TODO comments???
 *
 * @author stevelake
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
     * @return whole row. i.e. up to two cells.
     */
    Optional<ImmutableArrayTable<T>> findDefinitionByShorthand(String shorthand);


    public ImmutableSet<String> getAllDefinitionShorthands();

}
