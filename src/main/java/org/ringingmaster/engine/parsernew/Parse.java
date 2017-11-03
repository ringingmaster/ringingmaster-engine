package org.ringingmaster.engine.parsernew;

import com.google.common.collect.ImmutableList;
import org.ringingmaster.engine.arraytable.ImmutableArrayTable;
import org.ringingmaster.engine.parsernew.cell.ParsedCell;
import org.ringingmaster.engine.parsernew.cell.ParsedDefinitionCell;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public interface Parse {

    ImmutableArrayTable<ParsedCell> getCells();

    ImmutableList<ParsedDefinitionCell> getParsedDefinitions();
}
