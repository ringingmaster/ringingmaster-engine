package org.ringingmaster.engine.notation;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;

@RunWith(Parameterized.class)
public class PlaceSetSequenceSplitterTest {

	private NotationSplitter fixture = new NotationSplitter();

	private final String notation;
	private final String[] expectedSplits;
	
	public PlaceSetSequenceSplitterTest(String notation, String... expectedSplits) {
		super();
		this.notation = notation;
		this.expectedSplits = expectedSplits;
		
	}

	@Parameters
	public static Collection<Object[]> testValues() {
		return Arrays.asList(new Object[][] { 
			{ "X", new String[]{"X"} },
			{ "x", new String[]{"x"} },
			{ "x.X", new String[]{"x", "X"} },
			{ ".x", new String[]{"x"} },
			{ ".x.", new String[]{"x"} },
			{ "ET", new String[]{"ET"} },
			{ ".Et", new String[]{"Et"} },
			{ ".Et.", new String[]{"Et"} },
			{ "x.X.12", new String[]{"x", "X", "12"} },
			{ "xX12", new String[]{"x", "X", "12"} },
			{ "12.34.56.78.90.et.ET", new String[]{"12","34","56","78","90","et","ET"} },
			{ "12X34X56X78X90XetXET", new String[]{"12","X","34","X","56","X","78","X","90","X","et","X","ET"} },
			{ "x12zxz1z2", new String[]{"x","12","x","1","2"} },
			});
	}

	@Test
	public void split() {
		List<String> split = fixture.split(notation);
		String[] actualSplits = new String[split.size()];
		split.toArray(actualSplits);
		assertArrayEquals(expectedSplits, actualSplits);
	}
}