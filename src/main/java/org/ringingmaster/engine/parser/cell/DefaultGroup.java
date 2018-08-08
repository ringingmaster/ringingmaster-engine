package org.ringingmaster.engine.parser.cell;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import org.ringingmaster.engine.parser.assignparsetype.ParseType;

import javax.annotation.concurrent.Immutable;
import java.util.Collection;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * TODO comments???
 *
 * @author stevelake
 */
@Immutable
class DefaultGroup implements Group {

    private final int elementStartIndex;
    private final int elementLength;
    private final boolean valid;
    private final Optional<String> message;
    private final ImmutableList<Section> sections;

    DefaultGroup(int elementStartIndex, int elementLength, boolean valid, Optional<String> message, Collection<Section> sections) {
        this.elementStartIndex = elementStartIndex;
        this.elementLength = elementLength;
        this.valid = valid;
        this.message = checkNotNull(message);
        checkNotNull(sections);
        checkState(!sections.isEmpty()); // relied on in getFirstSectionParseType()
        this.sections = ImmutableList.sortedCopyOf(BY_START_INDEX, sections);
    }

    @Override
    public ImmutableList<Section> getSections() {
        return sections;
    }

    @Override
    public int getElementStartIndex() {
        return elementStartIndex;
    }

    @Override
    public int getElementLength() {
        return elementLength;
    }

    @Override
    public boolean fallsWithin(int elementIndex) {
        return elementIndex >= elementStartIndex &&
                elementIndex < elementStartIndex + elementLength;
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    public Optional<String> getMessage() {
        return message;
    }

    @Override
    public ParseType getFirstSectionParseType() {
        // we have a predicate that insists there is at least 1 section.
        return sections.get(0).getParseType();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultGroup that = (DefaultGroup) o;
        return getElementStartIndex() == that.getElementStartIndex() &&
                getElementLength() == that.getElementLength();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getElementStartIndex(), getElementLength());
    }

    @Override
    public String toString() {

        return "Group{" +
                "location=" + elementStartIndex +
                "/" + elementLength +
                ", valid=" + valid +
                ", message=" + message +
                ", sections=" + sections +
                '}';
    }
}
