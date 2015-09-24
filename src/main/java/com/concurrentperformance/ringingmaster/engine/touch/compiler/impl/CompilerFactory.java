package com.concurrentperformance.ringingmaster.engine.touch.compiler.impl;

import com.concurrentperformance.ringingmaster.engine.touch.compiler.Compiler;
import com.concurrentperformance.ringingmaster.engine.touch.container.Touch;

/**
 * TODO comments.
 *
 * @author stephen
 */
public class CompilerFactory {

	public static Compiler getInstance(Touch touch) {
		return getInstance(touch, "");
	}

	public static Compiler getInstance(Touch touch, String logPreamble) {
		switch (touch.getTouchCheckingType()) {
			case LEAD_BASED:
				return new LeadBasedCompiler(touch, logPreamble);
			case COURSE_BASED:
				return new CourseBasedCompiler(touch, logPreamble);
			default:
				throw new IllegalStateException("Cant build Compiler for [" + touch + "]");
		}
	}
}