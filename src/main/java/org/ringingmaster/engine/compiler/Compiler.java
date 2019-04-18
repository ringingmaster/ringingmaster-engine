package org.ringingmaster.engine.compiler;

import org.ringingmaster.engine.compiler.compiledcomposition.CompiledComposition;
import org.ringingmaster.engine.compiler.coursebased.CourseBasedCompilePipeline;
import org.ringingmaster.engine.compiler.leadbased.LeadBasedCompilePipeline;
import org.ringingmaster.engine.parser.parse.Parse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class Compiler implements Function<Parse, CompiledComposition> {

    private final Logger log = LoggerFactory.getLogger(Compiler.class);


    private final static CourseBasedCompilePipeline courseBasedCompilePipeline = new CourseBasedCompilePipeline();
    private final static LeadBasedCompilePipeline leadBasedCompilePipeline = new LeadBasedCompilePipeline();


    @Override
    public CompiledComposition apply(Parse parse) {

        log.info("[{}] > compile", parse.getComposition().getTitle());

        CompiledComposition compiledComposition = getPipeline(parse).apply(parse);

        log.info("[{}] < compile", parse.getComposition().getTitle());

        return compiledComposition;
    }

    private Function<Parse, CompiledComposition> getPipeline(Parse parse) {

        switch (parse.getComposition().getCheckingType() ) {

//            case COURSE_BASED:
//                return courseBasedCompilePipeline;
            case LEAD_BASED:
                return leadBasedCompilePipeline;
            default:
                throw new RuntimeException("No pipeline for Parse Type [" + parse.getComposition().getCheckingType() + "]");
        }
    }


}
