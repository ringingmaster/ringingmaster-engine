package org.ringingmaster.engine.parser;

import org.ringingmaster.engine.parser.assignparse.AssignMultiplier;
import org.ringingmaster.engine.parser.assignparse.AssignParseType;
import org.ringingmaster.engine.parser.callposition.MultipleCallPositionsInOneCell;
import org.ringingmaster.engine.parser.definition.CircularDefinition;
import org.ringingmaster.engine.parser.definition.DefinitionInSplicedOrMain;
import org.ringingmaster.engine.parser.group.GroupLogic;
import org.ringingmaster.engine.parser.splice.SplicedCallsNotDefinedInEachMethod;
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
    private final AssignMultiplier assignMultiplier = new AssignMultiplier();
    private final MultipleCallPositionsInOneCell multipleCallPositionsInOneCell = new MultipleCallPositionsInOneCell();
    private final SplicedCallsNotDefinedInEachMethod splicedCallsNotDefinedInEachMethod = new SplicedCallsNotDefinedInEachMethod();
    private final GroupLogic groupLogic = new GroupLogic();
    private final DefinitionInSplicedOrMain definitionInSplicedOrMain = new DefinitionInSplicedOrMain();
    private final CircularDefinition circularDefinition = new CircularDefinition();


    @Override
    public Parse apply(Touch touch) {

        log.info("Parsing");

        //TODO think very care fully about what parts of each parser needs applying to definitions,

        return assignParseType
                .andThen(assignMultiplier)
                .andThen(multipleCallPositionsInOneCell)
                .andThen(splicedCallsNotDefinedInEachMethod)
//TODO		parseSplicedCallPosMethodNotDefinedInEachMethod();
//TODO		parseSplicedCallPosAgregateNotDefinedInEachMethod();
//TODO		parseSpliceCountDifferentInEachMethod();
//TODO		parseVarianceLogic();
                .andThen(groupLogic)
//TODO		parseGroupOnDifferentLines(); // TODO I dont think we need this one now as allowing groups on many lines. Might have to do extra parsing when a block definition in use though to make sure we have full groups in the block
//TODO		parseVarianceGroupInteractionLogic();
//TODO		parseSpaceOnlyInCell(); //TODO I dont think this is necessary, apart from where whole rows and columns are empty
//TODO		parseCallsInColumnWithoutCallingPos();
//TODO		parseSplicedNotBlocks();
                .andThen(definitionInSplicedOrMain)
                .andThen(circularDefinition)
                .apply(touch);

    }
}
