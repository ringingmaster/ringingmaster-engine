package org.ringingmaster.engine.compilerold.impl;

import org.ringingmaster.engine.compilerold.Compiler;
import org.ringingmaster.engine.composition.Composition;

/**
 * TODO comments.
 *
 * @author stephen
 */
public class CompilerFactory {

	public static Compiler getInstance(Composition composition) {
		return getInstance(composition, "");
	}

	public static Compiler getInstance(Composition composition, String logPreamble) {
		switch (composition.getCompositionType()) {
//			case LEAD_BASED:
//				return new LeadBasedCompiler(composition, logPreamble);
//			case COURSE_BASED:
//				return new CourseBasedCompiler(composition, logPreamble);
			default:
				throw new IllegalStateException("Cant build Compiler for [" + composition + "]");
		}
	}
}