package com.concurrentperformance.ringingmaster.engine.parser;

import com.concurrentperformance.ringingmaster.engine.touch.Touch;

/**
 * TODO comments???
 * User: Stephen
 */
public interface Parser {

	void parseAndAnnotate(Touch touch);
}
