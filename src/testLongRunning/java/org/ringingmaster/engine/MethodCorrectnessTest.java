package org.ringingmaster.engine;

import org.ringingmaster.engine.compiler.compiledcomposition.CompiledComposition;
import org.ringingmaster.engine.helper.PlainCourseHelper;
import org.ringingmaster.engine.method.Lead;
import org.ringingmaster.engine.notation.Notation;
import org.ringingmaster.engine.notation.LeadHeadCalculator;
import org.ringingmaster.engine.notation.persist.PersistableNotationTransformer;
import org.ringingmaster.persist.NotationLibraryPersist;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;


public class MethodCorrectnessTest {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	public static final Path LIBRARY_PATH = Paths.get("./src/test/resources/notationlibrary.xml").toAbsolutePath().normalize();
	public static final Path KNOWN_PROBLEM_NOTATION_PATH = Paths.get("./src/test/resources/checkLeadHeadCodeGenerationAgainstCCLibrary_KnownProblemNotations.txt").toAbsolutePath().normalize();

	@Test
	public void checkCalculatesLastRowInLeadCorrectlyAgainstCCLibrary() throws IOException {

		long problemNotationCount = new NotationLibraryPersist().readNotationLibrary(LIBRARY_PATH)
				.getNotation().stream()
				.filter(persistableNotation ->  {
					Notation notation = PersistableNotationTransformer
							.populateBuilderFromPersistableNotation(persistableNotation)
							.build();

					final String leadHead = LeadHeadCalculator.lookupRowFromCode(persistableNotation.getLeadHead(), NumberOfBells.valueOf(persistableNotation.getNumberOfWorkingBells()));

					CompiledComposition compiledComposition = PlainCourseHelper.buildPlainCourse(notation, "");
					Lead lead = compiledComposition.getMethod().get().getLead(0);

					if (!Objects.equals(leadHead, lead.getLastRow().getDisplayString(false))) {
						log.warn("[%d] %s[%s](library) vs [%s](calculated) NOT OK: %s",
								notation.getNumberOfWorkingBells().toInt(), notation.getNameIncludingNumberOfBells(),
								persistableNotation.getLeadLength(), (lead.getRowCount() - 1), notation.toString());
						return true;
					}
					return false;

				})
				.count();

		Assert.assertEquals(0, problemNotationCount);
	}

	@Test
	public void checkLeadHeadCodeGenerationAgainstCCLibrary() throws IOException {

		List<String> problemNotations = Files.readAllLines(KNOWN_PROBLEM_NOTATION_PATH);

		long problemNotationCount = new NotationLibraryPersist().readNotationLibrary(LIBRARY_PATH)
				.getNotation().stream()
				.filter(persistableNotation ->  {
					Notation notation = PersistableNotationTransformer
							.populateBuilderFromPersistableNotation(persistableNotation)
							.build();
					String ccLeadHead = persistableNotation.getLeadHead();
					String calculatedLeadHead = notation.getLeadHeadCode();
					if (!Objects.equals(ccLeadHead, calculatedLeadHead)) {
						String msg = String.format("[%d] %s[%s](library) vs [%s](calculated) NOT OK: %s",
								notation.getNumberOfWorkingBells().toInt(), notation.getNameIncludingNumberOfBells(),
								ccLeadHead, calculatedLeadHead, notation.toString());
						if (problemNotations.contains(notation.getNameIncludingNumberOfBells())) {
							log.info("Ignoring known issue for: [{}]", msg);
						}
						else {
							log.error(msg);
							return true;
						}
					}
					return false;
				})
				.count();

		Assert.assertEquals(0, problemNotationCount);


// If its ever needed again, this code is the start of writing out all the problem notations
//			Path normalize = Paths.get(".", "checkLeadHeadCodeGenerationAgainstCCLibrary_ProblemNotations.txt").toAbsolutePath().normalize();
//			Files.write(normalize, notationBody.getNameIncludingNumberOfBells().getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
//			Files.write(normalize, System.lineSeparator().getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);

	}

	@Test
	public void checkCalculatesMethodLengthAgainstCCLibrary() throws IOException {

		long problemNotationCount = new NotationLibraryPersist().readNotationLibrary(LIBRARY_PATH)
				.getNotation().stream()
				.filter(persistableNotation ->  {
					Notation notation = PersistableNotationTransformer
							.populateBuilderFromPersistableNotation(persistableNotation)
							.build();

					CompiledComposition compiledComposition = PlainCourseHelper.buildPlainCourse(notation, "");
					Lead lead = compiledComposition.getMethod().get().getLead(0);

					if (persistableNotation.getLeadLength() != lead.getRowCount() - 1) {
						log.warn("[%d] %s[%s](library) vs [%s](calculated) NOT OK: %s",
								notation.getNumberOfWorkingBells().toInt(), notation.getNameIncludingNumberOfBells(),
								persistableNotation.getLeadLength(), (lead.getRowCount() - 1), notation.toString());
						return true;
					}
					return false;

				})
				.count();

		Assert.assertEquals(0, problemNotationCount);
	}

}