package org.ringingmaster.engine.compiler.coursebased;

import com.google.common.collect.ImmutableMap;
import org.ringingmaster.engine.composition.Composition;
import org.ringingmaster.engine.helper.PlainCourseHelper;
import org.ringingmaster.engine.method.Method;
import org.ringingmaster.engine.notation.CallingPosition;
import org.ringingmaster.engine.notation.Notation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

/**
 * Builds the map of tenor place's for each calling position.
 * It does this by building a plain course, then finding the tenor position on the calling positions defined lead and
 * call initiation row.
 *
 * @author Steve Lake
 */
public class BuildTenorPlaceForCallingPositionLookup implements Function<CourseBasedCompilerPipelineData, CourseBasedCompilerPipelineData> {

    private final Logger log = LoggerFactory.getLogger(BuildTenorPlaceForCallingPositionLookup.class);


    @Override
    public CourseBasedCompilerPipelineData apply(CourseBasedCompilerPipelineData input) {
        log.debug("{} > build tenor place for calling position", input.getLogPreamble());

        ImmutableMap<CallingPosition, Integer> tenorPlaceForCallingPositionLookup =
                buildTenorPlaceForCallingPositionLookup(input.getParse().getComposition(), input.getLogPreamble());

        log.debug("{} < build tenor place for calling position. [{}]", input.getLogPreamble(), tenorPlaceForCallingPositionLookup); //TODO log the calling bell positions

        return input.setTenorPlaceForCallingPositionLookup(tenorPlaceForCallingPositionLookup);
    }

    private ImmutableMap<CallingPosition, Integer> buildTenorPlaceForCallingPositionLookup(Composition composition, String logPreamble) {
        // Build a plain course.
        Notation activeNotation = composition.getNonSplicedActiveNotation().get();
        Method plainCourse = PlainCourseHelper.buildPlainCourse(activeNotation, logPreamble + " > ").getMethod().get();

        // build the map.
        ImmutableMap.Builder<CallingPosition, Integer> callingPositionMap = ImmutableMap.builder();

        for (CallingPosition callingPosition : activeNotation.getCallingPositions()) {
            int place = plainCourse
                    .getLead(callingPosition.getLeadOfTenor())
                    .getRow(callingPosition.getCallInitiationRow())
                    .getPlaceOfBell(composition.getNumberOfBells().getTenor());
            callingPositionMap.put(callingPosition, place);
        }

        return callingPositionMap.build();
    }
}
