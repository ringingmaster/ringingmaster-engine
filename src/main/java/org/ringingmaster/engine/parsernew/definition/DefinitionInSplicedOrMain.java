package org.ringingmaster.engine.parsernew.definition;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Sets;
import com.google.errorprone.annotations.Immutable;
import org.ringingmaster.engine.arraytable.BackingTableLocationAndValue;
import org.ringingmaster.engine.arraytable.ImmutableArrayTable;
import org.ringingmaster.engine.parsernew.Parse;
import org.ringingmaster.engine.parsernew.ParseBuilder;
import org.ringingmaster.engine.parsernew.cell.ParsedCell;
import org.ringingmaster.engine.parsernew.cell.ParsedCellBuilder;
import org.ringingmaster.engine.parsernew.cell.Section;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.ringingmaster.engine.parser.ParseType.DEFINITION;
import static org.ringingmaster.engine.touch.newcontainer.tableaccess.DefinitionTableAccess.DEFINITION_COLUMN;
import static org.ringingmaster.engine.touch.newcontainer.tableaccess.DefinitionTableAccess.SHORTHAND_COLUMN;

/**
 * Enforces that a definition is only useable in spliced or main area. When used in both,
 * both are illegal.
 *
 * @author stevelake
 */
@Immutable
public class DefinitionInSplicedOrMain {

    private final Logger log = LoggerFactory.getLogger(this.getClass());


    public Parse parse(Parse parse) {

        // Step 1: Map out the internal dependencies in the definitions
        Map<String, Set<String>> adjacency = buildDefinitionsAdjacencyList(parse);


        // Step 2: Find usage of definition shorthands in both main and spliced and
        final Set<String> mainBodyDefinitions = new HashSet<>();
        followDefinitions(mainBodyDefinitions, findDefinitionsInUse(parse.mainBodyCells()), adjacency);
        final Set<String> splicedDefinitions = new HashSet<>();
        followDefinitions(splicedDefinitions, findDefinitionsInUse(parse.splicedCells()), adjacency);

        //Step 3: find the problematic definitions
        Set<String> invalidDefinitions = Sets.intersection(mainBodyDefinitions, splicedDefinitions);
        if (invalidDefinitions.size() == 0) {
            return parse;
        }

        // Step 4: Mark invalid
        HashBasedTable<Integer, Integer, ParsedCell> touchTableResult =
                HashBasedTable.create(parse.allTouchCells().getBackingTable());
        markInvalid(parse.mainBodyCells(), invalidDefinitions, touchTableResult);
        markInvalid(parse.splicedCells(), invalidDefinitions, touchTableResult);

        HashBasedTable<Integer, Integer, ParsedCell> definitionTableResult =
                HashBasedTable.create(parse.definitionDefinitionCells().getBackingTable());
        markInvalid(parse.definitionDefinitionCells(), invalidDefinitions, definitionTableResult);

        return new ParseBuilder()
                .prototypeOf(parse)
                .setTouchTableCells(touchTableResult)
                .setDefinitionTableCells(definitionTableResult)
                .build();
    }

    private Map<String, Set<String>> buildDefinitionsAdjacencyList(Parse parse) {
        return parse.getAllDefinitionShorthands().stream()
                .map(parse::findDefinitionByShorthand)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toMap(
                        table -> table.get(0, SHORTHAND_COLUMN).getCharacters().trim(),
                        table -> {
                            final ParsedCell parsedCell = table.get(0, DEFINITION_COLUMN);
                            return  parsedCell.allSections().stream()
                                    .filter(section -> DEFINITION.equals(section.getParseType()))
                                    .map(parsedCell::getCharacters)
                                    .collect(Collectors.toSet());

                        }

                ));
    }

    private void followDefinitions(Set<String> results, Set<String> definitionsToFollow, Map<String, Set<String>> adjacency) {
        for (String definition : definitionsToFollow) {
            if (!results.contains(definition)) {
                results.add(definition);
                final Set<String> dependentDefinition = adjacency.get(definition);
                followDefinitions(results, dependentDefinition, adjacency);

            }
        }
    }


    private Set<String> findDefinitionsInUse(ImmutableArrayTable<ParsedCell> locationAndCells) {

        return StreamSupport.stream(locationAndCells.spliterator(), false)
                .map(BackingTableLocationAndValue::getValue)
                .flatMap(parsedCell -> parsedCell.allSections().stream()
                        .filter(section -> section.getParseType().equals(DEFINITION))
                        .map(parsedCell::getCharacters)
                )
                .collect(Collectors.toSet());
    }

    private void markInvalid(ImmutableArrayTable<ParsedCell> originalCells, Set<String> invalidDefinitions, HashBasedTable<Integer, Integer, ParsedCell> resultCells) {
        for (BackingTableLocationAndValue<ParsedCell> locationAndCell : originalCells) {
            ParsedCell originalCell = locationAndCell.getValue();
            ParsedCellBuilder parsedCellBuilder = new ParsedCellBuilder().prototypeOf(originalCell);

            for (Section section : originalCell.allSections()) {
                String characters = originalCell.getCharacters(section);
                if (invalidDefinitions.contains(characters)) {
                    parsedCellBuilder.setInvalid(originalCell.getGroupForSection(section), "Definition [" + characters + "] should be used in the main body or the splice area, but not both");
                }
            }

            resultCells.put(locationAndCell.getRow(), locationAndCell.getCol(), parsedCellBuilder.build());
        }
    }


}
