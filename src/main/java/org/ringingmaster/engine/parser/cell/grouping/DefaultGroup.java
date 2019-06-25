package org.ringingmaster.engine.parser.cell.grouping;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import org.ringingmaster.engine.parser.assignparsetype.ParseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.concurrent.Immutable;
import java.util.Collection;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
@Immutable
class DefaultGroup implements Group {

    private final Logger log = LoggerFactory.getLogger(DefaultGroup.class);

    private final ElementRange elementRangeDelegate;
    private final boolean valid;
    private final ImmutableList<String> message;
    private final ImmutableList<Section> sections;

    /**
     * @param startIndex inclusive
     */
    DefaultGroup(int startIndex, int length, boolean valid, ImmutableList<String> message, Collection<Section> sections) {
        elementRangeDelegate = new DefaultElementRange(startIndex, length);;
        this.valid = valid;
        this.message = checkNotNull(message);
        checkNotNull(sections);
        checkArgument(!sections.isEmpty()); // relied on in getFirstSectionParseType()
        this.sections = ImmutableList.sortedCopyOf(BY_START_INDEX, sections);

        checkSectionBounds(startIndex, length);
    }

    private void checkSectionBounds(int startIndex, int length) {
        log.debug("Checking contiguous sections in group [{}/{}]", startIndex, length);
        int sectionStartIndex = startIndex;
        for (Section section : this.sections) {
            log.debug("Checking [{}]", section);
            checkArgument(section.getStartIndex() == sectionStartIndex, "Section [%s] does not align with %s [%s] ", section, ((sectionStartIndex == startIndex)?"group start":"previous section boundary"), sectionStartIndex);
            sectionStartIndex = section.getEndIndex();
        }
        checkArgument(sectionStartIndex == getEndIndex(), "Final section end [%s] does not align with group end [%s]", sectionStartIndex, getEndIndex());
    }

    @Override
    public ImmutableList<Section> getSections() {
        return sections;
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    public ImmutableList<String> getMessages() {
        return message;
    }

    @Override
    public ParseType getFirstSectionParseType() {
        // we have a predicate that insists there is at least 1 section.
        return sections.get(0).getParseType();
    }

    @Override
    public int getStartIndex() {
        return elementRangeDelegate.getStartIndex();
    }

    @Override
    public int getLength() {
        return elementRangeDelegate.getLength();
    }

    @Override
    public int getEndIndex() {
        return elementRangeDelegate.getEndIndex();
    }

    @Override
    public boolean fallsWithin(int otherIndex) {
        return elementRangeDelegate.fallsWithin(otherIndex);
    }

    @Override
    public boolean fallsWithin(ElementRange other) {
        return elementRangeDelegate.fallsWithin(other);
    }

    @Override
    public boolean intersection(int startIndex, int length) {
        return elementRangeDelegate.intersection(startIndex, length);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultGroup that = (DefaultGroup) o;
        return getStartIndex() == that.getStartIndex() &&
                getLength() == that.getLength();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getStartIndex(), getLength());
    }

    @Override
    public String toString() {

        return "Group{" +
                getStartIndex() +
                "/" + getLength() +
                ", valid=" + valid +
                ", message=" + message +
                ", sections=" + sections +
                '}';
    }
}
