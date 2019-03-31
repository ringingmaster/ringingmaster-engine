package org.ringingmaster.engine.parser.assignparsetype;

import com.google.errorprone.annotations.Immutable;

import java.util.Arrays;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
@Immutable
class ParseDefinition {

    private final String regex;
    private final ParseType[] parseTypes;

    ParseDefinition(String regex, ParseType... parseTypes) {
        this.regex = regex;
        this.parseTypes = parseTypes;
    }

    public String getRegex() {
        return regex;
    }

    public ParseType[] getParseTypes() {
        return parseTypes;
    }

    @Override
    public String toString() {
        return "ParseDefinition{" +
                "regex='" + regex + '\'' +
                ", parseTypes=" + Arrays.toString(parseTypes) +
                '}';
    }
}
