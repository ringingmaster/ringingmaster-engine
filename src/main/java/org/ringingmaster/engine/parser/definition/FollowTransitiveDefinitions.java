package org.ringingmaster.engine.parser.definition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
public class FollowTransitiveDefinitions implements BiFunction<Set<String>, Map<String, Set<String>>, Set<String>> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public Set<String> apply(Set<String> rootDefinitions, Map<String, Set<String>> adjacency) {
        final Set<String> results = new HashSet<>();
        followDefinitions(results, rootDefinitions, adjacency);
        return results;
    }

    private void followDefinitions(Set<String> results, Set<String> rootDefinitions, Map<String, Set<String>> adjacency) {
        for (String definition : rootDefinitions) {
            if (!results.contains(definition)) {
                results.add(definition);
                final Set<String> dependentDefinition = adjacency.get(definition);
                if (dependentDefinition != null) {
                    followDefinitions(results, dependentDefinition, adjacency);
                }
            }
        }
    }
}
