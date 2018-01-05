package org.ringingmaster.engine.parser.cell;

import org.pcollections.PSet;

import javax.annotation.concurrent.Immutable;
import java.util.HashSet;
import java.util.OptionalInt;
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
public class ParsedCellMutatorWidenSection implements Function<ParsedCellMutatorSectionsAndGroups, ParsedCellMutatorSectionsAndGroups> {

    private final Set<SectionWidening> sectionWidenings = new HashSet<>();

    public void widenSectionRight(Section sourceSection, int additionalElementCount) {
        checkNotNull(sourceSection);
        checkArgument(additionalElementCount >= 0);

        sectionWidenings.add(new SectionWidening(sourceSection.getElementStartIndex(), additionalElementCount, Side.RIGHT));
    }

    public void widenSectionLeft(Section sourceSection, int additionalElementCount) {
        checkNotNull(sourceSection);
        checkArgument(additionalElementCount >= 0);

        sectionWidenings.add(new SectionWidening(sourceSection.getElementStartIndex(), additionalElementCount, Side.LEFT));
    }

    @Override
    public ParsedCellMutatorSectionsAndGroups apply(ParsedCellMutatorSectionsAndGroups source) {
        if (sectionWidenings.size() == 0) {
            return source;
        }

        final Set<Section> widenedSections = sectionWidenings.stream()
                .map(sectionWidening -> {
                    final Section sectionForIndex = getSectionForIndex(source.getSections(), sectionWidening.sourceSectionElementIndex);
                    checkNotNull(sectionForIndex);
                    return ParsedCellFactory.buildSection(
                            sectionWidening.side == Side.LEFT ? sectionForIndex.getElementStartIndex() - sectionWidening.additionalElementCount : sectionForIndex.getElementStartIndex(),
                            sectionForIndex.getElementLength() + sectionWidening.additionalElementCount,
                            sectionForIndex.getParseType());

                })
                .collect(Collectors.toSet());

        final Set<Section> consumedSections = sectionWidenings.stream()
                .map(sectionWidening -> getSectionForIndex(source.getSections(), sectionWidening.sourceSectionElementIndex))
                .collect(Collectors.toSet());

        final PSet<Section> sections = source.getSections()
                .minusAll(consumedSections)
                .plusAll(widenedSections);

        final Set<Group> widenedGroups = sectionWidenings.stream()
                .map(sectionWidening -> getGroupForIndex(source.getGroups(), sectionWidening.sourceSectionElementIndex))
                .map(group -> {
                    final Set<Section> newSections = group.getSections().stream()
                            .map(oldSection -> getSectionForIndex(sections, oldSection.getElementStartIndex()))
                            .collect(Collectors.toSet());
                    final OptionalInt startIndex = newSections.stream().mapToInt(ElementSequence::getElementStartIndex).min();
                    final OptionalInt endIndex = newSections.stream().mapToInt(section -> section.getElementStartIndex() + section.getElementLength()).max();

                    return ParsedCellFactory.buildGroup(startIndex.getAsInt(), endIndex.getAsInt()-startIndex.getAsInt(),
                            group.isValid(), group.getMessage(),  newSections);
                })
                .collect(Collectors.toSet());

        final Set<Group> consumedGroups = sectionWidenings.stream()
                .map(sectionWidening -> getGroupForIndex(source.getGroups(), sectionWidening.sourceSectionElementIndex))
                .collect(Collectors.toSet());

        final PSet<Group> groups = source.getGroups()
                .minusAll(consumedGroups)
                .plusAll(widenedGroups);

        return new ParsedCellMutatorSectionsAndGroups(sections, groups);
    }

    private Section getSectionForIndex(Set<Section> allSections, int index) {
        for (Section section : allSections) {
            if (section.fallsWithin(index)) {
                return section;
            }
        }
        return null;
    }

    private Group getGroupForIndex(Set<Group> allGroups, int index) {
        for (Group group : allGroups) {
            if (group.fallsWithin(index)) {
                return group;
            }
        }
        return null;
    }

    @Immutable
    private class SectionWidening {
        private final int sourceSectionElementIndex;
        private final int additionalElementCount;
        private final Side side;

        private SectionWidening(int sourceSectionElementIndex, int additionalElementCount, Side side) {
            this.sourceSectionElementIndex = sourceSectionElementIndex;
            this.additionalElementCount = additionalElementCount;
            this.side = side;
        }
    }

    private enum Side {
        RIGHT,
        LEFT
    }

}
