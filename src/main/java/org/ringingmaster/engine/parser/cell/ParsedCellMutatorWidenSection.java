package org.ringingmaster.engine.parser.cell;

import javax.annotation.concurrent.Immutable;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class ParsedCellMutatorWidenSection implements Function<ParsedCellMutatorSectionsAndGroups, ParsedCellMutatorSectionsAndGroups> {

    private final Set<SectionWidening> sectionWidening = new HashSet<>();

    public void widenSection(Section sourceSection, int additionalElementIndex) {
        checkNotNull(sourceSection);
        checkArgument(additionalElementIndex >= 0);

        //TODO check new element is next to source section
        //TODO check new element does not overlap anything else.
        //TODO Should we have two methods - addBefore() and addAfter()
        sectionWidening.add(new SectionWidening(sourceSection.getElementStartIndex(), additionalElementIndex));
    }

    @Override
    public ParsedCellMutatorSectionsAndGroups apply(ParsedCellMutatorSectionsAndGroups source) {
        if (sectionWidening.size() == 0) {
            return source;
        }
        return null;
    }

    @Immutable
    private class SectionWidening {
        private final int sourceSectionElementIndex;
        private final int additionalElementIndex;

        private SectionWidening(int sourceSectionElementIndex, int additionalElementIndex) {
            this.sourceSectionElementIndex = sourceSectionElementIndex;
            this.additionalElementIndex = additionalElementIndex;
        }
    }
}
