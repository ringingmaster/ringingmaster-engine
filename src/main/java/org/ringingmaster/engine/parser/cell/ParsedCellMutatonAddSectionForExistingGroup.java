package org.ringingmaster.engine.parser.cell;

import org.pcollections.PSet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class ParsedCellMutatonAddSectionForExistingGroup implements Function<ParsedCellMutatorSectionsAndGroups, ParsedCellMutatorSectionsAndGroups> {

    private final Map<Section, Integer> newSectionForGroupAtIndex = new HashMap<>();


    public void addSectionIntoGroup(Section section, Group targetGroup) {
        checkNotNull(section);
        checkNotNull(targetGroup);

        newSectionForGroupAtIndex.put(section, targetGroup.getElementStartIndex());
    }

    @Override
    public ParsedCellMutatorSectionsAndGroups apply(ParsedCellMutatorSectionsAndGroups source) {

        if (newSectionForGroupAtIndex.size() == 0) {
            return source;
        }

        final PSet<Section> sections = source.getSections().plusAll(newSectionForGroupAtIndex.keySet());
        PSet<Group> groups = source.getGroups();

        Map<Group, Set<Group>> forMerge = new HashMap<>();

        for (Map.Entry<Section, Integer> sectionGroupEntry : newSectionForGroupAtIndex.entrySet()) {
            final Group groupForIndex = getGroupForIndex(groups, sectionGroupEntry.getValue());

            final Set<Group> groupSet = forMerge.computeIfAbsent(groupForIndex, (group) -> {
                Set<Group> setForMerge = new HashSet<>();
                setForMerge.add(groupForIndex);
                return setForMerge;
            });

            final Group groupForSection = ParsedCellFactory.buildGroupToMatchSection(sectionGroupEntry.getKey());
            groups = groups.plus(groupForSection);
            groupSet.add(groupForSection);
        }

        ParsedCellMutatorMergeGroups mergeGroupsHelper = new ParsedCellMutatorMergeGroups();
        for (Set<Group> groupsForMerge : forMerge.values()) {
            mergeGroupsHelper.merge(groupsForMerge);
        }

        return mergeGroupsHelper.apply(new ParsedCellMutatorSectionsAndGroups(sections, groups));
    }

    private Group getGroupForIndex(Set<Group> allGroups, int index) {
        for (Group group : allGroups) {
            if (group.fallsWithin(index)) {
                return group;
            }
        }
        return null;
    }
}
