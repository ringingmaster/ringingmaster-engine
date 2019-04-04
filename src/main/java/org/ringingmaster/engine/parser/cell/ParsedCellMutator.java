package org.ringingmaster.engine.parser.cell;

import org.ringingmaster.engine.parser.cell.grouping.Group;
import org.ringingmaster.engine.parser.cell.grouping.Section;

import java.util.Set;

public class    ParsedCellMutator {

    private ParsedCell prototype;

    private final ParsedCellMutatorGetSectionsAndGroups getSectionsAndGroups = new ParsedCellMutatorGetSectionsAndGroups();
    private final ParsedCellMutatonAddSectionToExistingGroup addSectionToExistingGroup = new ParsedCellMutatonAddSectionToExistingGroup();
    private final ParsedCellMutatorAddSectionGeneratingNewGroup addSectionGeneratingNewGroup = new ParsedCellMutatorAddSectionGeneratingNewGroup();
    private final ParsedCellMutatorWidenSection widenSection = new ParsedCellMutatorWidenSection();
    private final ParsedCellMutatorInvalidateGroups invalidateGroups = new ParsedCellMutatorInvalidateGroups();
    private final ParsedCellMutatorMergeGroups mergeGroups = new ParsedCellMutatorMergeGroups();

    public ParsedCell build() {

        ParsedCellMutatorSectionsAndGroups sectionsAndGroups =
                getSectionsAndGroups
                .andThen(addSectionToExistingGroup)
                .andThen(addSectionGeneratingNewGroup)
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

    public ParsedCellMutator addSectionAndGenerateMatchingNewGroup(Section section) {
        addSectionGeneratingNewGroup.addSectionAndGenerateMatchingGroup(section);
        return this;
    }

    public ParsedCellMutator addSectionIntoExistingGroup(Section section, int targetGroupElementIndex) {
        addSectionToExistingGroup.addSectionIntoGroup(section, targetGroupElementIndex);
        return this;
    }

    public ParsedCellMutator widenSectionRight(int sourceSectionElementIndex, int additionalElementCount) {
        widenSection.widenSectionRight(sourceSectionElementIndex, additionalElementCount);
        return this;
    }

    public ParsedCellMutator widenSectionLeft(int sourceSectionElementIndex, int additionalElementCount) {
        widenSection.widenSectionLeft(sourceSectionElementIndex, additionalElementCount);
        return this;
    }

    public ParsedCellMutator invalidateGroup(int sourceGroupElementIndex, String message) {
        invalidateGroups.invalidateGroup(sourceGroupElementIndex, message);
        return this;
    }

    public ParsedCellMutator mergeGroups(final Set<Group> candidateForMerge) {
        mergeGroups.merge( candidateForMerge);
        return this;
    }
}
