package org.ringingmaster.engine.compilernew;

import org.ringingmaster.engine.compilernew.coursebased.CourseBasedPipeline;
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


    @Override
    public Proof apply(Parse parse) {

        return getPipeline(parse).apply(parse);
    }

    private Function<Parse, Proof> getPipeline(Parse parse) {

        return courseBasedPipeline;

//        switch (parse.getTouch().getCheckingType() ) {
//
//            case COURSE_BASED:
//                return courseBasedPipeline;
//            case LEAD_BASED:
//                break;
//        }
//        return null;
    }


}
