package org.ringingmaster.engine.parsernew.cell;

import org.ringingmaster.engine.parser.ParseType;
import org.ringingmaster.engine.touch.newcontainer.cell.Cell;
import org.ringingmaster.engine.touch.newcontainer.definition.DefinitionCell;

import java.util.Set;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class ParsedCellFactory {

    public static ParsedCell buildParsedCell(Cell cell, Set<Section> sections) {
        Section[] sectionByElement = new Section[cell.getElementSize()];
        Group[] groupByElement = new Group[cell.getElementSize()];
        createSectionIndexAndGroupIndex(sections, sectionByElement, groupByElement);

        return new DefaultParsedCell(cell, sectionByElement, groupByElement);
    }

    public static ParsedDefinitionCell buildParsedCell(DefinitionCell cell, Set<Section> sections) {
        Section[] sectionByElement = new Section[cell.getElementSize()];
        Group[] groupByElement = new Group[cell.getElementSize()];
        createSectionIndexAndGroupIndex(sections, sectionByElement, groupByElement);

        return new DefaultParsedDefinitionCell(cell, sectionByElement, groupByElement);
    }

    private static void createSectionIndexAndGroupIndex(Set<Section> sections, Section[] sectionByElement, Group[] groupByElement) {
        for (Section section : sections) {
            Group group = new DefaultGroup(section.getElementStartIndex(), section.getElementLength(), section) ;
            for (int index = section.getElementStartIndex(); index < section.getElementStartIndex()+section.getElementLength() ; index++) {
                sectionByElement[index] = section;
                groupByElement[index] = group;
            }
        }
    }

    public static Section buildSection(int elementStartIndex, int elementLength, ParseType parseType) {
        return new DefaultSection(elementStartIndex, elementLength, parseType);
    }
}
