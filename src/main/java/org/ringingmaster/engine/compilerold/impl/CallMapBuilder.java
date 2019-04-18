package org.ringingmaster.engine.compilerold.impl;

import org.ringingmaster.engine.composition.Composition;
import org.ringingmaster.engine.notation.NotationCall;
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

	private final Composition composition;
	private final String logPreamble;

	CallMapBuilder(Composition composition, String logPreamble) {
		this.composition = composition;
		this.logPreamble = logPreamble;
	}


	public Map<String, NotationCall> createCallMap() {
		log.debug("{} > creating call map", logPreamble);
		Map<String, NotationCall> callNameToCall = new HashMap<>();
		if (composition.getNonSplicedActiveNotation() != null) {
			for (NotationCall notationCall : composition.getNonSplicedActiveNotation().get().getCalls()) {
				callNameToCall.put(notationCall.getName(), notationCall);
				callNameToCall.put(notationCall.getNameShorthand(), notationCall);
			}
		}
		log.debug("{} < creating call map [{}]", logPreamble, callNameToCall);
		return callNameToCall;
	}

}
