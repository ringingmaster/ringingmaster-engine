package org.ringingmaster.engine.parser.assignparsetype;

import com.google.errorprone.annotations.Immutable;

import java.util.Arrays;
import java.util.Comparator;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Defines a regex that the lexer searches for. Then create grouped ParseType tokens based around the regex capture groups.
 *
 * @author Steve Lake
 */
@Immutable
class LexerDefinition {

    static final int PRIORITY_LOWEST = -1000;
    static final int PRIORITY_HIGHEST = 1000;

    static Comparator<LexerDefinition> SORT_PRIORITY_THEN_REGEX =
            Comparator.comparingInt(LexerDefinition::getPriority).reversed().
                    thenComparing(LexerDefinition::getRegex);

    private final int priority;
    private final String regex;
    private final ParseType[] parseTypes;

    /**
     * Constructor that gets priority from the regex length
     *
     * @param regex the match string. Can contain capture groups.
     * @param parseTypes When the regex does not contain capture, then pass one ParseType, otherwise match the
     *                   number of ParseTypes to the number of capture groups.
     */
    LexerDefinition(String regex, ParseType... parseTypes) {
        this(regex.length(), regex, parseTypes);
    }

    /**
     * Constructor with an explicit priority.
     *
     * @param priority defined the The lex order - high number is lexed first.
     *                 Use the constants at the top of the class.
     * @param regex the match string. Can contain capture groups.
     * @param parseTypes When the regex does not contain capture, then pass one ParseType, otherwise match the
     *                   number of ParseTypes to the number of capture groups.
     */
    LexerDefinition(int priority, String regex, ParseType... parseTypes) {
        this.priority = priority;
        this.regex = checkNotNull(regex);
        checkArgument(parseTypes.length > 0);
        this.parseTypes = checkNotNull(parseTypes);
    }


    int getPriority() {
        return priority;
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
                "priority=" + priority +
                ", regex='" + regex + '\'' +
                ", parseTypes=" + Arrays.toString(parseTypes) +
                '}';
    }
}
