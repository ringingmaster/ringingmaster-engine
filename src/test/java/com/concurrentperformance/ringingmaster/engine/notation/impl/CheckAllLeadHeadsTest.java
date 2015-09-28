package com.concurrentperformance.ringingmaster.engine.notation.impl;


import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import com.concurrentperformance.ringingmaster.engine.notation.persist.PersistableNotationTransformer;
import com.concurrentperformance.ringingmaster.persist.DocumentPersist;
import com.concurrentperformance.ringingmaster.persist.generated.v1.LibraryNotationPersist;
import com.concurrentperformance.ringingmaster.persist.generated.v1.NotationLibraryType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class CheckAllLeadHeadsTest {

	private final Logger log = LoggerFactory.getLogger(this.getClass());


	public static final Path LIBRARY_PATH = Paths.get("./src/test/java/resource/notationlibrary.xml");

	@Parameterized.Parameters
	public static Collection<Object[]> checkAllCCLibrary() {
		NotationLibraryType notationLibrary = new DocumentPersist().readNotationLibrary(LIBRARY_PATH);

		return new DocumentPersist().readNotationLibrary(LIBRARY_PATH)
				.getNotation().stream()
				.map(notation -> new Object[]{notation})
				.collect(Collectors.toList());
	}

	public CheckAllLeadHeadsTest(LibraryNotationPersist persistableNotation) {
		this.persistableNotation = persistableNotation;
	}

	private final LibraryNotationPersist persistableNotation;

	@Test
	public void checkLeadHeadCorrectness() {

		log.info(persistableNotation.toString());

		NotationBody notationBody = PersistableNotationTransformer
				.populateBuilderFromPersistableNotation(persistableNotation)
				.build();

		String ccLeadHead = persistableNotation.getLeadHead();
		String calculatedLeadHead = notationBody.getLeadHeadCode();

		// Uncomment section to log out the changes in the lead
//		log.warn(notationBody.toString());
//		log.warn(PlainCourseHelper.buildPlainCourse(notationBody, "TEST", false).getCreatedMethod().getLead(0).toString());

		assertEquals("[" + notationBody.getNumberOfWorkingBells().getBellCount() + "] " + notationBody.getNameIncludingNumberOfBells() +
						"[" + ccLeadHead + "](library) vs [" + calculatedLeadHead + "](calculated) NOT OK: " + notationBody.toString(),
				ccLeadHead, calculatedLeadHead);

	}
}