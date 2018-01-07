package org.ringingmaster.engine.parser.cell;

import org.pcollections.PSet;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class ParsedCellMutatorAddSectionGeneratingNewGroup implements Function<ParsedCellMutatorSectionsAndGroups, ParsedCellMutatorSectionsAndGroups> {

    private final Set<Section> newSection = new HashSet<>();

    public void addSectionAndGenerateMatchingGroup(Section section) {
        checkNotNull(section);

        newSection.add(section);
    }

    @Override
    public ParsedCellMutatorSectionsAndGroups apply(ParsedCellMutatorSectionsAndGroups source) {
        if (newSection.size() == 0) {
            return source;
        }

        PSet<Section> sections = source.getSections().plusAll(newSection);
        PSet<Group> groups = source.getGroups();

        for (Section section : newSection) {
            final Group groupForSection = ParsedCellFactory.buildGroupToMatchSection(section);
            groups = groups.plus(groupForSection);
        }

        return new ParsedCellMutatorSectionsAndGroups(sections, groups);
    }
}
