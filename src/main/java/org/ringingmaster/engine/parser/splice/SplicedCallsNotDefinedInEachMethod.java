package org.ringingmaster.engine.parser.splice;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Sets;
import org.pcollections.PSet;
import org.ringingmaster.engine.arraytable.BackingTableLocationAndValue;
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.parser.Parse;
import org.ringingmaster.engine.parser.ParseBuilder;
import org.ringingmaster.engine.parser.ParseType;
import org.ringingmaster.engine.parser.cell.ParsedCell;
import org.ringingmaster.engine.parser.cell.ParsedCellMutator;
import org.ringingmaster.engine.parser.cell.Section;
import org.ringingmaster.engine.parser.functions.InUseNamesForParseType;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.ringingmaster.engine.parser.ParseType.SPLICE;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class SplicedCallsNotDefinedInEachMethod implements Function<Parse, Parse> {

    @Override
    public Parse apply(Parse parse) {
        if (! parse.getTouch().isSpliced()) {
            return parse;
        }

        final PSet<NotationBody> allNotations =  parse.getTouch().getAvailableNotations();
        if (allNotations.size() == 0) {
            // We don't need to do any invalidating because there will have been no parsing of calls going on.
            return parse;
        }

        final Set<String> spliceNames = Sets.union(new InUseNamesForParseType().apply(parse.splicedCells(), SPLICE),
                                                   new InUseNamesForParseType().apply(parse.definitionDefinitionCells(), SPLICE));

        final Set<NotationBody> inUseNotations = allNotations.stream()
                .filter(notation -> spliceNames.contains(notation.getSpliceIdentifier()))
                .collect(Collectors.toSet());

        final Set<String> allCalls = getAllCalls(allNotations);
        final Set<String> commonCalls = getCommonCallsFromInUseNotations(inUseNotations);
        final Set<String> invalidCalls = Sets.difference(allCalls, commonCalls);

        if (invalidCalls.size() == 0) {
            return parse;
        }

        HashBasedTable<Integer, Integer, ParsedCell> touchCells =
                HashBasedTable.create(parse.allTouchCells().getBackingTable());

        for (BackingTableLocationAndValue<ParsedCell> locationAndCell : parse.mainBodyCells()) {
            doInvalidation(invalidCalls, touchCells, locationAndCell);
        }

         HashBasedTable<Integer, Integer, ParsedCell> definitionCells =
                HashBasedTable.create(parse.allDefinitionCells().getBackingTable());
        for (BackingTableLocationAndValue<ParsedCell> locationAndCell : parse.definitionDefinitionCells()) {
            doInvalidation(invalidCalls, definitionCells, locationAndCell);
        }

        return new ParseBuilder()
                .prototypeOf(parse)
                .setTouchTableCells(touchCells)
                .setDefinitionTableCells(definitionCells)
                .build();
    }

    private void doInvalidation(Set<String> invalidCalls, HashBasedTable<Integer, Integer, ParsedCell> cells, BackingTableLocationAndValue<ParsedCell> locationAndCell) {
        final ParsedCell cell = locationAndCell.getValue();

        ParsedCellMutator builder = new ParsedCellMutator()
                .prototypeOf(cell);

        for (Section section : cell.allSections()) {
            if (section.getParseType() == ParseType.CALL &&
                    invalidCalls.contains(cell.getCharacters(section))) {
                builder.invalidateGroup(section.getElementStartIndex(), "The call " + cell.getCharacters(section) + " is not defined in all methods");
            }
        }

        cells.put(locationAndCell.getRow(), locationAndCell.getCol(), builder.build());
    }

    private Set<String> getAllCalls(Set<NotationBody> notations) {
        return notations.stream()
                .map(NotationBody::getCalls)
                .flatMap(Collection::stream)
                .flatMap(p -> Stream.of(p.getNameShorthand(), p.getName()))
                .collect(Collectors.toSet());
    }

    private Set<String> getCommonCallsFromInUseNotations(Set<NotationBody> allNotations) {
        if (allNotations.size() == 0) {
            return Collections.emptySet();
        }

        Set<String> commonCalls = null;

        for (NotationBody notation : allNotations) {
            if (commonCalls == null) {
                commonCalls = getCalls(notation);
            }
            else {
                commonCalls = Sets.intersection(commonCalls, getCalls(notation));
            }
        }

        return commonCalls;
    }

    private Set<String> getCalls(NotationBody notation) {
        return notation.getCalls().stream()
                .flatMap(call -> Stream.of(call.getNameShorthand(), call.getName()))
                .collect(Collectors.toSet());
    }

}
