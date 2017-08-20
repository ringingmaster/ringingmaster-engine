package org.ringingmaster.engine.method;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * TODO comments???
 * User: Stephen
 */
public class RowCourseTypeTest {

	@Test
	public void inCourse() {
		RowCourseType rowCourseType = RowCourseType.calculateRowCourseType(buildBellArray(1, 5, 6, 2, 3, 4));
		assertEquals(RowCourseType.POSITIVE, rowCourseType);
	}

	@Test
	public void outCourse() {
		RowCourseType rowCourseType = RowCourseType.calculateRowCourseType(buildBellArray(1, 5, 6, 2, 4, 3));
		assertEquals(RowCourseType.NEGATIVE, rowCourseType);
	}

	private Bell[] buildBellArray(int... bellNums) {
		Bell[] bells = new Bell[bellNums.length];
		for (int i =0;i<bellNums.length;i++) {
			bells[i] = Bell.valueOf(bellNums[i]-1);
		}
		return bells;
	}

}
