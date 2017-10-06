package org.ringingmaster.engine.parsernew.assignparsetype;

import org.ringingmaster.engine.parser.ParseType;
import org.ringingmaster.engine.touch.newcontainer.cell.Cell;

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
public class CellLexer {

    private static Comparator<String> SORT_SIZE_THEN_NAME = (o1, o2) -> {
        int result = (o2.length() - o1.length());
        if (result != 0) {
            return result;
        }
        return o1.compareTo(o2);
    };

    public ParsedCell parseCell(Cell cell, Map<String, ParseType> parseMap) {


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
                    sections.add(new DefaultSection(searchFromIndex, token.length(), parseType));
                    searchFromIndex += token.length();
                }
                else {
                    searchFromIndex++;
                }
            }

        });

        Section[] sectionByElement = new Section[cell.size()];
        Group[] groupByElement = new Group[cell.size()];
        for (Section section : sections) {
            Group group = new DefaultGroup(section.getElementStartIndex(), section.getElementLength(), section) ;
            for (int index = section.getElementStartIndex(); index < section.getElementStartIndex()+section.getElementLength() ; index++) {
                sectionByElement[index] = section;
                groupByElement[index] = group;
            }
        }

        return new DefaultParsedCell(cell, sectionByElement, groupByElement);
    }

    public boolean isSectionAvailable(int start, int length, Set<Section> sections) {
        for (Section section : sections) {
            if (section.fallsWithin(start) ||
                    section.fallsWithin(start+length)) {
                return false;
            }
        }
        return true;
    }

}
