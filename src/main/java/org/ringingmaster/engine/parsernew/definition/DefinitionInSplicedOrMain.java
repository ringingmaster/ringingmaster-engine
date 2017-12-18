package org.ringingmaster.engine.parsernew.definition;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Sets;
import com.google.errorprone.annotations.Immutable;
import org.ringingmaster.engine.arraytable.BackingTableLocationAndValue;
import org.ringingmaster.engine.arraytable.ImmutableArrayTable;
import org.ringingmaster.engine.parser.ParseType;
import org.ringingmaster.engine.parsernew.Parse;
import org.ringingmaster.engine.parsernew.ParseBuilder;
import org.ringingmaster.engine.parsernew.cell.ParsedCell;
import org.ringingmaster.engine.parsernew.cell.ParsedCellBuilder;
import org.ringingmaster.engine.parsernew.cell.Section;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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

        // First Pass to find usage of definition shorthands
        Set<String> mainBodyDefinitions = findDefinitionsInUse(parse.mainBodyCells());
        Set<String> splicedDefinitions = findDefinitionsInUse(parse.splicedCells());

        Set<String> invalidDefinitions = Sets.intersection(mainBodyDefinitions, splicedDefinitions);

        if (invalidDefinitions.size() == 0) {
            return parse;
        }

        // Second Pass to mark invalid
        HashBasedTable<Integer, Integer, ParsedCell> resultCells =
                HashBasedTable.create(parse.allTouchCells().getBackingTable());

        markInvalid(parse.mainBodyCells(), invalidDefinitions, resultCells);
        markInvalid(parse.splicedCells(), invalidDefinitions, resultCells);
        markInvalid(parse.definitionDefinitionCells(), invalidDefinitions, resultCells);

        return new ParseBuilder()
                .prototypeOf(parse)
                .setTouchTableCells(resultCells)
                .build();
    }

    private Set<String> findDefinitionsInUse(ImmutableArrayTable<ParsedCell> locationAndCells) {

        return StreamSupport.stream(locationAndCells.spliterator(), false)
                .map(BackingTableLocationAndValue::getValue)
                .flatMap(parsedCell -> parsedCell.allSections().stream()
                        .filter(section -> section.getParseType().equals(ParseType.DEFINITION))
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
