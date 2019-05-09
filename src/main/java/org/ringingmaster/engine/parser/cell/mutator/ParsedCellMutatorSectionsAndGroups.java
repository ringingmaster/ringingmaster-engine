package org.ringingmaster.engine.parser.cell.mutator;

import org.pcollections.PSet;
import org.ringingmaster.engine.parser.cell.grouping.Group;
import org.ringingmaster.engine.parser.cell.grouping.Section;

import javax.annotation.concurrent.Immutable;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
@Immutable
public class ParsedCellMutatorSectionsAndGroups {

    private final PSet<Section> section;
    private final PSet<Group> group;

    ParsedCellMutatorSectionsAndGroups(PSet<Section> section, PSet<Group> group) {
        this.section = section;
        this.group = group;
    }

    public PSet<Section> getSections() {
        return section;
    }

    public PSet<Group> getGroups() {
        return group;
    }
}
