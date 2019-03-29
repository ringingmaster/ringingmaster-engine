package org.ringingmaster.engine.compiler;

import org.ringingmaster.engine.compiler.coursebased.CourseBasedCompilePipeline;
import org.ringingmaster.engine.compiler.leadbased.LeadBasedCompilePipeline;
import org.ringingmaster.engine.compiler.proof.Proof;
import org.ringingmaster.engine.parser.parse.Parse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class Compiler implements Function<Parse, Proof> {

    private final Logger log = LoggerFactory.getLogger(Compiler.class);


    private final CourseBasedCompilePipeline courseBasedCompilePipeline = new CourseBasedCompilePipeline();
    private final LeadBasedCompilePipeline leadBasedCompilePipeline = new LeadBasedCompilePipeline();


    @Override
    public Proof apply(Parse parse) {

        log.info("[{}] > compile", parse.getUnderlyingTouch().getTitle());

        Proof proof = getPipeline(parse).apply(parse);

        log.info("[{}] < compile", parse.getUnderlyingTouch().getTitle());

        return proof;
    }

    private Function<Parse, Proof> getPipeline(Parse parse) {

        switch (parse.getUnderlyingTouch().getCheckingType() ) {

//            case COURSE_BASED:
//                return courseBasedCompilePipeline;
            case LEAD_BASED:
                return leadBasedCompilePipeline;
            default:
                throw new RuntimeException("No pipeline for Parse Type [" + parse.getUnderlyingTouch().getCheckingType() + "]");
        }
    }


}
