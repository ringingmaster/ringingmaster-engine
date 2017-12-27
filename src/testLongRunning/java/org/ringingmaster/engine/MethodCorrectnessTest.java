package org.ringingmaster.engine;

import org.ringingmaster.engine.helper.PlainCourseHelper;
import org.ringingmaster.engine.method.MethodLead;
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.notation.impl.LeadHeadCalculator;
import org.ringingmaster.engine.notation.persist.PersistableNotationTransformer;
import org.ringingmaster.engine.proof.Proof;
import org.ringingmaster.persist.DocumentPersist;
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

		long problemNotationCount = new DocumentPersist().readNotationLibrary(LIBRARY_PATH)
				.getNotation().stream()
				.filter(persistableNotation ->  {
					NotationBody notationBody = PersistableNotationTransformer
							.populateBuilderFromPersistableNotation(persistableNotation)
							.build();

					final String leadHead = LeadHeadCalculator.lookupRowFromCode(persistableNotation.getLeadHead(), NumberOfBells.valueOf(persistableNotation.getNumberOfWorkingBells()));

					Proof proof = PlainCourseHelper.buildPlainCourse(notationBody, "", false);
					MethodLead lead = proof.getCreatedMethod().get().getLead(0);

					if (!Objects.equals(leadHead, lead.getLastRow().getDisplayString(false))) {
						log.warn("[%d] %s[%s](library) vs [%s](calculated) NOT OK: %s",
								notationBody.getNumberOfWorkingBells().toInt(), notationBody.getNameIncludingNumberOfBells(),
								persistableNotation.getLeadLength(), (lead.getRowCount() - 1), notationBody.toString());
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

		long problemNotationCount = new DocumentPersist().readNotationLibrary(LIBRARY_PATH)
				.getNotation().stream()
				.filter(persistableNotation ->  {
					NotationBody notationBody = PersistableNotationTransformer
							.populateBuilderFromPersistableNotation(persistableNotation)
							.build();
					String ccLeadHead = persistableNotation.getLeadHead();
					String calculatedLeadHead = notationBody.getLeadHeadCode();
					if (!Objects.equals(ccLeadHead, calculatedLeadHead)) {
						String msg = String.format("[%d] %s[%s](library) vs [%s](calculated) NOT OK: %s",
								notationBody.getNumberOfWorkingBells().toInt(), notationBody.getNameIncludingNumberOfBells(),
								ccLeadHead, calculatedLeadHead, notationBody.toString());
						if (problemNotations.contains(notationBody.getNameIncludingNumberOfBells())) {
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

		long problemNotationCount = new DocumentPersist().readNotationLibrary(LIBRARY_PATH)
				.getNotation().stream()
				.filter(persistableNotation ->  {
					NotationBody notationBody = PersistableNotationTransformer
							.populateBuilderFromPersistableNotation(persistableNotation)
							.build();

					Proof proof = PlainCourseHelper.buildPlainCourse(notationBody, "", false);
					MethodLead lead = proof.getCreatedMethod().get().getLead(0);

					if (persistableNotation.getLeadLength() != lead.getRowCount() - 1) {
						log.warn("[%d] %s[%s](library) vs [%s](calculated) NOT OK: %s",
								notationBody.getNumberOfWorkingBells().toInt(), notationBody.getNameIncludingNumberOfBells(),
								persistableNotation.getLeadLength(), (lead.getRowCount() - 1), notationBody.toString());
						return true;
					}
					return false;

				})
				.count();

		Assert.assertEquals(0, problemNotationCount);
	}

}