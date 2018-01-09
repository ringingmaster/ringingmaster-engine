package org.ringingmaster.engine.parser.definition;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Sets;
import com.google.errorprone.annotations.Immutable;
import org.ringingmaster.engine.parser.Parse;
import org.ringingmaster.engine.parser.ParseBuilder;
import org.ringingmaster.engine.parser.cell.ParsedCell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Marks as invalid any definitions that are used in both spliced or main area.
 * A dependency is considered part of splice or main when it is directly used, or used transitively.
 *
 * @author stevelake
 */
@Immutable
public class DefinitionInSplicedOrMain implements Function<Parse, Parse> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final DefinitionFunctions definitionFunctions = new DefinitionFunctions();

    // TODO can we get the dependency chain in the error message?
    private final Function<String, String> createErrorMessage = (characters) -> "Definition [" + characters + "] should be used in the main body or the splice area, but not both";


    public Parse apply(Parse parse) {

        // Step 1: Map out the internal dependencies in the definitions
        Map<String, Set<String>> adjacency = definitionFunctions.buildDefinitionsAdjacencyList(parse);

        // Step 2: Find usage of definition shorthands in both main and spliced and
        final Set<String> mainBodyDefinitions = new HashSet<>();
        definitionFunctions.followDefinitions(mainBodyDefinitions, definitionFunctions.findDefinitionsInUse(parse.mainBodyCells()), adjacency);
        final Set<String> splicedDefinitions = new HashSet<>();
        definitionFunctions.followDefinitions(splicedDefinitions, definitionFunctions.findDefinitionsInUse(parse.splicedCells()), adjacency);

        //Step 3: find the problematic definitions
        Set<String> invalidDefinitions = Sets.intersection(mainBodyDefinitions, splicedDefinitions);
        if (invalidDefinitions.size() == 0) {
            return parse;
        }

        // Step 4: Mark invalid
        HashBasedTable<Integer, Integer, ParsedCell> touchTableResult =
                HashBasedTable.create(parse.allTouchCells().getBackingTable());
        definitionFunctions.markInvalid(parse.mainBodyCells(), invalidDefinitions, touchTableResult, createErrorMessage);
        definitionFunctions.markInvalid(parse.splicedCells(), invalidDefinitions, touchTableResult, createErrorMessage);

        HashBasedTable<Integer, Integer, ParsedCell> definitionTableResult =
                HashBasedTable.create(parse.definitionDefinitionCells().getBackingTable());
        definitionFunctions.markInvalid(parse.definitionDefinitionCells(), invalidDefinitions, definitionTableResult, createErrorMessage);

        return new ParseBuilder()
                .prototypeOf(parse)
                .setTouchTableCells(touchTableResult)
                .setDefinitionTableCells(definitionTableResult)
                .build();
    }

}
