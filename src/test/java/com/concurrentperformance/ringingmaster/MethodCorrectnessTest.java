package com.concurrentperformance.ringingmaster;

import com.concurrentperformance.ringingmaster.engine.NumberOfBells;
import com.concurrentperformance.ringingmaster.engine.helper.PlainCourseHelper;
import com.concurrentperformance.ringingmaster.engine.method.MethodLead;
import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import com.concurrentperformance.ringingmaster.engine.notation.impl.LeadHeadCalculator;
import com.concurrentperformance.ringingmaster.engine.notation.persist.PersistableNotationTransformer;
import com.concurrentperformance.ringingmaster.engine.touch.proof.Proof;
import com.concurrentperformance.ringingmaster.persist.DocumentPersist;
import com.concurrentperformance.ringingmaster.persist.generated.v1.LibraryNotationPersist;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

/**
 * TODO Comments
 *
 * @author Lake
 */
@RunWith(Parameterized.class)
public class MethodCorrectnessTest   {


	public static final Path LIBRARY_PATH = Paths.get("./src/test/resource/notationlibrary.xml");

	@Parameters
	public static Collection<Object[]> checkAllCCLibrary() {
		return new DocumentPersist().readNotationLibrary(LIBRARY_PATH)
				.getNotation().stream()
				.map(serializableNotation -> new Object[]{serializableNotation})
				.collect(Collectors.toList());
	}

	public MethodCorrectnessTest(LibraryNotationPersist persistableNotation) {
		this.persistableNotation = persistableNotation;
		this.leadHead = LeadHeadCalculator.lookupRowFromCode(persistableNotation.getLeadHead(), NumberOfBells.valueOf(persistableNotation.getNumberOfWorkingBells()));
	}

	private final LibraryNotationPersist persistableNotation;
	private final String leadHead;

	@Test
	public void checkCalculatedMethodLength() {

		NotationBody notationBody = PersistableNotationTransformer
				.populateBuilderFromPersistableNotation(persistableNotation)
				.build();

		Proof proof = PlainCourseHelper.buildPlainCourse(notationBody, "", false);
		MethodLead lead = proof.getCreatedMethod().get().getLead(0);
	//	log.info(lead.toString());

		assertEquals(persistableNotation.getLeadLength(), lead.getRowCount() - 1);
		assertEquals(leadHead, lead.getLastRow().getDisplayString(false));


	}
}
