package org.ringingmaster.engine.parser.assignparsetype;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import org.ringingmaster.engine.composition.cell.Cell;
import org.ringingmaster.engine.parser.cell.ParsedCell;
import org.ringingmaster.engine.parser.cell.grouping.ElementRange;
import org.ringingmaster.engine.parser.cell.grouping.Group;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.concurrent.Immutable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkState;
import static org.ringingmaster.engine.parser.assignparsetype.LexerDefinition.SORT_PRIORITY_THEN_SIZE_THEN_REGEX;
import static org.ringingmaster.engine.parser.cell.ParsedCellFactory.buildParsedCellFromGroups;
import static org.ringingmaster.engine.parser.cell.grouping.GroupingFactory.buildGroup;
import static org.ringingmaster.engine.parser.cell.grouping.GroupingFactory.buildGroupToMatchSection;
import static org.ringingmaster.engine.parser.cell.grouping.GroupingFactory.buildSection;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
@Immutable
class CellLexer {

    private final Logger log = LoggerFactory.getLogger(CellLexer.class);

    ParsedCell lexCell(Cell cell, Set<LexerDefinition> lexerDefinitions, String logPreamble) {
        Set<Group> groups = calculateSections(cell, lexerDefinitions, logPreamble);
        return buildParsedCellFromGroups(cell, groups);
    }

    private Set<Group> calculateSections(Cell cell, Set<LexerDefinition> lexerDefinitions, String logPreamble) {
        final String cellAsString = cell.getCharacters();
        Set<Group> groups = new HashSet<>();

        List<LexerDefinition> sortedLexerDefinitions = new ArrayList<>(lexerDefinitions);
        sortedLexerDefinitions.sort(SORT_PRIORITY_THEN_SIZE_THEN_REGEX);

        sortedLexerDefinitions.forEach((parseing) -> {
            checkState(parseing.getRegex().length() > 0, "Should never have an empty token. Mapped to [{}]", Arrays.toString(parseing.getParseTypes()));

            log.debug("[{}]  testing for [{}] against cell string [{}]", logPreamble, parseing, cellAsString);

            Pattern p = Pattern.compile(parseing.getRegex());
            Matcher m = p.matcher(cellAsString);
            int searchFromIndex = 0;

            while (m.find(searchFromIndex)) {

                int matchStart = m.start();
                int matchEnd = m.end();
                int matchLength = matchEnd-matchStart;

                log.debug("[{}]   starting at index [{}], found match between index [{}>{}] with [{}] groups", logPreamble, searchFromIndex, matchStart, matchEnd, m.groupCount());

                if (m.groupCount() == 0) {
                    checkState(parseing.getParseTypes().length == 1, "No regex groups detected - should have 1 parse type, but supplied [%s]", parseing.getParseTypes().length );
                    addNonGroupedMatchIfRoom(groups, parseing.getParseTypes()[0], matchStart, matchLength, logPreamble);
                }
                else {
                    checkState(m.groupCount() == parseing.getParseTypes().length, "Mismatch between parse types count [%s] and regex groups length [%s] ", parseing.getParseTypes().length, m.groupCount());
                    addGroupedMatchIfRoom(logPreamble, groups, parseing, m, matchStart, matchLength);
                }

                searchFromIndex = m.start() +1;
            }

        });
        return groups;
    }

    private void addNonGroupedMatchIfRoom(Set<Group> groups, ParseType parseType, int start, int length, String logPreamble) {
        log.debug("[{}]    looking for room for lexical match [{}] [{}/{}]", logPreamble, parseType, start, length);

        if (isRangeAvailable(start, length, groups)) {
            groups.add(buildGroupToMatchSection(buildSection(start, length, parseType)));
            log.debug("[{}]     adding group with section", logPreamble);
        }
        else {
            log.debug("[{}]     not adding section", logPreamble);
        }
    }

    private void addGroupedMatchIfRoom(String logPreamble, Set<Group> groups, LexerDefinition parseing, Matcher m, int matchStart, int matchLength) {
        // We have a lexical match - do we have room?
        log.debug("[{}]    looking for room for lexical match [{}/{}]", logPreamble, matchStart, matchLength);
        if (isRangeAvailable(matchStart, matchLength, groups)) {
            log.debug("[{}]     adding group ", logPreamble);
            Set sections = Sets.newHashSet();
            int groupStart = matchStart;
            for (int group = 1; group <= m.groupCount(); group++) {
                ParseType parseType = parseing.getParseTypes()[group - 1];
                log.debug("[{}]      adding section [{}]", logPreamble, parseType);
                int groupLength = m.group(group).length();
                if (groupLength > 0) {
                    sections.add(buildSection(groupStart, groupLength, parseType));
                    groupStart += groupLength;
                }
            }
            groups.add(buildGroup(matchStart, matchLength, true, ImmutableList.of(), sections));
        }
        else  {
            log.debug("[{}]     not adding group", logPreamble);
        }
    }

    private boolean isRangeAvailable(int start, int length, Set<? extends ElementRange> ranges) {
        for (ElementRange range : ranges) {
            if (range.intersection(start, length)) {
                return false;
            }
        }
        return true;
    }
}
