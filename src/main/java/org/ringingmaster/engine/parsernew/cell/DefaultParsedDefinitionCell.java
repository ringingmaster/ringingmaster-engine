package org.ringingmaster.engine.parsernew.cell;

import org.ringingmaster.engine.touch.newcontainer.definition.DefinitionCell;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class DefaultParsedDefinitionCell extends DefaultParsedCell implements ParsedDefinitionCell {

    private final DefinitionCell parentCell;

    DefaultParsedDefinitionCell(DefinitionCell parentCell, Section[] sectionByElement, Group[] groupByElement) {
        super(parentCell, sectionByElement, groupByElement);
        this.parentCell = parentCell;
    }

    @Override
    public String getShorthand() {
        return parentCell.getShorthand();
    }
}
