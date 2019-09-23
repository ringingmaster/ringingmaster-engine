package org.ringingmaster.engine;

import org.ringingmaster.engine.compiler.compiledcomposition.CompiledComposition;
import org.ringingmaster.engine.helper.PlainCourseHelper;
import org.ringingmaster.engine.method.Lead;
import org.ringingmaster.engine.notation.Notation;
import org.ringingmaster.engine.notation.LeadHeadCalculator;
import org.ringingmaster.engine.notation.persist.PersistableNotationTransformer;
import org.ringingmaster.persist.NotationLibraryPersister;
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
		//TODO NOTE: This fails for a number of CC notations that come round before the plain course. Need an additional termination
		// mechanism that looks only for rounds at the end of the lead,

		long problemNotationCount = new NotationLibraryPersister().readNotationLibrary(LIBRARY_PATH)
				.getNotation().stream()
				.filter(persistableNotation ->  {
					Notation notation = PersistableNotationTransformer
							.populateBuilderFromPersistableNotation(persistableNotation)
							.build();
                    log.info("LeadHead CHANGE correctness test for [{}]", notation.getNameIncludingNumberOfBells());

					final String ccLeadHeadChange = LeadHeadCalculator.lookupRowFromCode(persistableNotation.getLeadHead(), NumberOfBells.valueOf(persistableNotation.getNumberOfWorkingBells()));

					CompiledComposition calculatedPlainCourse = PlainCourseHelper.buildPlainCourse(notation, "");
					Lead calcLead = calculatedPlainCourse.getMethod().get().getLead(0);
                    String calcLeadHeadChange = calcLead.getLastRow().getDisplayString(false);

                    if (!Objects.equals(ccLeadHeadChange, calcLeadHeadChange)) {
						log.warn("[{}] {}  expected[{}] actual[{}]",
								notation.getNumberOfWorkingBells().toInt(),
                                notation.getNameIncludingNumberOfBells(),
                                ccLeadHeadChange,
                                calcLeadHeadChange);
						return true;
					}
					return false;

				})
				.count();

		Assert.assertEquals(0, problemNotationCount);
	}

	@Test
	public void checkLeadHeadCodeGenerationAgainstCCLibrary() throws IOException {
		//TODO NOTE: This fails for a number of CC notations that come round before the plain course. Need an additional termination
		// mechanism that looks only for rounds at the end of the lead,

		List<String> problemNotations = Files.readAllLines(KNOWN_PROBLEM_NOTATION_PATH);

		long problemNotationCount = new NotationLibraryPersister().readNotationLibrary(LIBRARY_PATH)
				.getNotation().stream()
				.filter(persistableNotation ->  {
					Notation notation = PersistableNotationTransformer
							.populateBuilderFromPersistableNotation(persistableNotation)
							.build();

                    log.info("LeadHead CODE correctness test for [{}]", notation.getNameIncludingNumberOfBells());

                    String ccLeadHeadCode = persistableNotation.getLeadHead();
					String calcLeadHeadCode = notation.getLeadHeadCode();

					if (!Objects.equals(ccLeadHeadCode, calcLeadHeadCode)) {
						if (problemNotations.contains(notation.getNameIncludingNumberOfBells())) {
							log.info("Ignoring known issue for: [{}]", notation.getNameIncludingNumberOfBells());
						}
						else {
                            log.warn("[{}] {}  expected[{}] actual[{}]",
                                    notation.getNumberOfWorkingBells().toInt(),
                                    notation.getNameIncludingNumberOfBells(),
                                    ccLeadHeadCode,
                                    calcLeadHeadCode);
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
		//TODO NOTE: This fails for a number of CC notations that come round before the plain course. Need an additional termination
		// mechanism that looks only for rounds at the end of the lead,

		long problemNotationCount = new NotationLibraryPersister().readNotationLibrary(LIBRARY_PATH)
				.getNotation().stream()
				.filter(persistableNotation ->  {
					Notation notation = PersistableNotationTransformer
							.populateBuilderFromPersistableNotation(persistableNotation)
							.build();

					CompiledComposition compiledComposition = PlainCourseHelper.buildPlainCourse(notation, "");
					Lead lead = compiledComposition.getMethod().get().getLead(0);

					if (persistableNotation.getLeadLength() != lead.getRowCount() - 1) {
                        log.warn("[{}] {}  expected[{}] actual[{}]",
                                notation.getNumberOfWorkingBells().toInt(),
                                notation.getNameIncludingNumberOfBells(),
                                persistableNotation.getLeadLength(),
                                lead.getRowCount() - 1);
						return true;
					}
					return false;

				})
				.count();

		Assert.assertEquals(0, problemNotationCount);
	}

}