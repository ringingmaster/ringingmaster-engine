package org.ringingmaster.engine.parsernew.cell;

import com.google.common.collect.ImmutableList;
import org.ringingmaster.engine.touch.newcontainer.definition.DefinitionCell;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class DefaultParsedDefinitionCell extends DefaultParsedCell implements ParsedDefinitionCell {

    private final DefinitionCell parentCell;

    DefaultParsedDefinitionCell(DefinitionCell parentCell, Section[] sectionByElementIndex, Group[] groupByElementIndex,
                                ImmutableList<Section> allSections, ImmutableList<Group> allGroups) {
        super(parentCell, sectionByElementIndex, groupByElementIndex, allSections, allGroups);
        this.parentCell = parentCell;
    }

    @Override
    public String getShorthand() {
        return parentCell.getShorthand();
    }
}
