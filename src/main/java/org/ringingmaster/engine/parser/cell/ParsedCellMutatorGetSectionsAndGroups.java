package org.ringingmaster.engine.parser.cell;

import com.google.common.collect.Sets;
import org.pcollections.HashTreePSet;
import org.pcollections.PSet;
import org.ringingmaster.engine.parser.cell.grouping.Group;
import org.ringingmaster.engine.parser.cell.grouping.Section;

import java.util.Set;
import java.util.function.Function;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class ParsedCellMutatorGetSectionsAndGroups implements Function<ParsedCell, ParsedCellMutatorSectionsAndGroups> {

    @Override
    public ParsedCellMutatorSectionsAndGroups apply(ParsedCell prototype) {
        return new ParsedCellMutatorSectionsAndGroups(getPrototypeSections(prototype), getPrototypeGroups(prototype));
    }

    private PSet<Section> getPrototypeSections(ParsedCell prototype) {
        Set<Section> sections = Sets.newHashSet();
        for(int elementIndex = 0;elementIndex<prototype.getElementSize();elementIndex++) {
            prototype.getSectionAtElementIndex(elementIndex)
                    .ifPresent(sections::add);
        }
        return HashTreePSet.from(sections);
    }

    private PSet<Group> getPrototypeGroups(ParsedCell prototype) {
        Set<Group> groups = Sets.newHashSet();
        for(int elementIndex = 0;elementIndex<prototype.getElementSize();elementIndex++) {
            prototype.getGroupAtElementIndex(elementIndex)
                    .ifPresent(groups::add);
        }
        return HashTreePSet.from(groups);
    }
}
