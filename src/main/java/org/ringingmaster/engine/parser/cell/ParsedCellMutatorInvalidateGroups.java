package org.ringingmaster.engine.parser.cell;

import org.pcollections.HashTreePSet;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class ParsedCellMutatorInvalidateGroups implements Function<ParsedCellMutatorSectionsAndGroups, ParsedCellMutatorSectionsAndGroups> {

    private final Map<Group, String> invalidGroupCandidates = new HashMap<>();

    public void invalidateGroup(Group group, String message) {
        checkNotNull(group);
        checkNotNull(message);

        invalidGroupCandidates.put(group, message);
    }


    @Override
    public ParsedCellMutatorSectionsAndGroups apply(ParsedCellMutatorSectionsAndGroups source) {

        if (invalidGroupCandidates.size() == 0) {
            return source;
        }

        final Set<Group> groups = source.getGroups().stream().map((originalGroup -> {
            if (invalidGroupCandidates.containsKey(originalGroup)) {
                final String message = originalGroup.getMessage()
                        .map((originalMessage) -> originalMessage + ", " + invalidGroupCandidates.get(originalGroup))
                        .orElse(invalidGroupCandidates.get(originalGroup));

                return ParsedCellFactory.buildGroup(originalGroup.getElementStartIndex(), originalGroup.getElementLength(),
                        false, Optional.of(message), originalGroup.getSections());
            }
            return originalGroup;
        })).collect(Collectors.toSet());

        return new ParsedCellMutatorSectionsAndGroups(source.getSections(), HashTreePSet.from(groups));
    }
}
