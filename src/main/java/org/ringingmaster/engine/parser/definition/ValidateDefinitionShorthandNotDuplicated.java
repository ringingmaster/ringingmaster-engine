package org.ringingmaster.engine.parser.definition;

import com.google.common.collect.HashBasedTable;
import org.ringingmaster.engine.parser.cell.ParsedCell;
import org.ringingmaster.engine.parser.parse.Parse;
import org.ringingmaster.engine.parser.parse.ParseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.ringingmaster.engine.composition.tableaccess.DefinitionTableAccess.SHORTHAND_COLUMN;
import static org.ringingmaster.engine.parser.definition.DefinitionFunctions.markDefinitionsInvalidInComposition;
import static org.ringingmaster.engine.parser.definition.DefinitionFunctions.markDefinitionsInvalidInDefinitions;

public class ValidateDefinitionShorthandNotDuplicated implements Function<Parse, Parse> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final Function<String, String> createErrorMessage = (characters) -> "Definition [" + characters + "] can only be defined once";



    @Override
    public Parse apply(Parse input) {
        log.debug("[{}] > validate definition shorthand is only defined once", input.getComposition().getTitle());

        Parse result = doCheck(input);

        log.debug("[{}] < validate definition shorthand is only defined once", input.getComposition().getTitle());

        return result;

    }

    private Parse doCheck(Parse parse) {

        Set<String> duplicates = findDuplicates(parse);

        HashBasedTable<Integer, Integer, ParsedCell> compositionTableResult = markDefinitionsInvalidInComposition(parse, duplicates, createErrorMessage);
        HashBasedTable<Integer, Integer, ParsedCell> definitionTableResult = markDefinitionsInvalidInDefinitions(parse, duplicates, createErrorMessage);

        return new ParseBuilder()
                .prototypeOf(parse)
                .setCompositionTableCells(compositionTableResult)
                .setDefinitionTableCells(definitionTableResult)
                .build();

    }

    private Set<String> findDuplicates(Parse input) {
        Set<String> temp = new HashSet<>();
        return input.getComposition().getDefinitionAsTables().stream()
                .map(cell -> cell.get(0, SHORTHAND_COLUMN).getCharacters())
                .filter(shorthand -> !temp.add(shorthand))
                .collect(Collectors.toSet());
    }


}
