package org.ringingmaster.engine.parser.functions;

import org.ringingmaster.engine.arraytable.BackingTableLocationAndValue;
import org.ringingmaster.engine.arraytable.ImmutableArrayTable;
import org.ringingmaster.engine.parser.assignparsetype.ParseType;
import org.ringingmaster.engine.parser.cell.ParsedCell;

import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class InUseNamesForParseType implements BiFunction<ImmutableArrayTable<ParsedCell>, ParseType, Set<String>> {

    @Override
    public Set<String> apply(ImmutableArrayTable<ParsedCell> locationAndCells, ParseType parseType) {

        return StreamSupport.stream(locationAndCells.spliterator(), false)
                .map(BackingTableLocationAndValue::getValue)
                .flatMap(parsedCell -> parsedCell.allSections().stream()
                        .filter(section -> section.getParseType().equals(parseType))
                        .map(parsedCell::getCharacters)
                )
                .collect(Collectors.toSet());
    }

}
