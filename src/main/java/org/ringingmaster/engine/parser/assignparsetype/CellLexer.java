package org.ringingmaster.engine.parser.assignparsetype;

import org.ringingmaster.engine.parser.cell.ParsedCell;
import org.ringingmaster.engine.parser.cell.ParsedCellFactory;
import org.ringingmaster.engine.parser.cell.Section;
import org.ringingmaster.engine.touch.cell.Cell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.concurrent.Immutable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkState;

/**
 * TODO comments???
 *
 * @author stevelake
 */
@Immutable
class CellLexer {

    private final Logger log = LoggerFactory.getLogger(CellLexer.class);

    private static Comparator<ParseDefinition> SORT_SIZE_THEN_NAME = (o1, o2) -> {
        int result = (o2.getRegex().length() - o1.getRegex().length());
        if (result != 0) {
            return result;
        }
        return o1.getRegex().compareTo(o2.getRegex());
    };

    ParsedCell lexCell(Cell cell, Set<ParseDefinition> parseDefinitions) {
        Set<Section> sections = calculateSections(cell, parseDefinitions);
        return ParsedCellFactory.buildParsedCell(cell, sections);
    }

    private Set<Section> calculateSections(Cell cell, Set<ParseDefinition> parseDefinitions) {
        final String cellAsString = cell.getCharacters();
        Set<Section> sections = new HashSet<>();

        List<ParseDefinition> sortedParseDefinitions = new ArrayList<>(parseDefinitions);
        sortedParseDefinitions.sort(SORT_SIZE_THEN_NAME);

        sortedParseDefinitions.forEach((parseing) -> {
            checkState(parseing.getRegex().length() > 0, "Should never have an empty token. Mapped to [{}]", Arrays.toString(parseing.getParseTypes()));

            log.debug("Testing for [{}]", parseing);

            Pattern p = Pattern.compile(parseing.getRegex());//. represents single character
            Matcher m = p.matcher(cellAsString);
            int searchFromIndex = 0;

            while (m.find(searchFromIndex)) {

                int start = m.start();
                int end = m.end();

                log.debug(" Starting at index [{}], found match between index [{}-{}] with [{}] groups", searchFromIndex, start, end, m.groupCount());

                if (m.groupCount() == 0) {
                    checkState(parseing.getParseTypes().length == 1, "No regex groups detected - should have 1 parse type, but supplied [%s]", parseing.getParseTypes().length );
                    addLexicalMatchIfRoom(sections, parseing.getParseTypes()[0], start, end-start);
                }
                else {
                    checkState(m.groupCount() == parseing.getParseTypes().length, "Mismatch between parse types count [%s] and regex groups length [%s] ", parseing.getParseTypes().length, m.groupCount());
                    int groupStart = start;
                    for (int group = 1; group <= m.groupCount() ; group++) {
                        // We have a lexical match - do we have room?
                        int groupLength = m.group(group).length();
                        addLexicalMatchIfRoom(sections, parseing.getParseTypes()[group-1], groupStart, groupLength);
                        groupStart += groupLength;

                    }
                }

                searchFromIndex = m.start() +1;
            }

        });
        return sections;
    }

    private void addLexicalMatchIfRoom(Set<Section> sections, ParseType parseType, int start, int length) {
        log.debug("  Looking for room for lexical match [{}] [{},{}]", parseType, start, length);

        if (isSectionAvailable(start, length, sections)) {
            sections.add(ParsedCellFactory.buildSection(start, length, parseType));
            log.debug("   Adding section");
        }
        else {
            log.debug("   Not Adding section");
        }
    }

    private boolean isSectionAvailable(int start, int length, Set<Section> sections) {
        for (Section section : sections) {
            if (section.fallsWithin(start) ||
                    section.fallsWithin(start+length-1)) {
                return false;
            }
        }
        return true;
    }
}
