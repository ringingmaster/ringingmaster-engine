package org.ringingmaster.engine.touch.newcontainer;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.jcip.annotations.Immutable;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.method.Bell;
import org.ringingmaster.engine.method.MethodRow;
import org.ringingmaster.engine.method.Stroke;
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.notation.impl.NotationBuilderHelper;
import org.ringingmaster.engine.touch.container.TouchCheckingType;
import org.ringingmaster.engine.touch.container.TouchDefinition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Raw immutable POJO for a touch.
 *
 * @author Lake
 */
@Immutable
public class Touch {

    private final String title;
    private final String author;

    private final NumberOfBells numberOfBells;
    private final TouchCheckingType touchCheckingType;

    private final Bell callFromBell;
    private final List<NotationBody> sortedNotations;
    private final NotationBody nonSplicedActiveNotation;
    private final boolean spliced; // we use separate spliced and active-notation, rather than an optional because otherwise, adding your first notation will always be spliced.
    private final String plainLeadToken;
    private final Set<TouchDefinition> definitions;

    private final MethodRow startChange;
    private final int startAtRow;
    private final Stroke startStroke;
    private final Optional<NotationBody> startNotation;

    private final int terminationMaxRows;
    private final Optional<Integer> terminationMaxLeads;
    private final Optional<Integer> terminationMaxParts;
    private final Optional<Integer> terminationMaxCircularTouch;
    private final Optional<MethodRow> terminationChange;

    public Touch(String title,
                 String author,
                 NumberOfBells numberOfBells,
                 TouchCheckingType touchCheckingType,
                 Bell callFromBell,
                 List<NotationBody> sortedNotations,
                 NotationBody nonSplicedActiveNotation,
                 boolean spliced,
                 String plainLeadToken,
                 Set<TouchDefinition> definitions,
                 MethodRow startChange, int startAtRow,
                 Stroke startStroke, Optional<NotationBody> startNotation,
                 int terminationMaxRows, Optional<Integer> terminationMaxLeads,
                 Optional<Integer> terminationMaxParts,
                 Optional<Integer> terminationMaxCircularTouch,
                 Optional<MethodRow> terminationChange) {
        this.title = title;
        this.author = author;

        this.numberOfBells = numberOfBells;
        this.touchCheckingType = touchCheckingType;

        this.callFromBell = callFromBell;
        this.sortedNotations = new ArrayList<>(sortedNotations);
        this.nonSplicedActiveNotation = nonSplicedActiveNotation;
        this.spliced = spliced;
        this.plainLeadToken = plainLeadToken;
        this.definitions = definitions;

        this.startChange = startChange;
        this.startAtRow = startAtRow;
        this.startStroke = startStroke;
        this.startNotation = startNotation;

        this.terminationMaxRows = terminationMaxRows;
        this.terminationMaxLeads = terminationMaxLeads;
        this.terminationMaxParts = terminationMaxParts;
        this.terminationMaxCircularTouch = terminationMaxCircularTouch;
        this.terminationChange = terminationChange;
    }


    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public NumberOfBells getNumberOfBells() {
        return numberOfBells;
    }

    public TouchCheckingType getTouchCheckingType() {
        return touchCheckingType;
    }

    public Bell getCallFromBell() {
        return callFromBell;
    }

    public List<NotationBody> getAllNotations() {
        return Lists.newArrayList(sortedNotations);
    }

    public List<NotationBody> getValidNotations() {
        return NotationBuilderHelper.filterNotationsUptoNumberOfBells(sortedNotations, numberOfBells);
    }

    public List<NotationBody> getNotationsInUse() {
        if (isSpliced()) {
            return getValidNotations();
        }
        else {
            // Not Spliced
            if (nonSplicedActiveNotation != null) {
                return Lists.<NotationBody>newArrayList(nonSplicedActiveNotation);
            }
        }

        return Collections.emptyList();
    }

    public NotationBody getNonSplicedActiveNotation() {
        return nonSplicedActiveNotation;
    }

    public boolean isSpliced() {
        return spliced;
    }

    public String getPlainLeadToken() {
        return plainLeadToken;
    }

    public Set<TouchDefinition> getDefinitions() {
        return Sets.newHashSet(definitions);
    }

    public Optional<TouchDefinition> findDefinitionByShorthand(String name) {
        checkNotNull(name);

        return definitions.stream()
                .filter((definition) -> name.equals(definition.getShorthand()))
                .findFirst();
    }

    public MethodRow getStartChange() {
        return startChange;
    }

    public int getStartAtRow() {
        return startAtRow;
    }

    public Stroke getStartStroke() {
        return startStroke;
    }

    public Optional<NotationBody> getStartNotation() {
        return startNotation;
    }

    public int getTerminationMaxRows() {
        return terminationMaxRows;
    }

    public Optional<Integer> getTerminationMaxLeads() {
        return terminationMaxLeads;
    }

    public Optional<Integer> getTerminationMaxParts() {
        return terminationMaxParts;
    }

    public Optional<Integer> getTerminationMaxCircularTouch() {
        return terminationMaxCircularTouch;
    }

    public Optional<MethodRow> getTerminationChange() {
        return terminationChange;
    }

    @Override
    public String toString() {
        return "Touch{" +
                "title='" + title + '\'' +
                ", author=" + author +
                ", numberOfBells='" + numberOfBells + '\'' +
                ", touchType=" + touchCheckingType +
                ", callFromBell='" + callFromBell + '\'' +
                ", sortedNotations=" + sortedNotations +
                ", nonSplicedActiveNotation=" + nonSplicedActiveNotation +
                ", spliced=" + spliced +
                ", plainLeadToken='" + plainLeadToken + '\'' +
                ", definitions=" + definitions +
                ", startChange=" + startChange +
                ", startAtRow=" + startAtRow +
                ", startStroke=" + startStroke +
                ", startNotation=" + startNotation +
                ", terminationMaxRows=" + terminationMaxRows +
                ", terminationMaxLeads=" + terminationMaxLeads +
                ", terminationMaxParts=" + terminationMaxParts +
                ", terminationMaxCircularTouch=" + terminationMaxCircularTouch +
                ", terminationChange=" + terminationChange +
            //TODO    ", cells=" + cells +
                '}';
    }
}
