package org.ringingmaster.engine.parser.parse;

import org.ringingmaster.engine.parser.cell.ParsedCell;
import org.ringingmaster.engine.composition.Composition;
import org.ringingmaster.engine.composition.tableaccess.DefinitionTableAccess;
import org.ringingmaster.engine.composition.tableaccess.CompositionTableAccess;

import javax.annotation.concurrent.Immutable;

/**
 * TODO comments???
 *
 * @author stevelake
 */
@Immutable
public interface Parse extends CompositionTableAccess<ParsedCell>, DefinitionTableAccess<ParsedCell>  {

    Composition getComposition();

}
