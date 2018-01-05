package org.ringingmaster.engine.parser.cell;

import java.util.Set;

public class ParsedCellMutator {

    private ParsedCell prototype;

    private final ParsedCellMutatorGetSectionsAndGroups getSectionsAndGroups = new ParsedCellMutatorGetSectionsAndGroups();
    private final ParsedCellMutatonAddSectionForExistingGroup newSectionForExistingGroup = new ParsedCellMutatonAddSectionForExistingGroup();
    private final ParsedCellMutatorAddSectionGeneratingNewGroup newSectionGeneratingNewGroup = new ParsedCellMutatorAddSectionGeneratingNewGroup();
    private final ParsedCellMutatorWidenSection widenSection = new ParsedCellMutatorWidenSection();
    private final ParsedCellMutatorInvalidateGroups invalidateGroups = new ParsedCellMutatorInvalidateGroups();
    private final ParsedCellMutatorMergeGroups mergeGroups = new ParsedCellMutatorMergeGroups();

    public ParsedCell build() {

        ParsedCellMutatorSectionsAndGroups sectionsAndGroups =
                getSectionsAndGroups
                .andThen(newSectionForExistingGroup)
                .andThen(newSectionGeneratingNewGroup)
                .andThen(widenSection)
                .andThen(invalidateGroups)
                .andThen(mergeGroups)
                .apply(prototype);

        // rebuild parsed cell
        return ParsedCellFactory.buildParsedCell(prototype.getParentCell(), sectionsAndGroups.getSections(), sectionsAndGroups.getGroups());
    }

    public ParsedCellMutator prototypeOf(ParsedCell prototype) {
        this.prototype = prototype;
        return this;
    }

    public ParsedCellMutator addSectionAndGenerateNewGroup(Section section) {
        newSectionGeneratingNewGroup.addSectionIntoGroup(section);
        return this;
    }

    public ParsedCellMutator addSectionIntoGroup(Section section, Group targetGroup) {
        newSectionForExistingGroup.addSectionIntoGroup(section, targetGroup);
        return this;
    }

    public ParsedCellMutator widenSectionRight(Section sourceSection, int additionalElementCount) {
        widenSection.widenSectionRight(sourceSection, additionalElementCount);
        return this;
    }

    public ParsedCellMutator widenSectionLeft(Section sourceSection, int additionalElementCount) {
        widenSection.widenSectionLeft(sourceSection, additionalElementCount);
        return this;
    }

    public ParsedCellMutator invalidateGroup(Group group, String message) {
        invalidateGroups.invalidateGroup(group, message);
        return this;
    }

    public ParsedCellMutator mergeGroups(final Set<Group> candidateForMerge) {
        mergeGroups.merge( candidateForMerge);
        return this;
    }
}
