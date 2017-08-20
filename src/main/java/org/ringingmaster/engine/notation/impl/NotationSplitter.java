package org.ringingmaster.engine.notation.impl;

import net.jcip.annotations.Immutable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ringingmaster.engine.notation.NotationPlace;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Split an input notation in the constituent parts, respecting the
 * different style on entry. </b>
 *  i.e. 'X12X', x.12x' Etc.
 *
 * @author Stephen Lake
 */
@Immutable
public class NotationSplitter {

	/**
	 * regex for extracting groups of individual notations
	 * This selects either a single all change, or any number of places
	 */
	private static final String REGEX_PATTERN;

	static {
		String regexPattern = "[" + NotationPlace.ALL_CHANGE.getRegex() + "]|[";
		for (int i=0;i< NotationPlace.values().length-1;i++) {
			regexPattern += NotationPlace.valueOf(i).getRegex();
		}
		regexPattern += "]{1,}";
		REGEX_PATTERN = regexPattern;
	}


	private final Pattern pattern = Pattern.compile(REGEX_PATTERN);

	/**
	 * Split the input notation into Strings, where each String represents a
	 * single row. A single row can contain many notations
	 * e.g.
	 *    x.12.x should return a list containing {'x','12','x')
	 * 
	 * @param notation
	 * @return List<String>
	 */
	List<String> split(final String notation) {
		checkNotNull(notation, "notation must not be null");
		final List<String> result = new ArrayList<String>();
		final Matcher matcher = pattern.matcher(notation);
		while (matcher.find()) {
			final String element = matcher.group();
			result.add(element);
		}
		return result;
	}
}