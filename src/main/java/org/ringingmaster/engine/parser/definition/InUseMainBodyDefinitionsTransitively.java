package org.ringingmaster.engine.parser.definition;

import org.ringingmaster.engine.parser.functions.BuildDefinitionsAdjacencyList;
import org.ringingmaster.engine.parser.functions.FollowTransitiveDefinitions;
import org.ringingmaster.engine.parser.functions.InUseNamesForParseType;
import org.ringingmaster.engine.parser.parse.Parse;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static org.ringingmaster.engine.parser.assignparsetype.ParseType.DEFINITION;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
class InUseMainBodyDefinitionsTransitively implements Function<Parse, Set<String>>{

    private final InUseNamesForParseType inUseNamesForParseType = new InUseNamesForParseType();

    @Override
    public Set<String> apply(Parse parse) {
        final Map<String, Set<String>> adjacency = new BuildDefinitionsAdjacencyList().apply(parse);
        final Set<String> mainBodyDefinitions = inUseNamesForParseType.apply(parse.mainBodyCells(), DEFINITION);
        return new FollowTransitiveDefinitions().apply(mainBodyDefinitions, adjacency);
    }
}
