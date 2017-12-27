package org.ringingmaster.engine.parser;

import org.ringingmaster.engine.parser.cell.ParsedCell;
import org.ringingmaster.engine.touch.container.Touch;
import org.ringingmaster.engine.touch.container.tableaccess.DefinitionTableAccess;
import org.ringingmaster.engine.touch.container.tableaccess.TouchTableAccess;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public interface Parse extends TouchTableAccess<ParsedCell>, DefinitionTableAccess<ParsedCell>  {

    Touch getTouch();

}
