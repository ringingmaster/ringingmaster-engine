package org.ringingmaster.engine.parser;

import org.ringingmaster.engine.parser.cell.ParsedCell;
import org.ringingmaster.engine.touch.newcontainer.Touch;
import org.ringingmaster.engine.touch.newcontainer.tableaccess.DefinitionTableAccess;
import org.ringingmaster.engine.touch.newcontainer.tableaccess.TouchTableAccess;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public interface Parse extends TouchTableAccess<ParsedCell>, DefinitionTableAccess<ParsedCell>  {

    Touch getTouch();

}
