package org.ringingmaster.engine.parser.cell;

import com.google.common.base.Objects;
import org.pcollections.PSet;
import org.ringingmaster.engine.parser.cell.grouping.Group;
import org.ringingmaster.engine.parser.cell.grouping.GroupingFactory;
import org.ringingmaster.engine.parser.cell.grouping.Section;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class ParsedCellMutatorMergeGroups implements Function<ParsedCellMutatorSectionsAndGroups, ParsedCellMutatorSectionsAndGroups> {

    private final Set<List<Integer>> mergeGroups = new HashSet<>();

    public void merge(final Set<Group> candidateForMergeUnsorted) {
        checkArgument(candidateForMergeUnsorted.size() >= 2);

        List<Group> candidateForMerge = new ArrayList<>(candidateForMergeUnsorted);
        candidateForMerge.sort(Group.BY_START_INDEX);

        // Check for non contiguous groups
        for (int i=0;i<candidateForMerge.size()-1;i++) {
            checkArgument(candidateForMerge.get(i).getStartIndex() + candidateForMerge.get(i).getLength() == candidateForMerge.get(i+1).getStartIndex(),
                    "Merging non contiguous blocks [%s], [%s]", candidateForMerge.get(i), candidateForMerge.get(i+1));
        }

        // Check a group does not appear in another merge request
        mergeGroups.stream()
                .flatMap(Collection::stream)
                .forEach(existingMergeMember -> {
                    for (Group potentialGroup : candidateForMerge) {
                        checkArgument(!Objects.equal(existingMergeMember, potentialGroup.getStartIndex()), "[%s] is already in a merge group", potentialGroup);
                    }
                });

        // Convert to element start position
        final List<Integer> candidateForMergeElementIndex = candidateForMerge.stream()
                .map(Group::getStartIndex)
                .collect(Collectors.toList());

        mergeGroups.add(candidateForMergeElementIndex);
    }

    @Override
    public ParsedCellMutatorSectionsAndGroups apply(ParsedCellMutatorSectionsAndGroups source) {
        if (mergeGroups.size() == 0) {
            return source;
        }

        final Set<Group> mergeAppiedGroups = mergeGroups.stream()
                .map((indexes) -> getGroupsForIndexes(source.getGroups(), indexes))
                .map(mergeGroupList -> {

                    int startIndex = mergeGroupList.get(0).getStartIndex();
                    int elementLength = mergeGroupList.stream().mapToInt(Group::getLength).sum();
                    boolean valid = mergeGroupList.stream().allMatch(Group::isValid);
                    String message = mergeGroupList.stream().map(Group::getMessage).filter(Optional::isPresent)
                            .map(Optional::get).collect(Collectors.joining(","));
                    Set<Section> sectionsForGroup = mergeGroupList.stream()
                            .flatMap(group -> group.getSections().stream())
                            .collect(Collectors.toSet());

                    return GroupingFactory.buildGroup(startIndex, elementLength, valid, (message.length() == 0) ? Optional.empty() : Optional.of(message), sectionsForGroup);
                }).collect(Collectors.toSet());

        final Set<Group> consumedGroups = mergeGroups.stream()
                .flatMap(Collection::stream)
                .distinct()
                .map(index -> getGroupForIndex(source.getGroups(), index))
                .collect(Collectors.toSet());

        final PSet<Group> groups = source.getGroups()
                .minusAll(consumedGroups)
                .plusAll(mergeAppiedGroups);

        return new ParsedCellMutatorSectionsAndGroups(source.getSections(), groups);
    }

    private List<Group> getGroupsForIndexes(Set<Group> allGroups, List<Integer> indexes) {
        return indexes.stream()
                .map(index -> getGroupForIndex(allGroups, index))
                .collect(Collectors.toList());
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
