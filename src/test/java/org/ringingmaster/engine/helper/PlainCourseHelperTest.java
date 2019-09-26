package org.ringingmaster.engine.helper;

import org.junit.Test;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.compiler.compiledcomposition.CompiledComposition;
import org.ringingmaster.engine.method.Method;
import org.ringingmaster.engine.notation.Notation;
import org.ringingmaster.engine.notation.NotationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class PlainCourseHelperTest {

    //TODO Needs more testing.

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Test
    public void internalFalsenessWithRoundsBuildsCorrectPlainCourse() throws IOException {
        Notation notation = NotationBuilder.getInstance()
                .setNumberOfWorkingBells(NumberOfBells.BELLS_4)
                .setName("Clearwell Hybrid")
                .setFoldedPalindromeNotationShorthand("34.34.34.14.12.12.12.34", "12")
                .build();

        CompiledComposition composition = PlainCourseHelper.buildPlainCourse(notation, "");

        Optional<Method> method = composition.getMethod();

        log.info(method.get().getAllChangesAsText());

        assertEquals(48, method.get().getRowCount());
    }

}
