package org.ringingmaster.engine.parser.definition;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Sets;
import com.google.errorprone.annotations.Immutable;
import org.pcollections.ConsPStack;
import org.pcollections.PStack;
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
 * Marks any participants in a circular dependency as invalid.
 *
 * @author stevelake
 */
@Immutable
public class CircularDefinition implements Function<Parse, Parse> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final DefinitionFunctions definitionFunctions = new DefinitionFunctions();

    // TODO can we get the dependency chain in the error message? If so the addition of all shorthands for definitions in use will have to change.
    private final Function<String, String> createErrorMessage = (characters) -> "Definition [" + characters + "] forms part of a circular dependency";

    public Parse apply(Parse parse) {

        // Step 1: Map out the internal dependencies in the definitions
        Map<String, Set<String>> adjacency = definitionFunctions.buildDefinitionsAdjacencyList(parse);

        final Sets.SetView<String> definitionsInUse = Sets.union(definitionFunctions.findDefinitionsInUse(parse.mainBodyCells()),
                                                      Sets.union(definitionFunctions.findDefinitionsInUse(parse.splicedCells()),
                                                                 parse.getAllDefinitionShorthands()));

        Set<String> invalidDefinitions = new HashSet<>();
        for (String shorthand : definitionsInUse) {
            discoverCircularity(invalidDefinitions, adjacency, ConsPStack.singleton(shorthand));
        }

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