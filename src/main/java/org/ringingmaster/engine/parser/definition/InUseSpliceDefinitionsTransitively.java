package org.ringingmaster.engine.parser.definition;

import org.ringingmaster.engine.parser.parse.Parse;

import java.util.Map;
import java.util.Set;

import static org.ringingmaster.engine.parser.assignparsetype.ParseType.DEFINITION;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
class InUseSpliceDefinitionsTransitively {

    private final InUseNamesForParseType inUseNamesForParseType = new InUseNamesForParseType();

    public Set<String> apply(Parse parse) {
        final Map<String, Set<String>> adjacency = new BuildDefinitionsAdjacencyList().apply(parse);
        final Set<String> mainBodyDefinitions = inUseNamesForParseType.apply(parse.splicedCells(), DEFINITION);
        return new FollowTransitiveDefinitions().apply(mainBodyDefinitions, adjacency);
    }
}
