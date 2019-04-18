package org.ringingmaster.engine.analyser.rows;

import com.google.common.collect.ImmutableList;
import org.ringingmaster.engine.analyser.pipelinedata.AnalysisPipelineData;
import org.ringingmaster.engine.method.Method;
import org.ringingmaster.engine.method.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.function.Function;

/**
 * TODO comments???
 * User: Stephen
 */

//TODO needs testing
public class FalseRowAnalysis implements Function<AnalysisPipelineData, AnalysisPipelineData> {

	private final Logger log = LoggerFactory.getLogger(FalseRowAnalysis.class);

	@Override
	public AnalysisPipelineData apply(AnalysisPipelineData input) {

		log.debug("{} > false row analysis", "TODO");
		AnalysisPipelineData result = analise(input);
		log.debug("{} > false row analysis", "TODO");

		return result;
	}

	private AnalysisPipelineData analise(AnalysisPipelineData input) {
		if (!input.getCompiledTouch().getMethod().isPresent()) {
			return input;
		}

		Method method = input.getCompiledTouch().getMethod().get();
		if (!method.getFirstRow().isPresent()) {
			return input;
		}

		//TODO Could this be be a stream operation?
		Row[] sortedRows = getSortedRows(method);
		ImmutableList<ImmutableList<Row>> falseRowGroups = buildFalseRowGroup(sortedRows);

		return input.setFalseRowGroups(falseRowGroups);
	}

	private Row[] getSortedRows(Method method) {
		boolean excludeLastRow = method.firstAndLastRowEqual();
		Row[] rows = method.getRows(!excludeLastRow);
		Arrays.sort(rows);
		return rows;
	}

	private ImmutableList<ImmutableList<Row>> buildFalseRowGroup(Row[] sortedRows) {
		ImmutableList.Builder<ImmutableList<Row>> falseRowGroups = ImmutableList.builder();
		ImmutableList.Builder<Row> currentFalseRowGroup = null;
		Row previousRow = null;
		for (Row row : sortedRows) {
			if (previousRow != null) {
				if (previousRow.equals(row)) {
					if (currentFalseRowGroup == null) {
						currentFalseRowGroup = ImmutableList.builder();
						currentFalseRowGroup.add(previousRow);
					}
					currentFalseRowGroup.add(row);
				}
				else if (currentFalseRowGroup != null) {
					falseRowGroups.add(currentFalseRowGroup.build());
					currentFalseRowGroup = null;
				}
			}
			previousRow = row;
		}
		return falseRowGroups.build();
	}
}
