package org.ringingmaster.engine.parser.parse;

import org.ringingmaster.engine.parser.cell.ParsedCell;
import org.ringingmaster.engine.touch.Touch;
import org.ringingmaster.engine.touch.tableaccess.DefinitionTableAccess;
import org.ringingmaster.engine.touch.tableaccess.TouchTableAccess;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public interface Parse extends TouchTableAccess<ParsedCell>, DefinitionTableAccess<ParsedCell>  {

    Touch getUnderlyingTouch();

}
