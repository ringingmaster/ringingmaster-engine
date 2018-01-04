package org.ringingmaster.engine.parser.cell;

import org.pcollections.PSet;

import javax.annotation.concurrent.Immutable;

/**
 * TODO comments???
 *
 * @author stevelake
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
