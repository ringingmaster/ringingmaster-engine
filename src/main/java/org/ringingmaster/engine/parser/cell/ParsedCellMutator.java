package org.ringingmaster.engine.parser.cell;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.ringingmaster.engine.parser.cell.Section.BY_START_POSITION;

public class ParsedCellBuilder {

    private ParsedCell prototype;
    private final Map<Group, String> invalidGroupCandidates = new HashMap<>();
    private final Set<List<Group>> mergeGroups = new HashSet<>();

    public ParsedCell build() {
        // Has anything changed?
        if (invalidGroupCandidates.size() == 0 &&
                mergeGroups.size() == 0) {
            return prototype;
        }

        final Set<Section> allSections = getPrototypeSections();
        final Set<Group> allGroups  = getPrototypeGroups();
        final Set<Group> allGroupsWithInvalidApplied = applyInvalid(allGroups);
        final Set<List<Group>> mergeGroupsWithInvalidApplied = applyInvalidToMergeGroups(allGroupsWithInvalidApplied);
        final Set<Group> mergedGroups = doMerge(mergeGroupsWithInvalidApplied);
        final Set<Group> allGroupsWithMergeGroupsRemoved = removeOriginalMergeGroups(allGroupsWithInvalidApplied);
        final Set<Group> resultGroups = Sets.union(allGroupsWithMergeGroupsRemoved, mergedGroups);

        return ParsedCellFactory.buildParsedCell(((DefaultParsedCell)prototype).getParentCell(), allSections, resultGroups);

    }

    private Set<Section> getPrototypeSections() {
        Set<Section> sections = Sets.newHashSet();
        for(int elementIndex = 0;elementIndex<prototype.getElementSize();elementIndex++) {
            prototype.getSectionAtElementIndex(elementIndex)
                    .ifPresent(sections::add);
        }
        return sections;
    }

    private Set<Group> getPrototypeGroups() {
        Set<Group> groups = Sets.newHashSet();
        for(int elementIndex = 0;elementIndex<prototype.getElementSize();elementIndex++) {
            prototype.getGroupAtElementIndex(elementIndex)
                    .ifPresent(groups::add);
        }
        return groups;
    }

    /**
     * Re-create the Group objects that have been marked as Invalid with the invalid state
     */
    private Set<Group> applyInvalid(Set<Group> allGroups) {
        return allGroups.stream().map((originalGroup -> {
            if (invalidGroupCandidates.containsKey(originalGroup)) {
                final String message = originalGroup.getMessage()
                        .map((originalMessage) -> originalMessage + ", " + invalidGroupCandidates.get(originalGroup))
                        .orElse(invalidGroupCandidates.get(originalGroup));

                return new DefaultGroup(originalGroup.getElementStartIndex(), originalGroup.getElementLength(),
                        false, Optional.of(message), originalGroup.getSections());
            }
            return originalGroup;
        })).collect(Collectors.toSet());
    }

    /**
     * Replace the Group objects with ones that have potentially been transformed with Invalid
     */
    private Set<List<Group>> applyInvalidToMergeGroups(Set<Group> allGroupsWithInvalidApplied) {
        return mergeGroups.stream()
                .map(mergeGroupList -> {
                    // Lookup from the transformed invalid groups.
                    return mergeGroupList.stream()
                            .map(group -> allGroupsWithInvalidApplied.stream().filter((group::equals)).findFirst().get())
                            .collect(Collectors.toList());
                }).collect(Collectors.toSet());
    }

    /**
     * Merge each candidate pairs into a single group object
     */
    private Set<Group> doMerge(Set<List<Group>> mergeGroupsWithInvalidApplied) {
        return mergeGroupsWithInvalidApplied.stream()
                .map(mergeGroupList -> {

                    int startIndex = mergeGroupList.get(0).getElementStartIndex();
                    int elementLength = mergeGroupList.stream().mapToInt(Group::getElementLength).sum();
                    boolean valid = mergeGroupList.stream().allMatch(Group::isValid);
                    String message = mergeGroupList.stream().map(Group::getMessage).filter(Optional::isPresent)
                            .map(Optional::get).collect(Collectors.joining(","));
                    List<Section> sectionsForGroup = mergeGroupList.stream()
                            .flatMap(group -> group.getSections().stream())
                            .sorted(BY_START_POSITION).collect(Collectors.toList());

                    return new DefaultGroup(startIndex, elementLength, valid, (message.length()==0)?Optional.empty():Optional.of(message), sectionsForGroup);
                }).collect(Collectors.toSet());
    }

    private Set<Group> removeOriginalMergeGroups(Set<Group> allGroupsWithInvalidApplied) {
        return Sets.difference(allGroupsWithInvalidApplied,
                mergeGroups.stream().flatMap(Collection::stream).collect(Collectors.toSet()));
    }

    public ParsedCellBuilder prototypeOf(ParsedCell prototype) {

        this.prototype = prototype;
        return this;
    }

    public ParsedCellBuilder setInvalid(Group group, String message) {
        checkNotNull(group);
        checkNotNull(message);

        invalidGroupCandidates.put(group, message);

        return this;
    }

    public ParsedCellBuilder merge(final Group... candidateForMergeArray) {
        final List<Group> candidateForMerge = Arrays.asList(candidateForMergeArray);
        checkArgument(candidateForMerge.size() >= 2);

        // Check for duplicate groups
        checkArgument(Sets.newHashSet(candidateForMerge).size() == candidateForMerge.size(), "Duplicate Group in merge", candidateForMerge);

        candidateForMerge.sort(Group.BY_START_POSITION);

        // Check for non contiguous groups
        for (int i=0;i<candidateForMerge.size()-1;i++) {
            checkArgument(candidateForMerge.get(i).getElementStartIndex() + candidateForMerge.get(i).getElementLength() == candidateForMerge.get(i+1).getElementStartIndex(),
                    "Merging non contiguous blocks [%s], [%s]", candidateForMerge.get(i), candidateForMerge.get(i+1));
        }

        // Check a group does not appear in another merge request
        mergeGroups.stream()
                .flatMap(Collection::stream)
                .forEach(mergeMember -> {
                    for (Group potentialGroup : candidateForMerge) {
                        checkArgument(!Objects.equal(mergeMember, potentialGroup), "[%s] is already in a merge group", potentialGroup);
                    }
                });

        mergeGroups.add(Lists.newArrayList(candidateForMerge));

        return this;
    }

}
