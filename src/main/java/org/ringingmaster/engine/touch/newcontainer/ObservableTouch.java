package org.ringingmaster.engine.touch.newcontainer;


import com.google.common.base.Strings;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.method.MethodRow;
import org.ringingmaster.engine.method.impl.MethodBuilder;
import org.ringingmaster.engine.notation.Notation;
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.notation.impl.NotationBuilder;
import org.ringingmaster.engine.touch.newcontainer.definition.Definition;
import org.ringingmaster.engine.touch.newcontainer.element.Element;
import org.ringingmaster.engine.touch.newcontainer.element.ElementBuilder;
import org.ringingmaster.util.smartcompare.SmartCompare;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class ObservableTouch {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private Touch currentTouch = new TouchBuilder().defaults().build();
    private final BehaviorSubject<Touch> subject = BehaviorSubject.create();
    private final SmartCompare s = new SmartCompare("", "> ")
            .comparePaths("numberOfBells")
            .comparePaths("startChange");

    Observable<Touch> observable() {
        return subject;
    }

    Touch get() {
        return currentTouch;
    }

    private void setCurrentTouch(Touch newTouch) {
        log.info("[{}] Touch diff [{}]", currentTouch.getTitle(), s.stringDifferences(currentTouch, newTouch));
        subject.onNext(newTouch);
        currentTouch = newTouch;
    }

    void setTitle(String title) {
        checkNotNull(title);

        if (Objects.equals(currentTouch.getTitle(), title)) {
            return;
        }

        TouchBuilder touchBuilder = new TouchBuilder().prototypeOf(currentTouch)
                .setTitle(title);

        setCurrentTouch(touchBuilder.build());
    }

    void setAuthor(String author) {
        checkNotNull(author);

        if (Objects.equals(currentTouch.getAuthor(), author)) {
            return;
        }

        log.debug("[{}] Set author [{}]", currentTouch.getTitle(), author);

        TouchBuilder touchBuilder = new TouchBuilder().prototypeOf(currentTouch)
                .setAuthor(author);

        setCurrentTouch(touchBuilder.build());
    }

    void setNumberOfBells(NumberOfBells numberOfBells) {
        checkNotNull(numberOfBells);

        if (Objects.equals(currentTouch.getNumberOfBells(), numberOfBells)) {
            return;
        }

        TouchBuilder touchBuilder = new TouchBuilder()
                .prototypeOf(currentTouch)
                .setNumberOfBells(numberOfBells);

        if (currentTouch.getCallFromBell().getZeroBasedBell() > numberOfBells.getTenor().getZeroBasedBell()) {
            touchBuilder.setCallFromBell(numberOfBells.getTenor());
        }

        final MethodRow existingStartChange = currentTouch.getStartChange();
        final MethodRow newStartChange = MethodBuilder.transformToNewNumberOfBells(existingStartChange, numberOfBells);
        touchBuilder.setStartChange(newStartChange);

        if (currentTouch.getTerminationChange().isPresent()) {
            final MethodRow existingTerminationRow = currentTouch.getTerminationChange().get();
            final MethodRow newTerminationRow = MethodBuilder.transformToNewNumberOfBells(existingTerminationRow, numberOfBells);
            touchBuilder.setTerminationChange(Optional.of(newTerminationRow));
        }

        if (!currentTouch.isSpliced() &&
                currentTouch.getNonSplicedActiveNotation().isPresent() &&
                currentTouch.getNonSplicedActiveNotation().get().getNumberOfWorkingBells().toInt() > numberOfBells.toInt()) {
            Optional<NotationBody> nextBestNonSplicedActiveNotation = findNextBestNonSplicedActiveNotation(currentTouch.getNonSplicedActiveNotation().get());
            touchBuilder.setNonSplicedActiveNotation(nextBestNonSplicedActiveNotation);
        }

        if (currentTouch.getStartNotation().isPresent()) {
            final String originalNotation = currentTouch.getStartNotation().get().getNotationDisplayString(false);
            NotationBody builtNotation = NotationBuilder.getInstance()
                    .setNumberOfWorkingBells(numberOfBells)
                    .setUnfoldedNotationShorthand(originalNotation)
                    .build();
            if (!originalNotation.equals(builtNotation.getNotationDisplayString(false))) {
                if (builtNotation.getRowCount() == 0) {
                    touchBuilder.setStartNotation(Optional.empty());
                } else {
                    touchBuilder.setStartNotation(Optional.of(builtNotation));
                }
            }
        }

        setCurrentTouch(touchBuilder.build());
    }

    private Optional<NotationBody> findNextBestNonSplicedActiveNotation(NotationBody previousNotation) {
        final List<NotationBody> validNotations = Lists.newArrayList(currentTouch.getValidNotations());
        validNotations.remove(previousNotation);

        Comparator<NumberOfBells> byDistanceFromPassedNumberOfBells = (o1, o2) -> ComparisonChain.start()
                .compare(Math.abs(previousNotation.getNumberOfWorkingBells().toInt() - o1.toInt()),
                        Math.abs(previousNotation.getNumberOfWorkingBells().toInt() - o2.toInt()))
                .compare(o2.toInt(), o1.toInt()) // always take higher number of bells where distance is equal
                .result();

        // from the validNotations, find all number of bells in use, sorted by distance from passed number of bells.
        Optional<NumberOfBells> bestNumberOfBells = validNotations.stream()
                .map(Notation::getNumberOfWorkingBells)
                .sorted(byDistanceFromPassedNumberOfBells)
                .findFirst();

        if (!bestNumberOfBells.isPresent()) {
            return Optional.empty();
        }

        // Try notations that are lexicographically the same or higher
        Optional<NotationBody> lexicographicallyHigher = validNotations.stream()
                .filter(notation -> notation.getNumberOfWorkingBells() == bestNumberOfBells.get())
                .filter(notation -> notation.getName().compareTo(previousNotation.getName()) >= 0)
                .sorted(Notation.BY_NAME)
                .findFirst();
        if (lexicographicallyHigher.isPresent()) {
            return lexicographicallyHigher;
        }

        // Try notations that are lexicographically lower
        Optional<NotationBody> lexicographicallyLower = validNotations.stream()
                .filter(notation -> notation.getNumberOfWorkingBells() == bestNumberOfBells.get())
                .filter(notation -> notation.getName().compareTo(previousNotation.getName()) < 0)
                .sorted(Notation.BY_NAME.reversed())
                .findFirst();
        if (lexicographicallyLower.isPresent()) {
            return lexicographicallyLower;
        }

        return Optional.empty();
    }

    public void addNotation(NotationBody notationToAdd) {
        checkNotNull(notationToAdd, "notation must not be null");

        List<String> messages = checkAddNotation(notationToAdd);

        if (messages.size() > 0) {
            String message = messages.stream().collect(Collectors.joining(System.lineSeparator()));
            throw new IllegalArgumentException("Can't add notation [" + notationToAdd + "]: " + System.lineSeparator() + message);
        }

        TouchBuilder touchBuilder = new TouchBuilder().prototypeOf(currentTouch);

        List<NotationBody> sortedNotations = Lists.newArrayList(currentTouch.getAllNotations());
        sortedNotations.add(notationToAdd);
        //TODO why are they sorted? Should be a set. Sorting for UI
        Collections.sort(sortedNotations, NotationBody.BY_NAME);
        touchBuilder.setSortedNotations(ImmutableList.copyOf(sortedNotations));

//TODO what if the number of bells is wrong?
        if (!currentTouch.isSpliced() && !currentTouch.getNonSplicedActiveNotation().isPresent()) {
            touchBuilder.setNonSplicedActiveNotation(Optional.of(notationToAdd));
        }

        setCurrentTouch(touchBuilder.build());
    }

    public List<String> checkAddNotation(NotationBody notationToAdd) {
        return checkPotentialNewNotation(notationToAdd, Collections.emptySet());
    }

    private List<String> checkPotentialNewNotation(NotationBody notationToCheck, Set<NotationBody> notationsToExclude) {
        List<String> messages = new ArrayList<>();
        Set<NotationBody> allNotationsWithExclusions = new HashSet<>(currentTouch.getAllNotations());
        allNotationsWithExclusions.removeAll(notationsToExclude);

        messages.addAll(allNotationsWithExclusions.stream()
                .filter(existingNotation -> (existingNotation.getNumberOfWorkingBells() == notationToCheck.getNumberOfWorkingBells()) &&
                        (com.google.common.base.Objects.equal(existingNotation.getName(), notationToCheck.getName())))
                .map(existingNotation -> "An existing method with notation '" + existingNotation.getNotationDisplayString(true) + "' has the same Name and Number Of Bells.")
                .collect(Collectors.toList()));

        messages.addAll(allNotationsWithExclusions.stream()
                .filter(existingNotation -> (!Strings.isNullOrEmpty(notationToCheck.getSpliceIdentifier()) &&
                        com.google.common.base.Objects.equal(existingNotation.getSpliceIdentifier(), notationToCheck.getSpliceIdentifier())))
                .map(existingNotation -> "An existing method '" + existingNotation.getNameIncludingNumberOfBells() + "' has the same Splice Identifier.")
                .collect(Collectors.toList()));

        messages.addAll(allNotationsWithExclusions.stream()
                .filter(existingNotation -> (existingNotation.getNumberOfWorkingBells() == notationToCheck.getNumberOfWorkingBells()) &&
                        com.google.common.base.Objects.equal(existingNotation.getNotationDisplayString(true), notationToCheck.getNotationDisplayString(true)))
                .map(existingNotation -> "An existing method '" + existingNotation.getNameIncludingNumberOfBells() + "' has the same Notation '" + notationToCheck.getNotationDisplayString(false) + "'.")
                .collect(Collectors.toList()));
        return messages;
    }

    public void removeNotation(NotationBody notationForRemoval) {
        checkNotNull(notationForRemoval, "notationForRemoval must not be null");

        List<NotationBody> sortedNotations = Lists.newArrayList(currentTouch.getAllNotations());
        checkState(sortedNotations.contains(notationForRemoval));

        TouchBuilder touchBuilder = new TouchBuilder().prototypeOf(currentTouch);

        sortedNotations.remove(notationForRemoval);
        touchBuilder.setSortedNotations(ImmutableList.copyOf(sortedNotations));

        // Sort out the next notation if it is the active notation
        if (currentTouch.getNonSplicedActiveNotation().isPresent() &&
                notationForRemoval.equals(currentTouch.getNonSplicedActiveNotation().get())) {
            Optional<NotationBody> nextBestNonSplicedActiveNotation = findNextBestNonSplicedActiveNotation(notationForRemoval);
            touchBuilder.setNonSplicedActiveNotation(nextBestNonSplicedActiveNotation);
        }

        setCurrentTouch(touchBuilder.build());
    }

    //TODO rename exchangeNotation
    public void updateNotation(NotationBody originalNotation, NotationBody replacementNotation) {
        checkNotNull(originalNotation, "originalNotation must not be null");
        checkNotNull(replacementNotation, "replacementNotation must not be null");
        checkState(originalNotation != replacementNotation);

        List<NotationBody> sortedNotations = Lists.newArrayList(currentTouch.getAllNotations());
        checkState(sortedNotations.contains(originalNotation));

        List<String> messages = checkUpdateNotation(originalNotation, replacementNotation);

        if (messages.size() > 0) {
            String message = messages.stream().collect(Collectors.joining(System.lineSeparator()));
            throw new IllegalArgumentException("Can't update notation [" + replacementNotation + "]: " + System.lineSeparator() + message);
        }

        TouchBuilder touchBuilder = new TouchBuilder().prototypeOf(currentTouch);

        sortedNotations.remove(originalNotation);
        sortedNotations.add(replacementNotation);
        Collections.sort(sortedNotations, NotationBody.BY_NAME);
        touchBuilder.setSortedNotations(ImmutableList.copyOf(sortedNotations));

        if (currentTouch.getNonSplicedActiveNotation().isPresent() &&
                currentTouch.getNonSplicedActiveNotation().get() == originalNotation) {
            if (replacementNotation.getNumberOfWorkingBells().toInt() > currentTouch.getNumberOfBells().toInt()) {
                Optional<NotationBody> nextBestNonSplicedActiveNotation = findNextBestNonSplicedActiveNotation(replacementNotation);
                touchBuilder.setNonSplicedActiveNotation(nextBestNonSplicedActiveNotation);
            } else {
                touchBuilder.setNonSplicedActiveNotation(Optional.of(replacementNotation));
            }
        }
        setCurrentTouch(touchBuilder.build());
    }

    //TODO rename exchangeNotation
    public List<String> checkUpdateNotation(NotationBody originalNotation, NotationBody replacementNotation) {
        return checkPotentialNewNotation(replacementNotation, Sets.<NotationBody>newHashSet(originalNotation));
    }

    public void setNonSplicedActiveNotation(NotationBody nonSplicedActiveNotation) {
        checkNotNull(nonSplicedActiveNotation);
        checkState(currentTouch.getAllNotations().contains(nonSplicedActiveNotation), "Can't set NonSplicedActiveNotation to notation not part of touch.");

        if (currentTouch.getNonSplicedActiveNotation().isPresent() &&
                Objects.equals(currentTouch.getNonSplicedActiveNotation().get(), nonSplicedActiveNotation)) {
            return;
        }

        TouchBuilder touchBuilder = new TouchBuilder().prototypeOf(currentTouch)
                .setNonSplicedActiveNotation(Optional.of(nonSplicedActiveNotation))
                .setSpliced(false);

        setCurrentTouch(touchBuilder.build());
    }

    public void setSpliced(boolean spliced) {

        if (Objects.equals(currentTouch.isSpliced(), spliced)) {
            return;
        }

        TouchBuilder touchBuilder = new TouchBuilder().prototypeOf(currentTouch)
                .setSpliced(spliced);


        if (spliced) {
            touchBuilder.setNonSplicedActiveNotation(Optional.empty());
        }
        else {
            final List<NotationBody> validNotations = currentTouch.getValidNotations();

            if (validNotations.size() > 0) {
                Optional<NotationBody> firstByName = validNotations.stream()
                        .sorted(NotationBody.BY_NAME)
                        .findFirst();
                touchBuilder.setNonSplicedActiveNotation(firstByName);
            }
        }

        setCurrentTouch(touchBuilder.build());
    }

    void setPlainLeadToken(String plainLeadToken) {
        checkNotNull(plainLeadToken);

        if (Objects.equals(currentTouch.getPlainLeadToken(), plainLeadToken)) {
            return;
        }

        TouchBuilder touchBuilder = new TouchBuilder().prototypeOf(currentTouch)
                .setPlainLeadToken(plainLeadToken);

        setCurrentTouch(touchBuilder.build());
    }

    void addDefinition(String shorthand, String characters) {
        checkNotNull(shorthand, "shorthand must not be null");
        checkNotNull(shorthand.length() > 0, "shorthand must contain some characters");

        // Check duplicate name
        if (currentTouch.findDefinitionByShorthand(shorthand).isPresent()) {
            throw new IllegalArgumentException("Can't add definition [" + shorthand + "] as it has a duplicate shorthand to existing definition [" + currentTouch.findDefinitionByShorthand(shorthand) + "]");
        }

        ImmutableList<Element> elements = ElementBuilder.createElements(characters);
        Definition definition = new Definition(shorthand, elements);

        Set<Definition> definitions = Sets.newHashSet(currentTouch.getAllDefinitions());
        definitions.add(definition);

        TouchBuilder touchBuilder = new TouchBuilder().prototypeOf(currentTouch)
                .setDefinitions(ImmutableSet.copyOf(definitions));

        setCurrentTouch(touchBuilder.build());
    }

    public void removeDefinition(String shorthand) {
        checkNotNull(shorthand, "shorthand must not be null");

        ImmutableSet<Definition> definitions = currentTouch.getAllDefinitions().stream()
                .filter(definition -> !definition.getShorthand().equals(shorthand))
                .collect(Collectors.collectingAndThen(Collectors.toSet(), ImmutableSet::copyOf));

        TouchBuilder touchBuilder = new TouchBuilder().prototypeOf(currentTouch)
                .setDefinitions(definitions);

        setCurrentTouch(touchBuilder.build());
    }

    public void setStartChange(MethodRow startChange) {
        checkNotNull(startChange);
        checkState(startChange.getNumberOfBells() == currentTouch.getNumberOfBells());

        if (Objects.equals(currentTouch.getStartChange(), startChange)) {
            return;
        }

        TouchBuilder touchBuilder = new TouchBuilder().prototypeOf(currentTouch)
                .setStartChange(startChange);

        setCurrentTouch(touchBuilder.build());
    }



}