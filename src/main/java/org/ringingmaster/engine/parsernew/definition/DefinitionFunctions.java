package org.ringingmaster.engine.parsernew.definition;

import com.google.common.collect.HashBasedTable;
import org.ringingmaster.engine.arraytable.BackingTableLocationAndValue;
import org.ringingmaster.engine.arraytable.ImmutableArrayTable;
import org.ringingmaster.engine.parsernew.Parse;
import org.ringingmaster.engine.parsernew.cell.ParsedCell;
import org.ringingmaster.engine.parsernew.cell.ParsedCellBuilder;
import org.ringingmaster.engine.parsernew.cell.Section;
import org.ringingmaster.engine.touch.newcontainer.tableaccess.DefinitionTableAccess;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.ringingmaster.engine.parser.ParseType.DEFINITION;

public class DefinitionFunctions {

    Map<String, Set<String>> buildDefinitionsAdjacencyList(Parse parse) {
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

    Set<String> findDefinitionsInUse(ImmutableArrayTable<ParsedCell> locationAndCells) {

        return StreamSupport.stream(locationAndCells.spliterator(), false)
                .map(BackingTableLocationAndValue::getValue)
                .flatMap(parsedCell -> parsedCell.allSections().stream()
                        .filter(section -> section.getParseType().equals(DEFINITION))
                        .map(parsedCell::getCharacters)
                )
                .collect(Collectors.toSet());
    }

    void markInvalid(ImmutableArrayTable<ParsedCell> originalCells, Set<String> invalidDefinitions, HashBasedTable<Integer, Integer, ParsedCell> resultCells, Function<String, String> createErrorMessage) {
        for (BackingTableLocationAndValue<ParsedCell> locationAndCell : originalCells) {
            ParsedCell originalCell = locationAndCell.getValue();
            ParsedCellBuilder parsedCellBuilder = new ParsedCellBuilder().prototypeOf(originalCell);

            for (Section section : originalCell.allSections()) {
                String characters = originalCell.getCharacters(section);
                if (invalidDefinitions.contains(characters)) {
                    String message = createErrorMessage.apply(characters);
                    parsedCellBuilder.setInvalid(originalCell.getGroupForSection(section), message);
                }
            }

            resultCells.put(locationAndCell.getRow(), locationAndCell.getCol(), parsedCellBuilder.build());
        }
    }
}