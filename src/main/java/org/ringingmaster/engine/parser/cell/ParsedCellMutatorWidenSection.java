package org.ringingmaster.engine.parser.cell;

import com.google.common.collect.Sets;

import org.pcollections.PSet;

import javax.annotation.concurrent.Immutable;
import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Map;
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

    public void widenSectionRight(int sourceSectionElementIndex, int additionalElementCount) {
        checkArgument(sourceSectionElementIndex >= 0);
        checkArgument(additionalElementCount >= 0);

        sectionWidenings.add(new SectionWidening(sourceSectionElementIndex, additionalElementCount, Side.RIGHT));
    }

    public void widenSectionLeft(int sourceSectionElementIndex, int additionalElementCount) {
        checkArgument(sourceSectionElementIndex >= 0);
        checkArgument(additionalElementCount >= 0);

        sectionWidenings.add(new SectionWidening(sourceSectionElementIndex, additionalElementCount, Side.LEFT));
    }

    @Override
    public ParsedCellMutatorSectionsAndGroups apply(ParsedCellMutatorSectionsAndGroups source) {
        if (sectionWidenings.size() == 0) {
            return source;
        }

        final Set<SectionWidening> mergedSectionWidenings = mergeSectionWidenings(source.getSections());

        final Set<Section> widenedSections = mergedSectionWidenings.stream()
                .map(sectionWidening -> {
                    final Section sectionForIndex = getSectionForIndex(source.getSections(), sectionWidening.sourceSectionElementIndex);
                    checkNotNull(sectionForIndex);
                    return ParsedCellFactory.buildSection(
                            sectionWidening.side == Side.LEFT ? sectionForIndex.getElementStartIndex() - sectionWidening.additionalElementCount : sectionForIndex.getElementStartIndex(),
                            sectionForIndex.getElementLength() + sectionWidening.additionalElementCount,
                            sectionForIndex.getParseType());

                })
                .collect(Collectors.toSet());

        final Set<Section> consumedSections = mergedSectionWidenings.stream()
                .map(sectionWidening -> getSectionForIndex(source.getSections(), sectionWidening.sourceSectionElementIndex))
                .collect(Collectors.toSet());

        final PSet<Section> sections = source.getSections()
                .minusAll(consumedSections)
                .plusAll(widenedSections);

        final Set<Group> widenedGroups = mergedSectionWidenings.stream()
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

        final Set<Group> consumedGroups = mergedSectionWidenings.stream()
                .map(sectionWidening -> getGroupForIndex(source.getGroups(), sectionWidening.sourceSectionElementIndex))
                .collect(Collectors.toSet());

        final PSet<Group> groups = source.getGroups()
                .minusAll(consumedGroups)
                .plusAll(widenedGroups);

        return new ParsedCellMutatorSectionsAndGroups(sections, groups);
    }

    private Set<SectionWidening> mergeSectionWidenings(final PSet<Section> sections) {
        final Map<SectionWidening, Section> wideningsWithSections = sectionWidenings.stream()
                .map(widening -> new AbstractMap.SimpleEntry<>(widening, getSectionForIndex(sections, widening.sourceSectionElementIndex)))
                .filter(entry -> entry.getValue() != null)
                .collect(Collectors.toMap(
                        AbstractMap.SimpleEntry::getKey,
                        AbstractMap.SimpleEntry::getValue));

        final Set<SectionWidening> wideningsToBeMerged =
                Sets.newHashSet(Sets.difference(this.sectionWidenings, wideningsWithSections.keySet()));

        int lastLoopCount = Integer.MAX_VALUE;
        while (wideningsToBeMerged.size() > 0 && wideningsToBeMerged.size() < lastLoopCount) {
            lastLoopCount = wideningsToBeMerged.size();
            for (SectionWidening wideningToBeMerged : wideningsToBeMerged) {
                if (merge(wideningToBeMerged, wideningsWithSections)) {
                    wideningsToBeMerged.remove(wideningToBeMerged);
                    break;
                }
            }
        }

        if (wideningsToBeMerged.size() > 0 ) {
            throw new IllegalStateException("Widenings [" + wideningsToBeMerged + "] have no associated section.");
        }

        return wideningsWithSections.keySet();
    }

    private boolean merge(SectionWidening wideningToBeMerged, Map<SectionWidening, Section> wideningsWithSections) {
        for (Map.Entry<SectionWidening, Section> entry : wideningsWithSections.entrySet()) {
            if (wideningToBeMerged.side != entry.getKey().side) {
                break;
            }
            if (fallsWithin(entry, wideningToBeMerged)) {
                wideningsWithSections.remove(entry.getKey());
                final SectionWidening newSectoinWidening = new SectionWidening(entry.getKey().sourceSectionElementIndex, entry.getKey().additionalElementCount + wideningToBeMerged.additionalElementCount, entry.getKey().side);
                wideningsWithSections.put(newSectoinWidening, entry.getValue());
                return true;
            }
        }
        return false;
    }

    public boolean fallsWithin(Map.Entry<SectionWidening, Section> entry, SectionWidening wideningToBeMerged) {
        final int elementIndex = wideningToBeMerged.sourceSectionElementIndex;
        int elementStartIndex = (entry.getKey().side == Side.LEFT)?entry.getValue().getElementStartIndex() - entry.getKey().additionalElementCount: entry.getValue().getElementStartIndex();
        int elementLength = entry.getKey().additionalElementCount + entry.getValue().getElementLength();
        return elementIndex >= elementStartIndex &&
                elementIndex < elementStartIndex + elementLength;
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

        @Override
        public String toString() {
            return "SectionWidening{" +
                    "sourceIndex=" + sourceSectionElementIndex +
                    " +" + additionalElementCount +
                    "," + side +
                    '}';
        }
    }

    private enum Side {
        RIGHT,
        LEFT
    }

}
