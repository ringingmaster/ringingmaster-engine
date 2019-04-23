package org.ringingmaster.engine.compilerold.impl;

import org.ringingmaster.engine.composition.Composition;
import org.ringingmaster.engine.notation.Call;
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


	public Map<String, Call> createCallMap() {
		log.debug("{} > creating call map", logPreamble);
		Map<String, Call> callNameToCall = new HashMap<>();
		if (composition.getNonSplicedActiveNotation() != null) {
			for (Call call : composition.getNonSplicedActiveNotation().get().getCalls()) {
				callNameToCall.put(call.getName(), call);
				callNameToCall.put(call.getNameShorthand(), call);
			}
		}
		log.debug("{} < creating call map [{}]", logPreamble, callNameToCall);
		return callNameToCall;
	}

}
