package com.concurrentperformance.ringingmaster.engine.compiler.impl;

import java.util.HashMap;
import java.util.Map;

import com.concurrentperformance.ringingmaster.engine.notation.NotationCall;
import com.concurrentperformance.ringingmaster.engine.touch.Touch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO comments???
 * User: Stephen
 */
public class CallMapBuilder {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private final Touch touch;
	private final String logPreamble;

	CallMapBuilder(Touch touch, String logPreamble) {
		this.touch = touch;
		this.logPreamble = logPreamble;
	}


	public Map<String, NotationCall> createCallMap() {
		log.info("{} > creating call map", logPreamble);
		Map<String, NotationCall> callNameToCall = new HashMap<>();
		for (NotationCall notationCall : touch.getActiveNotation().getCalls()) {
			callNameToCall.put(notationCall.getName(), notationCall);
			callNameToCall.put(notationCall.getNameShorthand(), notationCall);
		}
		log.info("{} < creating call map [{}]", logPreamble, callNameToCall);
		return callNameToCall;
	}

}
