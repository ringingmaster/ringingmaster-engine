package org.ringingmaster.engine.parser.cell.mutator;

import com.google.common.collect.ImmutableList;
import org.pcollections.HashTreePSet;
import org.ringingmaster.engine.parser.cell.grouping.Group;
import org.ringingmaster.engine.parser.cell.grouping.GroupingFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
public class ParsedCellMutatorGroups implements Function<ParsedCellMutatorSectionsAndGroups, ParsedCellMutatorSectionsAndGroups> {

    private boolean invalidating;
    private final Map<Integer, String> invalidGroupIndexCandidates = new HashMap<>();

    public ParsedCellMutatorGroups(boolean invalidating) {
        this.invalidating = invalidating;
    }

    public void invalidateGroup(int sourceGroupElementIndex, String message) {
        checkArgument(sourceGroupElementIndex >= 0);
        checkArgument(!isNullOrEmpty(message));

        invalidGroupIndexCandidates.put(sourceGroupElementIndex, message);
    }


    @Override
    public ParsedCellMutatorSectionsAndGroups apply(ParsedCellMutatorSectionsAndGroups source) {

        if (invalidGroupIndexCandidates.size() == 0) {
            return source;
        }

        final Map<Group, String> invalidGroupCandidates = invalidGroupIndexCandidates.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> getGroupForIndex(source.getGroups(), e.getKey()),
                        Map.Entry::getValue));

        final Set<Group> groups = source.getGroups().stream().map(originalGroup -> {
            if (invalidGroupCandidates.containsKey(originalGroup)) {
                ImmutableList.Builder<String> builder = ImmutableList.builder();
                builder.addAll(originalGroup.getMessages());
                builder.add(invalidGroupCandidates.get(originalGroup));

                boolean valid = invalidating ? false : originalGroup.isValid();

                return GroupingFactory.buildGroup(originalGroup.getStartIndex(), originalGroup.getLength(),
                        valid, builder.build(), originalGroup.getSections());
            }
            return originalGroup;
        }).collect(Collectors.toSet());

        return new ParsedCellMutatorSectionsAndGroups(source.getSections(), HashTreePSet.from(groups));
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
