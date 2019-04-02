package org.ringingmaster.engine.parser;

import org.ringingmaster.engine.parser.assignparsetype.AssignAndGroupMultiplier;
import org.ringingmaster.engine.parser.assignparsetype.AssignParseType;
import org.ringingmaster.engine.parser.assignparsetype.GroupTheValidationOpenAndDetailParseTypes;
import org.ringingmaster.engine.parser.brace.ValidateMultiplierGroupAndVarianceDontOverlap;
import org.ringingmaster.engine.parser.brace.ValidateMultiplierGroupLogic;
import org.ringingmaster.engine.parser.brace.ValidateVarianceLogic;
import org.ringingmaster.engine.parser.callposition.ValidateSingleCallPositionPerCell;
import org.ringingmaster.engine.parser.definition.ValidateDefinitionIsNotUsedSplicedAndMain;
import org.ringingmaster.engine.parser.definition.ValidateDefinitionNotCircular;
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

    private final AssignParseType assignParseType = new AssignParseType();
    private final AssignAndGroupMultiplier assignAndGroupMultiplier = new AssignAndGroupMultiplier();
    private final GroupTheValidationOpenAndDetailParseTypes groupTheValidationOpenAndDetailParseTypes = new GroupTheValidationOpenAndDetailParseTypes();

    private final ValidateSingleCallPositionPerCell validateSingleCallPositionPerCell = new ValidateSingleCallPositionPerCell();
    private final ValidateInUseCallAvailableInEveryMethodWhenSpliced validateInUseCallAvailableInEveryMethodWhenSpliced = new ValidateInUseCallAvailableInEveryMethodWhenSpliced();
    private final ValidateVarianceLogic validateVarianceLogic = new ValidateVarianceLogic();
    private final ValidateMultiplierGroupLogic validateMultiplierGroupLogic = new ValidateMultiplierGroupLogic();
    private final ValidateMultiplierGroupAndVarianceDontOverlap validateMultiplierGroupAndVarianceDontOverlap = new ValidateMultiplierGroupAndVarianceDontOverlap();
    private final ValidateDefinitionIsNotUsedSplicedAndMain validateDefinitionIsNotUsedSplicedAndMain = new ValidateDefinitionIsNotUsedSplicedAndMain();
    private final ValidateDefinitionNotCircular validateDefinitionNotCircular = new ValidateDefinitionNotCircular();


    @Override
    public Parse apply(Touch touch) {

        log.info("[{}] > parsing", touch.getTitle());

        //TODO think very care fully about what parts of each parser needs applying to definitions,

        Parse parse =
                // set the parse types
                assignParseType
                .andThen(assignAndGroupMultiplier)
                .andThen(groupTheValidationOpenAndDetailParseTypes)

                // validate
                .andThen(validateSingleCallPositionPerCell)
                .andThen(validateInUseCallAvailableInEveryMethodWhenSpliced)
//TODO		parseSplicedCallPosMethodNotDefinedInEachMethod();
//TODO		parseSplicedCallPosAgregateNotDefinedInEachMethod();
//TODO		parseSpliceCountDifferentInEachMethod();
                .andThen(validateVarianceLogic)
                .andThen(validateMultiplierGroupLogic)
//TODO		parseGroupOnDifferentLines(); // TODO I dont think we need this one now as allowing groups on many lines. Might have to do extra parsing when a block definition in use though to make sure we have full groups in the block
                .andThen(validateMultiplierGroupAndVarianceDontOverlap)
//TODO		parseSpaceOnlyInCell(); //TODO I dont think this is necessary, apart from where whole rows and columns are empty
//TODO		parseCallsInColumnWithoutCallingPos();
//TODO		parseSplicedNotBlocks();
                .andThen(validateDefinitionIsNotUsedSplicedAndMain)
                .andThen(validateDefinitionNotCircular)
                .apply(touch);

        log.info("[{}] < parsing", touch.getTitle());
        return parse;
    }
}
