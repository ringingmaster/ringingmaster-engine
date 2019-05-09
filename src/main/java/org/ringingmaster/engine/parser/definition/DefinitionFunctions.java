package org.ringingmaster.engine.parser.definition;

import com.google.common.collect.HashBasedTable;
import org.ringingmaster.engine.arraytable.BackingTableLocationAndValue;
import org.ringingmaster.engine.arraytable.ImmutableArrayTable;
import org.ringingmaster.engine.parser.cell.ParsedCell;
import org.ringingmaster.engine.parser.cell.grouping.Section;
import org.ringingmaster.engine.parser.cell.mutator.ParsedCellMutator;
import org.ringingmaster.engine.parser.parse.Parse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.function.Function;

public class DefinitionFunctions {

    private static final Logger log = LoggerFactory.getLogger(DefinitionFunctions.class);

    static HashBasedTable<Integer, Integer, ParsedCell> markDefinitionsInvalidInComposition(Parse parse, Set<String> invalidDefinitions,  Function<String, String> createErrorMessage) {

        log.info("Invalidating definitions in composition{}", invalidDefinitions);

        HashBasedTable<Integer, Integer, ParsedCell> compositionTableResult =
                HashBasedTable.create(parse.allCompositionCells().getBackingTable());
        markInvalid(parse.allCompositionCells(), invalidDefinitions, compositionTableResult, createErrorMessage);

        return compositionTableResult;
    }

    static HashBasedTable<Integer, Integer, ParsedCell> markDefinitionsInvalidInDefinitions(Parse parse, Set<String> invalidDefinitions,  Function<String, String> createErrorMessage) {

        log.info("Invalidating definitions in definition{}", invalidDefinitions);

        HashBasedTable<Integer, Integer, ParsedCell> definitionTableResult =
                HashBasedTable.create(parse.allDefinitionCells().getBackingTable());
        markInvalid(parse.allDefinitionCells(), invalidDefinitions, definitionTableResult, createErrorMessage);

        return definitionTableResult;
    }

    static void markInvalid(ImmutableArrayTable<ParsedCell> originalCells, Set<String> invalidDefinitions, HashBasedTable<Integer, Integer, ParsedCell> resultCells, Function<String, String> createErrorMessage) {
        for (BackingTableLocationAndValue<ParsedCell> locationAndCell : originalCells) {
            ParsedCell originalCell = locationAndCell.getValue();
            ParsedCellMutator parsedCellMutator = new ParsedCellMutator().prototypeOf(originalCell);

            for (Section section : originalCell.allSections()) {
                String characters = originalCell.getCharacters(section);
                if (invalidDefinitions.contains(characters)) {
                    String message = createErrorMessage.apply(characters);
                    parsedCellMutator.invalidateGroup(section.getStartIndex(), message);
                }
            }

            resultCells.put(locationAndCell.getRow(), locationAndCell.getCol(), parsedCellMutator.build());
        }
    }
}