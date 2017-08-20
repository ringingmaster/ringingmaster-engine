package org.ringingmaster.engine.touch.compiler.impl;

import org.ringingmaster.engine.notation.NotationCall;
import org.ringingmaster.engine.touch.container.Touch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

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
		log.debug("{} > creating call map", logPreamble);
		Map<String, NotationCall> callNameToCall = new HashMap<>();
		if (touch.getNonSplicedActiveNotation() != null) {
			for (NotationCall notationCall : touch.getNonSplicedActiveNotation().getCalls()) {
				callNameToCall.put(notationCall.getName(), notationCall);
				callNameToCall.put(notationCall.getNameShorthand(), notationCall);
			}
		}
		log.debug("{} < creating call map [{}]", logPreamble, callNameToCall);
		return callNameToCall;
	}

}
