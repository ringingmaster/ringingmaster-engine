package org.ringingmaster.engine.compiler;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import org.junit.Test;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.analyser.Analyser;
import org.ringingmaster.engine.analyser.proof.Proof;
import org.ringingmaster.engine.compiler.compiledcomposition.CompiledComposition;
import org.ringingmaster.engine.composition.Composition;
import org.ringingmaster.engine.composition.ObservableComposition;
import org.ringingmaster.engine.composition.compositiontype.CompositionType;
import org.ringingmaster.engine.method.Bell;
import org.ringingmaster.engine.method.Method;
import org.ringingmaster.engine.method.MethodBuilder;
import org.ringingmaster.engine.notation.Notation;
import org.ringingmaster.engine.notation.NotationBuilder;
import org.ringingmaster.engine.parser.Parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.junit.Assert.assertEquals;
import static org.ringingmaster.engine.composition.TableType.MAIN_TABLE;


/**
 * TODO comments???
 * User: Stephen
 */
public class CourseBasedCompilerTest {

    private static Parser parser = new Parser();
    private static Compiler compiler = new Compiler();
    private static Analyser analyser = new Analyser();

    @Test
    public void compileSingleCallCalledFromTenor() throws IOException {
        ObservableComposition composition = buildPlainBobMinorCompositionShell();
        composition.addCharacters(MAIN_TABLE, 0, 0, "W");
        composition.addCharacters(MAIN_TABLE, 1, 0, "-");
        proveAndCheckComposition(15
                , "/PlainBobMinor W - FromTenor.txt", true, CompileTerminationReason.SPECIFIED_ROW, composition.get());
    }

    @Test
    public void compileSingleCallCalledFrom5() throws IOException {
        ObservableComposition composition = buildPlainBobMinorCompositionShell();
		composition.setCallFromBell(Bell.BELL_5);
        composition.addCharacters(MAIN_TABLE, 0, 0, "W");
        composition.addCharacters(MAIN_TABLE, 1, 0, "-");
        proveAndCheckComposition(15
				, "/PlainBobMinor W - From5.txt", true, CompileTerminationReason.SPECIFIED_ROW, composition.get());
    }

    private ObservableComposition buildPlainBobMinorCompositionShell() {
        ObservableComposition composition = new ObservableComposition();
        composition.setNumberOfBells(NumberOfBells.BELLS_6);
        composition.setTitle("Test Composition");
        composition.addNotation(buildPlainBobMinor());
        composition.setCheckingType(CompositionType.COURSE_BASED);
        composition.setTerminationChange(MethodBuilder.buildRoundsRow(NumberOfBells.BELLS_6));
        return composition;
    }

    private Notation buildPlainBobMinor() {
        return NotationBuilder.getInstance()
                .setNumberOfWorkingBells(NumberOfBells.BELLS_6)
                .setName("Plain Bob")
                .setFoldedPalindromeNotationShorthand("-16-16-16", "12")
                .addCall("Bob", "-", "14", true)
                .addCall("Single", "s", "1234", false)
                .addCallInitiationRow(11)
                .addMethodCallingPosition("W", 11, 0)
                .build();
    }

    private Proof proveAndCheckComposition(int expectedLeadCount, String fileName, boolean trueComposition,
                                           CompileTerminationReason terminationReason, Composition composition) throws IOException {

        Proof proof = parser.andThen(compiler).andThen(analyser).apply(composition);
        CompiledComposition compiledComposition = proof.getCompiledComposition();
        assertEquals(terminationReason, compiledComposition.getTerminationReason());
        assertEquals(expectedLeadCount, compiledComposition.getMethod().get().getLeadCount());
        checkAgainstFile(compiledComposition.getMethod().get(), fileName);
        assertEquals(trueComposition, proof.isTrueComposition());
        return proof;
    }

    private void checkAgainstFile(Method method, String fileName) throws IOException {
        String allChangesAsText = method.getAllChangesAsText();
        String fileContent;

        try (InputStream stream = getClass().getResourceAsStream(fileName)) {
            fileContent = CharStreams.toString(new InputStreamReader(stream, Charsets.UTF_8));
        }

        assertEquals(convertToLineSeparators(fileContent), convertToLineSeparators(allChangesAsText));
    }

    private String convertToLineSeparators(String text) {
        text = text.replace("\r\n", System.lineSeparator());
        text = text.replace("\r", System.lineSeparator());
        text = text.replace("\n", System.lineSeparator());
        return text;
    }
}
