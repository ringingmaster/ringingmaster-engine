package org.ringingmaster.engine.parser.definition;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Sets;
import com.google.errorprone.annotations.Immutable;
import org.ringingmaster.engine.parser.parse.Parse;
import org.ringingmaster.engine.parser.parse.ParseBuilder;
import org.ringingmaster.engine.parser.cell.ParsedCell;
import org.ringingmaster.engine.parser.functions.InUseMainBodyDefinitionsTransitively;
import org.ringingmaster.engine.parser.functions.InUseSpliceDefinitionsTransitively;
import org.ringingmaster.engine.parser.functions.DefinitionFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.function.Function;

/**
 * Marks as invalid any definitions that are used in both spliced or main area.
 * A dependency is considered part of splice or main when it is directly used, or used transitively.
 *
 * @author stevelake
 */
@Immutable
public class ValidateDefinitionIsUsedSplicedOrMain implements Function<Parse, Parse> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final DefinitionFunctions definitionFunctions = new DefinitionFunctions();


    // TODO can we get the dependency chain in the error message?
    private final Function<String, String> createErrorMessage = (characters) -> "Definition [" + characters + "] should be used in the main body or the splice area, but not both";


    public Parse apply(Parse input) {
        log.debug("[{}] > validate definition is used  spliced or main", input.getComposition().getTitle());

        Parse result = doCheck(input);

        log.debug("[{}] < validate definition is used  spliced or main", input.getComposition().getTitle());

        return result;
    }

    public Parse doCheck(Parse parse) {

        final Set<String> mainBodyDefinitions = new InUseMainBodyDefinitionsTransitively().apply(parse);
        final Set<String> splicedDefinitions = new InUseSpliceDefinitionsTransitively().apply(parse);

        //Step 1: find the problematic definitions
        Set<String> invalidDefinitions = Sets.intersection(mainBodyDefinitions, splicedDefinitions);
        if (invalidDefinitions.size() == 0) {
            return parse;
        }

        // Step 4: Mark invalid
        if (!invalidDefinitions.isEmpty()) {
            log.debug("[{}]  marking invalid definitions [{}]", parse.getComposition().getTitle(), invalidDefinitions);
        }


        HashBasedTable<Integer, Integer, ParsedCell> compositionTableResult =
                HashBasedTable.create(parse.allCompositionCells().getBackingTable());
        definitionFunctions.markInvalid(parse.mainBodyCells(), invalidDefinitions, compositionTableResult, createErrorMessage);
        definitionFunctions.markInvalid(parse.splicedCells(), invalidDefinitions, compositionTableResult, createErrorMessage);

        HashBasedTable<Integer, Integer, ParsedCell> definitionTableResult =
                HashBasedTable.create(parse.definitionDefinitionCells().getBackingTable());
        definitionFunctions.markInvalid(parse.definitionDefinitionCells(), invalidDefinitions, definitionTableResult, createErrorMessage);

        return new ParseBuilder()
                .prototypeOf(parse)
                .setCompositionTableCells(compositionTableResult)
                .setDefinitionTableCells(definitionTableResult)
                .build();
    }

}