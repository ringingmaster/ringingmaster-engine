package org.ringingmaster.engine.touch.newcontainer.tableaccess;

import org.ringingmaster.engine.arraytable.ImmutableArrayTable;
import org.ringingmaster.engine.touch.newcontainer.cell.Cell;

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
    ImmutableArrayTable<T> allShorthands();
    Optional<ImmutableArrayTable<T>> findDefinitionByShorthand(String shorthand);
}
