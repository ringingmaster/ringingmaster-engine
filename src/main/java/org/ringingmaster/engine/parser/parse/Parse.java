package org.ringingmaster.engine.parser.parse;

import org.ringingmaster.engine.parser.cell.ParsedCell;
import org.ringingmaster.engine.touch.Touch;
import org.ringingmaster.engine.touch.tableaccess.DefinitionTableAccess;
import org.ringingmaster.engine.touch.tableaccess.TouchTableAccess;

import javax.annotation.concurrent.Immutable;

/**
 * TODO comments???
 *
 * @author stevelake
 */
@Immutable
public interface Parse extends TouchTableAccess<ParsedCell>, DefinitionTableAccess<ParsedCell>  {

    Touch getTouch();

}
