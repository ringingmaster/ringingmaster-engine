package org.ringingmaster.engine.method;

import java.util.Arrays;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Definition of a row being in (+ve) or out of course (-ve)
 *
 * User: Stephen
 */
public enum RowCourseType {

	POSITIVE,
	NEGATIVE,
	;

	public static RowCourseType calculateRowCourseType(Bell[] originalBells) {
		Bell[] bells = Arrays.copyOf(checkNotNull(originalBells, "bells can't be null"), originalBells.length);
		RowCourseType courseType = POSITIVE;

		for (int i=0;i<bells.length;i++) {
			if (bells[i].ordinal() != i) {
				courseType = flipRowCourseType(courseType);
				for (int j=i;j<bells.length;j++) {
					if (bells[j].ordinal() == i) {
						swap(bells, i, j);
					}
				}
			}
		}
		return courseType;
	}

	private static void swap(Bell[] bells, int i, int j) {
		Bell temp = bells[i];
		bells[i] = bells[j];
		bells[j] = temp;
	}

	public static RowCourseType flipRowCourseType(RowCourseType rowCourseType) {
		return (rowCourseType == POSITIVE)?NEGATIVE:POSITIVE;
	}

}
