package com.concurrentperformance.ringingmaster.engine.analysis.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.concurrentperformance.ringingmaster.engine.analysis.Analysis;
import com.concurrentperformance.ringingmaster.engine.method.Method;
import com.concurrentperformance.ringingmaster.engine.method.MethodRow;

/**
 * TODO comments???
 * User: Stephen
 */
public class FalseRowAnalysis {

	FalseRowAnalysis() {

	}

	public void analise(Method method, Analysis analysis) { //TODO should this be in the analysis package?
		if (method.getFirstRow() == null) {
			analysis.setFalseRowGroups(Collections.<List<MethodRow>>emptyList());
		}
		List<MethodRow> sortedRows = getSortedRows(method);
		List<List<MethodRow>> falseRowGroups = buildFalseRowGroup(sortedRows);

		analysis.setFalseRowGroups(falseRowGroups);
	}

	private List<MethodRow> getSortedRows(Method method) {
		List<MethodRow> sortedRows = new ArrayList<>();
		boolean excludeFirstRow = method.getFirstRow() != null &&
								  method.getFirstRow().equals(method.getLastRow());

		for (int i=excludeFirstRow?1:0;i<method.getRowCount();i++) {
			sortedRows.add(method.getRow(i));
		}
		Collections.sort(sortedRows);
		return sortedRows;
	}

	private List<List<MethodRow>> buildFalseRowGroup(List<MethodRow> sortedRows) {
		List<List<MethodRow>> falseRowGroups = new ArrayList<>();
		List<MethodRow> currentFalseRowList = null;
		MethodRow lastRow = null;
		for (MethodRow row : sortedRows) {
			if (lastRow != null) {
				if (lastRow.equals(row)) {
					if (currentFalseRowList == null) {
						currentFalseRowList = new ArrayList<>();
						falseRowGroups.add(currentFalseRowList);
						currentFalseRowList.add(lastRow);
					}
					currentFalseRowList.add(row);
				}
				else {
					currentFalseRowList = null;
				}
			}
			lastRow = row;
		}
		return falseRowGroups;
	}
}
