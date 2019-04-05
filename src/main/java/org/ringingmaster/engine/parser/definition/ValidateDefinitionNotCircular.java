package org.ringingmaster.engine.parser.definition;

import com.google.common.collect.HashBasedTable;
import com.google.errorprone.annotations.Immutable;
import org.pcollections.ConsPStack;
import org.pcollections.PStack;
import org.ringingmaster.engine.parser.parse.Parse;
import org.ringingmaster.engine.parser.parse.ParseBuilder;
import org.ringingmaster.engine.parser.cell.ParsedCell;
import org.ringingmaster.engine.parser.functions.BuildDefinitionsAdjacencyList;
import org.ringingmaster.engine.parser.functions.DefinitionFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Marks any participants in a circular dependency as invalid.
 *
 * @author stevelake
 */
@Immutable
public class ValidateDefinitionNotCircular implements Function<Parse, Parse> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final DefinitionFunctions definitionFunctions = new DefinitionFunctions();

    // TODO can we get the dependency chain in the error message? If so the addition of all shorthands for definitions in use will have to change.
    private final Function<String, String> createErrorMessage = (characters) -> "Definition [" + characters + "] forms part of a circular dependency";

    public Parse apply(Parse input) {

        log.debug("[{}] > circular definition check", input.getUnderlyingTouch().getTitle());

        Map<String, Set<String>> adjacency = new BuildDefinitionsAdjacencyList().apply(input);

        Set<String> invalidDefinitions = new HashSet<>();
        for (String shorthand : input.getAllDefinitionShorthands()) {
            discoverCircularity(invalidDefinitions, adjacency, ConsPStack.singleton(shorthand));
        }

        HashBasedTable<Integer, Integer, ParsedCell> touchTableResult =
                HashBasedTable.create(input.allTouchCells().getBackingTable());
        definitionFunctions.markInvalid(input.mainBodyCells(), invalidDefinitions, touchTableResult, createErrorMessage);
        definitionFunctions.markInvalid(input.splicedCells(), invalidDefinitions, touchTableResult, createErrorMessage);

        HashBasedTable<Integer, Integer, ParsedCell> definitionTableResult =
                HashBasedTable.create(input.definitionDefinitionCells().getBackingTable());
        definitionFunctions.markInvalid(input.definitionDefinitionCells(), invalidDefinitions, definitionTableResult, createErrorMessage);


        Parse result = new ParseBuilder()
                .prototypeOf(input)
                .setTouchTableCells(touchTableResult)
                .setDefinitionTableCells(definitionTableResult)
                .build();

        log.debug("[{}] < circular definition check", input.getUnderlyingTouch().getTitle());

        return result;
    }

    private void discoverCircularity(Set<String> results, Map<String, Set<String>> adjacency, PStack<String> path) {
        String shorthand = path.get(0);
        final Set<String> dependencies = adjacency.get(shorthand);

        for (String dependency : dependencies) {
            if (path.contains(dependency)) {
                // we are declaring circularity at this point, and delve no further
                results.addAll(path);
            } else {
                discoverCircularity(results, adjacency, path.plus(dependency));
            }
        }
    }

}