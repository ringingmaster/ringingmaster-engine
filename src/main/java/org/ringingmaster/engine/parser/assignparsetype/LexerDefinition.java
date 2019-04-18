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

    static Comparator<LexerDefinition> SORT_PRIORITY_THEN_SIZE_THEN_REGEX =
            Comparator.comparingInt(LexerDefinition::getPriority).reversed().
                    thenComparing(Comparator.comparingInt(LexerDefinition::getSubPriority).reversed()).
                    thenComparing(LexerDefinition::getRegex);

    private final int priority;
    private final int subPriority;
    private final String regex;
    private final ParseType[] parseTypes;


    /**
     * Constructor with an explicit priority.
     *
     * @param priority defines the lex order - high number is lexed first.
     *                 Use the constants at the top of the class.
     * @param subPriority  Sub priority of the regex we are matching. Defines the lex order - high number is lexed first.
     * @param regex the match string. Can contain capture groups.
     * @param parseTypes When the regex does not contain capture, then pass one ParseType, otherwise match the
     *                   number of ParseTypes to the number of capture groups.
     */
    LexerDefinition(int priority, int subPriority, String regex, ParseType... parseTypes) {
        this.priority = priority;
        this.subPriority = subPriority;
        this.regex = checkNotNull(regex);
        checkArgument(parseTypes.length > 0);
        this.parseTypes = checkNotNull(parseTypes);
    }


    int getPriority() {
        return priority;
    }

    public int getSubPriority() {
        return subPriority;
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
                "/" + subPriority +
                ", regex='" + regex + '\'' +
                ", parseTypes=" + Arrays.toString(parseTypes) +
                '}';
    }
}
