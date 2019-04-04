package org.ringingmaster.engine.parser.cell.grouping;

import com.google.common.collect.Sets;
import org.ringingmaster.engine.parser.assignparsetype.ParseType;

import java.util.Collection;
import java.util.Optional;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
public class GroupingFactory {

    public static Group buildGroupToMatchSection(Section section) {
        return buildGroup(section.getStartIndex(), section.getLength(), true, Optional.empty(), Sets.newHashSet(section));
    }

    public static Section buildSection(int elementStartIndex, int elementLength, ParseType parseType) {
        return new DefaultSection(elementStartIndex, elementLength, parseType);
    }

    public static Group buildGroup(int elementStartIndex, int elementLength, boolean valid, Optional<String> message, Collection<Section> sections) {

        return new DefaultGroup(elementStartIndex, elementLength, valid, message, sections);
    }
}
