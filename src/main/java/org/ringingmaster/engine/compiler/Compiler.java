package org.ringingmaster.engine.compiler;

import org.ringingmaster.engine.compiler.coursebased.CourseBasedCompilePipeline;
import org.ringingmaster.engine.compiler.leadbased.LeadBasedCompilePipeline;
import org.ringingmaster.engine.compiler.compiledtouch.CompiledTouch;
import org.ringingmaster.engine.parser.parse.Parse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class Compiler implements Function<Parse, CompiledTouch> {

    private final Logger log = LoggerFactory.getLogger(Compiler.class);


    private final static CourseBasedCompilePipeline courseBasedCompilePipeline = new CourseBasedCompilePipeline();
    private final static LeadBasedCompilePipeline leadBasedCompilePipeline = new LeadBasedCompilePipeline();


    @Override
    public CompiledTouch apply(Parse parse) {

        log.info("[{}] > compile", parse.getTouch().getTitle());

        CompiledTouch compiledTouch = getPipeline(parse).apply(parse);

        log.info("[{}] < compile", parse.getTouch().getTitle());

        return compiledTouch;
    }

    private Function<Parse, CompiledTouch> getPipeline(Parse parse) {

        switch (parse.getTouch().getCheckingType() ) {

//            case COURSE_BASED:
//                return courseBasedCompilePipeline;
            case LEAD_BASED:
                return leadBasedCompilePipeline;
            default:
                throw new RuntimeException("No pipeline for Parse Type [" + parse.getTouch().getCheckingType() + "]");
        }
    }


}
