package org.ringingmaster.engine.parser.assignparse;

import net.jcip.annotations.Immutable;
import org.ringingmaster.engine.parser.ParseType;
import org.ringingmaster.engine.parser.cell.ParsedCell;
import org.ringingmaster.engine.parser.cell.ParsedCellFactory;
import org.ringingmaster.engine.parser.cell.Section;
import org.ringingmaster.engine.touch.container.cell.Cell;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static com.google.common.base.Preconditions.checkState;

/**
 * TODO comments???
 *
 * @author stevelake
 */
@Immutable
class CellLexer {

    private static Comparator<String> SORT_SIZE_THEN_NAME = (o1, o2) -> {
        int result = (o2.length() - o1.length());
        if (result != 0) {
            return result;
        }
        return o1.compareTo(o2);
    };

    ParsedCell lexCell(Cell cell, Map<String, ParseType> parseMap) {
        Set<Section> sections = calculateSections(cell, parseMap);
        return ParsedCellFactory.buildParsedCell(cell, sections);
    }

    private Set<Section> calculateSections(Cell cell, Map<String, ParseType> parseMap) {
        final String cellAsString = cell.getCharacters();
        Set<Section> sections = new HashSet<>();

        TreeMap<String, ParseType> sortedParseMap = new TreeMap<>(SORT_SIZE_THEN_NAME);
        sortedParseMap.putAll(parseMap);
        sortedParseMap.forEach((token, parseType) -> {
            checkState(token.length() > 0, "Should never have an empty token. Mapped to [{}]", parseType);

            int searchFromIndex = 0;
            while ((searchFromIndex = cellAsString.indexOf(token, searchFromIndex)) != -1) {
                // We have a lexical match - do we have room?
                if (isSectionAvailable(searchFromIndex, token.length(), sections)) {
                    sections.add(ParsedCellFactory.buildSection(searchFromIndex, token.length(), parseType));
                    searchFromIndex += token.length();
                }
                else {
                    searchFromIndex++;
                }
            }

        });
        return sections;
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
