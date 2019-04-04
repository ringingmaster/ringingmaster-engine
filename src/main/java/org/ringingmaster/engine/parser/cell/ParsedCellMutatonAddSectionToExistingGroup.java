package org.ringingmaster.engine.parser.cell;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import org.pcollections.PSet;
import org.ringingmaster.engine.parser.cell.grouping.Group;
import org.ringingmaster.engine.parser.cell.grouping.GroupingFactory;
import org.ringingmaster.engine.parser.cell.grouping.Section;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class ParsedCellMutatonAddSectionToExistingGroup implements Function<ParsedCellMutatorSectionsAndGroups, ParsedCellMutatorSectionsAndGroups> {

    private final Map<Section, Integer> newSectionForGroupAtIndex = new HashMap<>();


    public void addSectionIntoGroup(Section section, int targetGroupElementIndex) {
        checkNotNull(section);
        checkArgument(targetGroupElementIndex > 0);

        newSectionForGroupAtIndex.put(section, targetGroupElementIndex);
    }

    @Override
    public ParsedCellMutatorSectionsAndGroups apply(ParsedCellMutatorSectionsAndGroups source) {

        if (newSectionForGroupAtIndex.size() == 0) {
            return source;
        }

        final PSet<Section> sections = source.getSections().plusAll(newSectionForGroupAtIndex.keySet());
        PSet<Group> groups = source.getGroups();

        final Map<Group, Set<Group>> forMerge = new HashMap<>();

        for (Map.Entry<Section, Integer> sectionGroupEntry : newSectionForGroupAtIndex.entrySet()) {
            final Group groupForIndex = getGroupForIndex(groups, sectionGroupEntry.getValue());

            final Set<Group> groupSet = forMerge.computeIfAbsent(groupForIndex, (group) -> {
                Set<Group> setForMerge = new HashSet<>();
                setForMerge.add(groupForIndex);
                return setForMerge;
            });

            final Group groupForSection = GroupingFactory.buildGroupToMatchSection(sectionGroupEntry.getKey());
            groups = groups.plus(groupForSection);
            groupSet.add(groupForSection);
        }

        // Where we have the following distinct but overlapping merege operations
        // NEW_GROUP_1 > EXISTING_GROUP
        // NEW_GROUP_2 > NEW_GROUP_1
        // We need to consolidate them to a single merge group of 3 items.
        // [EXISTING_GROUP, NEW_GROUP_1, NEW_GROUP_2]
        final Set<Set<Group>> consolidatedMergeGroups = new HashSet<>();
        for (Set<Group> setForMerge : forMerge.values()) {
            // do any of the groups appear in an existing consolidated group?
            final Set<Set<Group>> foundInConsolidatedGroup = consolidatedMergeGroups.stream()
                    .filter(consolidatedSet -> (Sets.intersection(setForMerge, consolidatedSet).size() > 0))
                    .collect(Collectors.toSet());
            if (foundInConsolidatedGroup.size() == 0) {
                // Add to consolidated as new group
                consolidatedMergeGroups.add(setForMerge);
            }
            else if (foundInConsolidatedGroup.size() == 1) {
                // Add to existing consolidated group
                Iterables.getOnlyElement(foundInConsolidatedGroup).addAll(setForMerge);
            }
            else {
                throw new IllegalStateException();
            }
        }


        final ParsedCellMutatorMergeGroups mergeGroupsHelper = new ParsedCellMutatorMergeGroups();
        for (Set<Group> groupsForMerge : consolidatedMergeGroups) {
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
