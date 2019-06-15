package org.ringingmaster.engine.parser;

import org.ringingmaster.engine.composition.Composition;
import org.ringingmaster.engine.parser.assignparsetype.AssignParseType;
import org.ringingmaster.engine.parser.brace.ValidateMultiplierGroupAndVarianceDontOverlap;
import org.ringingmaster.engine.parser.brace.ValidateMultiplierGroupMatchingBrace;
import org.ringingmaster.engine.parser.brace.ValidateVarianceMatchingBraceLogic;
import org.ringingmaster.engine.parser.brace.ValidateVariancePartNumbersWithinRange;
import org.ringingmaster.engine.parser.call.ValidateDefaultCallMultiplierFullyDefined;
import org.ringingmaster.engine.parser.callingposition.ValidateCellInColumnWithValidCallPosition;
import org.ringingmaster.engine.parser.callingposition.ValidateSingleCallingPositionPerCell;
import org.ringingmaster.engine.parser.definition.ValidateDefinitionIsNotCircular;
import org.ringingmaster.engine.parser.definition.ValidateDefinitionIsUsedSplicedOrMain;
import org.ringingmaster.engine.parser.definition.ValidateDefinitionShorthandNotDuplicated;
import org.ringingmaster.engine.parser.observability.PrettyPrintCells;
import org.ringingmaster.engine.parser.observability.SetEndTime;
import org.ringingmaster.engine.parser.parse.Parse;
import org.ringingmaster.engine.parser.splice.ValidateInUseCallAvailableInEveryMethodWhenSpliced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;


/**
 * TODO comments???
 *
 * @author Steve Lake
 */
public class Parser implements Function<Composition, Parse> {

    private final Logger log = LoggerFactory.getLogger(Parser.class);


    @Override
    public Parse apply(Composition composition) {

        log.info("[{}] > parse", composition.getLoggingTag());

        Parse parse = pipeline.apply(composition);

        log.info("[{}] < parse", composition.getLoggingTag());
        return parse;
    }

    private static Function<Composition, Parse> pipeline =
            // set the parse types
            new AssignParseType()
                    //TODO think very care fully about what parts of each parser needs applying to definitions,

            // validate
            .andThen(new ValidateSingleCallingPositionPerCell())
            .andThen(new ValidateInUseCallAvailableInEveryMethodWhenSpliced())
        //TODO		parseSplicedCallPosMethodNotDefinedInEachMethod();
        //TODO		parseSplicedCallPosAgregateNotDefinedInEachMethod();
        //TODO		parseSpliceCountDifferentInEachMethod();
            .andThen(new ValidateVarianceMatchingBraceLogic())
            .andThen(new ValidateVariancePartNumbersWithinRange())
            .andThen(new ValidateMultiplierGroupMatchingBrace())
        //TODO		parseGroupOnDifferentLines(); // TODO I dont think we need this one now as allowing groups on many lines. Might have to do extra parsing when a block definition in use though to make sure we have full groups in the block
            .andThen(new ValidateMultiplierGroupAndVarianceDontOverlap())
            .andThen(new ValidateCellInColumnWithValidCallPosition())
        //TODO		parseSplicedNotBlocks();
            .andThen(new ValidateDefinitionIsUsedSplicedOrMain())
            .andThen(new ValidateDefinitionIsNotCircular())
            .andThen(new ValidateDefinitionShorthandNotDuplicated())
            .andThen(new ValidateDefaultCallMultiplierFullyDefined())

            .andThen(new PrettyPrintCells())
            .andThen(new SetEndTime());


        //TODO need to enhance tool tips as per old ringingmaster. See: CString CellElement::getTipText()
}
