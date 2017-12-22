package org.ringingmaster.engine.parsernew;

import org.ringingmaster.engine.parsernew.assignparse.AssignParseType;
import org.ringingmaster.engine.parsernew.callposition.MultipleCallPositionsInOneCell;
import org.ringingmaster.engine.parsernew.group.GroupLogic;
import org.ringingmaster.engine.touch.newcontainer.Touch;
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
    final MultipleCallPositionsInOneCell multipleCallPositionsInOneCell = new MultipleCallPositionsInOneCell();
    private final GroupLogic groupLogic = new GroupLogic();


    @Override
    public Parse apply(Touch touch) {

        log.info("Parsing");

        return assignParseType
                .andThen(multipleCallPositionsInOneCell)
        //TODO think very care fully about what parts of each parser needs applying to definitions,
//TODO		parseSplicedCallsNotDefinedInEachMethod();
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
//TODO		parseDefinitionsOnWrongSide();

        //done last so that invalidity can be passed to the multipliers -  probablyu not true anymore.
//		new MultiplierParser().parse(touch);
                .apply(touch);

    }
}
