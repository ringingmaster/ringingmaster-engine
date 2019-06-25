package org.ringingmaster.engine.parser.cell.grouping;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import org.ringingmaster.engine.parser.assignparsetype.ParseType;

import java.util.Collection;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
public class GroupingFactory {

    public static Group buildGroupToMatchSection(Section section) {
        return buildGroup(section.getStartIndex(), section.getLength(), true, ImmutableList.of(), Sets.newHashSet(section));
    }

    public static Section buildSection(int elementStartIndex, int elementLength, ParseType parseType) {
        return new DefaultSection(elementStartIndex, elementLength, parseType);
    }

    public static Group buildGroup(int elementStartIndex, int elementLength, boolean valid, ImmutableList<String> message, Collection<Section> sections) {
        return new DefaultGroup(elementStartIndex, elementLength, valid, message, sections);
    }
}
