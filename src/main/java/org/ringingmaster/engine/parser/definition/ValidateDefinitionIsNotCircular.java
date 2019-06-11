package org.ringingmaster.engine.parser.definition;

import com.google.common.collect.HashBasedTable;
import com.google.errorprone.annotations.Immutable;
import org.pcollections.ConsPStack;
import org.pcollections.PStack;
import org.ringingmaster.engine.parser.cell.ParsedCell;
import org.ringingmaster.engine.parser.parse.Parse;
import org.ringingmaster.engine.parser.parse.ParseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static org.ringingmaster.engine.parser.definition.DefinitionFunctions.markDefinitionsInvalidInComposition;
import static org.ringingmaster.engine.parser.definition.DefinitionFunctions.markDefinitionsInvalidInDefinitions;

/**
 * Marks any participants in a circular dependency as invalid.
 *
 * @author Steve Lake
 */
@Immutable
public class ValidateDefinitionIsNotCircular implements Function<Parse, Parse> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    // TODO can we get the dependency chain in the error message? If so the addition of all shorthands for definitions in use will have to change.
    private final Function<String, String> createErrorMessage = (characters) -> "Definition [" + characters + "] forms part of a circular dependency";

    public Parse apply(Parse input) {

        log.debug("[{}] > validate definitions do not form a circular dependency", input.getComposition().getLoggingTag());

        Map<String, Set<String>> adjacency = new BuildDefinitionsAdjacencyList().apply(input);

        Set<String> invalidDefinitions = new HashSet<>();
        for (String shorthand : input.getAllDefinitionShorthands()) {
            discoverCircularity(invalidDefinitions, adjacency, ConsPStack.singleton(shorthand));
        }

        HashBasedTable<Integer, Integer, ParsedCell> compositionTableResult = markDefinitionsInvalidInComposition(input, invalidDefinitions, createErrorMessage);
        HashBasedTable<Integer, Integer, ParsedCell> definitionTableResult = markDefinitionsInvalidInDefinitions(input, invalidDefinitions, createErrorMessage);


        Parse result = new ParseBuilder()
                .prototypeOf(input)
                .setCompositionTableCells(compositionTableResult)
                .setDefinitionTableCells(definitionTableResult)
                .build();

        log.debug("[{}] < validate definitions do not form a circular dependency", input.getComposition().getLoggingTag());

        return result;
    }

    private void discoverCircularity(Set<String> results, Map<String, Set<String>> adjacency, PStack<String> path) {
        String shorthand = path.get(0);

        if (adjacency.containsKey(shorthand)) {
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

}