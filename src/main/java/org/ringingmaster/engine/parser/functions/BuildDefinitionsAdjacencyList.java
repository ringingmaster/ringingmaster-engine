package org.ringingmaster.engine.parser.functions;

import org.ringingmaster.engine.parser.parse.Parse;
import org.ringingmaster.engine.parser.cell.ParsedCell;
import org.ringingmaster.engine.touch.tableaccess.DefinitionTableAccess;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.ringingmaster.engine.parser.assignparsetype.ParseType.DEFINITION;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class BuildDefinitionsAdjacencyList implements Function<Parse, Map<String, Set<String>>> {

    @Override
    public Map<String, Set<String>> apply(Parse parse) {
        return parse.getAllDefinitionShorthands().stream()
                .map(parse::findDefinitionByShorthand)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toMap(
                        table -> table.get(0, DefinitionTableAccess.SHORTHAND_COLUMN).getCharacters().trim(),
                        table -> {
                            final ParsedCell parsedCell = table.get(0, DefinitionTableAccess.DEFINITION_COLUMN);
                            return parsedCell.allSections().stream()
                                    .filter(section -> DEFINITION.equals(section.getParseType()))
                                    .map(parsedCell::getCharacters)
                                    .collect(Collectors.toSet());

                        }

                ));
    }
}
