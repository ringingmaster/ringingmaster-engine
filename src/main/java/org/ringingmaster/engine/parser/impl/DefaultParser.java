package org.ringingmaster.engine.parser.impl;


import net.jcip.annotations.NotThreadSafe;

import org.ringingmaster.engine.touch.newcontainer.Touch;
import org.ringingmaster.engine.parser.Parser;

/**
 * Default implementation of the Parser interface
 *
 * User: Stephen
 */
@NotThreadSafe
public class DefaultParser implements Parser {

	@Override
	public void parseAndAnnotate(Touch touch) {
//		touch.resetParseData();
//		new AssignParseTypeParser().parse(touch);
		//TODO think very care fully about what parts of each parser needs applying to definitions,
//		new MultipleCallPositionsInOneCell().parse(touch);
//TODO		parseSplicedCallsNotDefinedInEachMethod();
//TODO		parseSplicedCallPosMethodNotDefinedInEachMethod();
//TODO		parseSplicedCallPosAgregateNotDefinedInEachMethod();
//TODO		parseSpliceCountDifferentInEachMethod();
//TODO		parseVarianceLogic();
//		new GroupLogicParser().parse(touch);
//TODO		parseGroupOnDifferentLines(); // TODO I dont think we need this one now as allowing groups on many lines. Might have to do extra parsing when a block definition in use though to make sure we have full groups in the block
//TODO		parseVarianceGroupInteractionLogic();
//TODO		parseSpaceOnlyInCell(); //TODO I dont think this is necessary, apart from where whole rows and columns are empty
//TODO		parseCallsInColumnWithoutCallingPos();
//TODO		parseSplicedNotBlocks();
//TODO		parseDefinitionsOnWrongSide();

		//done last so that invalidity can be passed to the multipliers
//		new MultiplierParser().parse(touch);
	}


}
