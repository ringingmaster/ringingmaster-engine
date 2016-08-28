package com.concurrentperformance.ringingmaster.engine;


import com.concurrentperformance.ringingmaster.engine.helper.PlainCourseHelper;
import com.concurrentperformance.ringingmaster.engine.method.MethodLead;
import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import com.concurrentperformance.ringingmaster.engine.notation.impl.LeadHeadCalculator;
import com.concurrentperformance.ringingmaster.engine.notation.persist.PersistableNotationTransformer;
import com.concurrentperformance.ringingmaster.engine.touch.proof.Proof;
import com.concurrentperformance.ringingmaster.persist.DocumentPersist;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.assertEquals;

public class MethodCorrectnessTest {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	public static final Path LIBRARY_PATH = Paths.get("./src/test/resources/notationlibrary.xml").toAbsolutePath().normalize();
	public static final Path PROBLEM_NOTATION_PATH = Paths.get("./src/test/resources/checkLeadHeadCodeGenerationAgainstCCLibrary_ProblemNotations.txt").toAbsolutePath().normalize();

	@Ignore
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
								notationBody.getNumberOfWorkingBells().getBellCount(), notationBody.getNameIncludingNumberOfBells(),
								persistableNotation.getLeadLength(), (lead.getRowCount() - 1), notationBody.toString());
						return true;
					}
					return false;

				})
				.count();

		assertEquals(0, problemNotationCount);
	}

	@Ignore
	@Test
	public void checkLeadHeadCodeGenerationAgainstCCLibrary() throws IOException {

		List<String> problemNotations = Files.readAllLines(PROBLEM_NOTATION_PATH);

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
								notationBody.getNumberOfWorkingBells().getBellCount(), notationBody.getNameIncludingNumberOfBells(),
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

		assertEquals(0, problemNotationCount);


// If its ever needed again, this code is the start of writing out all the problem notations
//			Path normalize = Paths.get(".", "checkLeadHeadCodeGenerationAgainstCCLibrary_ProblemNotations.txt").toAbsolutePath().normalize();
//			Files.write(normalize, notationBody.getNameIncludingNumberOfBells().getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
//			Files.write(normalize, System.lineSeparator().getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);

	}

	@Ignore
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
								notationBody.getNumberOfWorkingBells().getBellCount(), notationBody.getNameIncludingNumberOfBells(),
								persistableNotation.getLeadLength(), (lead.getRowCount() - 1), notationBody.toString());
						return true;
					}
					return false;

				})
				.count();

		assertEquals(0, problemNotationCount);
	}

}