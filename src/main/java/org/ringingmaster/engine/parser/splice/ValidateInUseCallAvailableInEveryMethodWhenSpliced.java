package org.ringingmaster.engine.parser.splice;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Sets;
import org.pcollections.PSet;
import org.ringingmaster.engine.arraytable.BackingTableLocationAndValue;
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.parser.assignparsetype.ParseType;
import org.ringingmaster.engine.parser.cell.ParsedCell;
import org.ringingmaster.engine.parser.cell.mutator.ParsedCellMutator;
import org.ringingmaster.engine.parser.cell.grouping.Section;
import org.ringingmaster.engine.parser.functions.InUseNamesForParseType;
import org.ringingmaster.engine.parser.parse.Parse;
import org.ringingmaster.engine.parser.parse.ParseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.ringingmaster.engine.parser.assignparsetype.ParseType.SPLICE;

/**
 * Validates that any call that is actually in use is available in every method in spliced
 *
 * @author stevelake
 */
public class ValidateInUseCallAvailableInEveryMethodWhenSpliced implements Function<Parse, Parse> {

    private final Logger log = LoggerFactory.getLogger(ValidateInUseCallAvailableInEveryMethodWhenSpliced.class);

    @Override
    public Parse apply(Parse parse) {
        log.debug("[{}] > validate in use calls available in every method when spliced", parse.getComposition().getTitle());
        Parse response = doCheck(parse);
        log.debug("[{}] < validate in use calls available in every method when spliced", parse.getComposition().getTitle());
        return response;
    }

    private Parse doCheck(Parse input) {
        if (! input.getComposition().isSpliced()) {
            log.debug("[{}]  ignore check: not spliced", input.getComposition().getTitle());
            return input;
        }

        final PSet<NotationBody> validNotationsInComposition =  input.getComposition().getAvailableNotations();
        if (validNotationsInComposition.size() == 0) {
            // We don't need to do any invalidating because there will have been no parsing of calls going on.
            return input;
        }
        if (log.isDebugEnabled()) {
            log.debug("[{}]  valid notations available in composition {}", input.getComposition().getTitle(),
                    validNotationsInComposition.stream().map(NotationBody::getNameIncludingNumberOfBells).collect(Collectors.toSet()));
        }

        final Set<String> spliceNamesInUse = Sets.union(new InUseNamesForParseType().apply(input.splicedCells(), SPLICE),
                                                   new InUseNamesForParseType().apply(input.definitionDefinitionCells(), SPLICE));
        log.debug("[{}]  splice Names in use {}", input.getComposition().getTitle(),
                spliceNamesInUse)  ;


        final Set<NotationBody> notationsInUse = validNotationsInComposition.stream()
                .filter(notation -> spliceNamesInUse.contains(notation.getSpliceIdentifier()))
                .collect(Collectors.toSet());
        if (log.isDebugEnabled()) {
            log.debug("[{}]  notations in use {}", input.getComposition().getTitle(),
                    notationsInUse.stream().map(NotationBody::getNameIncludingNumberOfBells).collect(Collectors.toSet()));
        }

        final Set<String> availableCallsFromValidNotations = getAllCalls(validNotationsInComposition);
        log.debug("[{}]  calls available {}", input.getComposition().getTitle(),
                spliceNamesInUse)  ;

        final Set<String> commonCalls = getCommonCallsFromNotationsInUse(notationsInUse);
        log.debug("[{}]  calls that are defined in every valid notation {}", input.getComposition().getTitle(),
                commonCalls)  ;

        final Set<String> invalidCalls = Sets.difference(availableCallsFromValidNotations, commonCalls);
        log.debug("[{}]  calls that cant be used {}", input.getComposition().getTitle(),
                invalidCalls)  ;

        if (invalidCalls.size() == 0) {

            return input;
        }

        HashBasedTable<Integer, Integer, ParsedCell> compositionCells =
                HashBasedTable.create(input.allCompositionCells().getBackingTable());

        for (BackingTableLocationAndValue<ParsedCell> locationAndCell : input.mainBodyCells()) {
            doInvalidation(invalidCalls, compositionCells, locationAndCell);
        }

         HashBasedTable<Integer, Integer, ParsedCell> definitionCells =
                HashBasedTable.create(input.allDefinitionCells().getBackingTable());
        for (BackingTableLocationAndValue<ParsedCell> locationAndCell : input.definitionDefinitionCells()) {
            doInvalidation(invalidCalls, definitionCells, locationAndCell);
        }

        return new ParseBuilder()
                .prototypeOf(input)
                .setCompositionTableCells(compositionCells)
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
                builder.invalidateGroup(section.getStartIndex(), "The call " + cell.getCharacters(section) + " is not defined in all methods");
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

    private Set<String> getCommonCallsFromNotationsInUse(Set<NotationBody> inUseNotations) {
        if (inUseNotations.size() == 0) {
            return Collections.emptySet();
        }

        Set<String> commonCalls = null;

        for (NotationBody notation : inUseNotations) {
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
