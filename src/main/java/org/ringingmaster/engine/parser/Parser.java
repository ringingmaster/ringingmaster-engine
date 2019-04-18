package org.ringingmaster.engine.parser;

import org.ringingmaster.engine.parser.assignparsetype.AssignParseType;
import org.ringingmaster.engine.parser.brace.ValidateMultiplierGroupAndVarianceDontOverlap;
import org.ringingmaster.engine.parser.brace.ValidateMultiplierGroupMatchingBrace;
import org.ringingmaster.engine.parser.brace.ValidateVarianceMatchingBraceLogic;
import org.ringingmaster.engine.parser.call.ValidateDefaultCallMultiplierFullyDefined;
import org.ringingmaster.engine.parser.callposition.ValidateSingleCallPositionPerCell;
import org.ringingmaster.engine.parser.definition.ValidateDefinitionIsUsedSplicedOrMain;
import org.ringingmaster.engine.parser.definition.ValidateDefinitionIsNotCircular;
import org.ringingmaster.engine.parser.observability.PrettyPrintCells;
import org.ringingmaster.engine.parser.parse.Parse;
import org.ringingmaster.engine.parser.splice.ValidateInUseCallAvailableInEveryMethodWhenSpliced;
import org.ringingmaster.engine.touch.Touch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;


/**
 * TODO comments???
 *
 * @author stevelake
 */
public class Parser implements Function<Touch, Parse> {

    private final Logger log = LoggerFactory.getLogger(Parser.class);


    @Override
    public Parse apply(Touch touch) {

        log.info("[{}] > parsing", touch.getTitle());

        Parse parse = pipeline.apply(touch);

        log.info("[{}] < parsing", touch.getTitle());
        return parse;
    }

    private static Function<Touch, Parse> pipeline =
            // set the parse types
            new AssignParseType()
                    //TODO think very care fully about what parts of each parser needs applying to definitions,

            // validate
            .andThen(new ValidateSingleCallPositionPerCell())
            .andThen(new ValidateInUseCallAvailableInEveryMethodWhenSpliced())
        //TODO		parseSplicedCallPosMethodNotDefinedInEachMethod();
        //TODO		parseSplicedCallPosAgregateNotDefinedInEachMethod();
        //TODO		parseSpliceCountDifferentInEachMethod();
            .andThen(new ValidateVarianceMatchingBraceLogic())
            .andThen(new ValidateMultiplierGroupMatchingBrace())
        //TODO		parseGroupOnDifferentLines(); // TODO I dont think we need this one now as allowing groups on many lines. Might have to do extra parsing when a block definition in use though to make sure we have full groups in the block
            .andThen(new ValidateMultiplierGroupAndVarianceDontOverlap())
        //TODO		parseSpaceOnlyInCell(); //TODO I dont think this is necessary, apart from where whole rows and columns are empty
        //TODO		parseCallsInColumnWithoutCallingPos();
        //TODO		parseSplicedNotBlocks();
            .andThen(new ValidateDefinitionIsUsedSplicedOrMain())
            .andThen(new ValidateDefinitionIsNotCircular())
            .andThen(new ValidateDefaultCallMultiplierFullyDefined())

            .andThen(new PrettyPrintCells());


        //TODO need to enhance tool tips as per old ringingmaster. See: CString CellElement::getTipText()
}
