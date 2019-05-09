package org.ringingmaster.engine.compiler;

import org.ringingmaster.engine.compiler.compiledcomposition.CompiledComposition;
import org.ringingmaster.engine.compiler.coursebased.CourseBasedCompilerPipeline;
import org.ringingmaster.engine.compiler.leadbased.LeadBasedCompilerPipeline;
import org.ringingmaster.engine.parser.parse.Parse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
public class Compiler implements Function<Parse, CompiledComposition> {

    private final Logger log = LoggerFactory.getLogger(Compiler.class);


    private final static CourseBasedCompilerPipeline COURSE_BASED_COMPILER_PIPELINE = new CourseBasedCompilerPipeline();
    private final static LeadBasedCompilerPipeline LEAD_BASED_COMPILER_PIPELINE = new LeadBasedCompilerPipeline();


    @Override
    public CompiledComposition apply(Parse parse) {

        log.info("[{}] > compile [{}] ", parse.getComposition().getTitle(), parse.getComposition().getCompositionType());

        CompiledComposition compiledComposition = getPipeline(parse).apply(parse);

        log.info("[{}] < compile", parse.getComposition().getTitle());

        return compiledComposition;
    }

    private Function<Parse, CompiledComposition> getPipeline(Parse parse) {

        switch (parse.getComposition().getCompositionType() ) {

            case COURSE_BASED:
                return COURSE_BASED_COMPILER_PIPELINE;
            case LEAD_BASED:
                return LEAD_BASED_COMPILER_PIPELINE;
            default:
                throw new RuntimeException("No pipeline for Checking Type [" + parse.getComposition().getCompositionType() + "]");
        }
    }


}
