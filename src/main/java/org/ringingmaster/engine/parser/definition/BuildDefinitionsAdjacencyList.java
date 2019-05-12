package org.ringingmaster.engine.parser.definition;

import com.google.common.collect.ImmutableSet;
import org.ringingmaster.engine.parser.cell.ParsedCell;
import org.ringingmaster.engine.parser.parse.Parse;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.ringingmaster.engine.composition.tableaccess.DefinitionTableAccess.DEFINITION_COLUMN;
import static org.ringingmaster.engine.composition.tableaccess.DefinitionTableAccess.SHORTHAND_COLUMN;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.DEFINITION;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
public class BuildDefinitionsAdjacencyList implements Function<Parse, Map<String, Set<String>>> {

    @Override
    public Map<String, Set<String>> apply(Parse parse) {
        ImmutableSet<String> allDefinitionShorthands = parse.getAllDefinitionShorthands();


        return parse.getAllDefinitionShorthands().stream()
                .map(parse::findDefinitionByShorthand)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(cell -> cell.getColumnSize() == 2)
                .map(table -> {
                    String shorthand = table.get(0, SHORTHAND_COLUMN).getCharacters().trim();
                    final ParsedCell parsedCell = table.get(0, DEFINITION_COLUMN);
                    Set<String> dependencies = parsedCell.allSections().stream()
                            .filter(section -> DEFINITION.equals(section.getParseType()))
                            .map(parsedCell::getCharacters)
                            .collect(Collectors.toSet());
                    return new AbstractMap.SimpleEntry<>(shorthand, dependencies);
                })
                .filter(entry -> !entry.getValue().isEmpty())
                .collect(Collectors.toMap(
                        AbstractMap.SimpleEntry::getKey,
                        AbstractMap.SimpleEntry::getValue));
    }
}
