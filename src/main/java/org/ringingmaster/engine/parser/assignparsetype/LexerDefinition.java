package org.ringingmaster.engine.parser.assignparsetype;

import com.google.errorprone.annotations.Immutable;

import java.util.Arrays;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Defines a something we will lex for.
 *
 * @author Steve Lake
 */
@Immutable
class LexerDefinition {

    private final String regex;
    private final ParseType[] parseTypes;

    LexerDefinition(String regex, ParseType... parseTypes) {
        this.regex = checkNotNull(regex);
        checkArgument(parseTypes.length > 0);
        this.parseTypes = checkNotNull(parseTypes);
    }

    String getRegex() {
        return regex;
    }

    ParseType[] getParseTypes() {
        return parseTypes;
    }

    @Override
    public String toString() {
        return "LexerDefinition{" +
                "regex='" + regex + '\'' +
                ", parseTypes=" + Arrays.toString(parseTypes) +
                '}';
    }
}
