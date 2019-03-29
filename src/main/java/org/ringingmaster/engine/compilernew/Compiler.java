package org.ringingmaster.engine.compilernew;

import org.ringingmaster.engine.compilernew.coursebased.CourseBasedPipeline;
import org.ringingmaster.engine.compilernew.leadbased.LeadBasedPipeline;
import org.ringingmaster.engine.compilernew.proof.Proof;
import org.ringingmaster.engine.parser.parse.Parse;

import java.util.function.Function;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class Compiler implements Function<Parse, Proof> {

    private final CourseBasedPipeline courseBasedPipeline = new CourseBasedPipeline();
    private final LeadBasedPipeline leadBasedPipeline = new LeadBasedPipeline();


    @Override
    public Proof apply(Parse parse) {

        return getPipeline(parse).apply(parse);
    }

    private Function<Parse, Proof> getPipeline(Parse parse) {

        switch (parse.getUnderlyingTouch().getCheckingType() ) {

            case COURSE_BASED:
                return courseBasedPipeline;
            case LEAD_BASED:
                return leadBasedPipeline;
            default:
                throw new RuntimeException("No pipeline for Parse Type [" + parse.getUnderlyingTouch().getCheckingType() + "]");
        }
    }


}
